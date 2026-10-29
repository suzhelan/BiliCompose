package top.suzhelan.bili.biz.biliplayer.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.data.RecommendedVideoPagingSource
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.suzhelan.bili.biz.biliplayer.entity.VideoInfo
import top.suzhelan.bili.biz.biliplayer.entity.VideoTag
import top.suzhelan.bili.biz.biliplayer.ui.controller.PlayerPool
import top.suzhelan.bili.biz.common.util.PlayerUtils
import top.suzhelan.bili.biz.user.api.FavoriteApi
import top.suzhelan.bili.biz.user.api.WatchLaterApi
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliContext
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.base.BaseViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class VideoPlayerViewModel(
    private val context: BiliContext,
) : BaseViewModel() {

    //生成时间戳构建的随机数
    @OptIn(ExperimentalTime::class)
    private val playerId: String = Clock.System.now().epochSeconds.toString()

    private val playerApi = VideoPlayerApi()
    private val api = VideoInfoApi()
    private val watchLaterApi = WatchLaterApi()
    private val favoriteApi = FavoriteApi()
    private val collectionProgressRequests = Semaphore(2)
    private val collectionProgressCache = mutableMapOf<Triple<Long, Long, Long>, Long?>()

    private val _collectionFavorite = MutableStateFlow(CollectionFavoriteState())
    val collectionFavorite = _collectionFavorite.asStateFlow()
    private var collectionFavoriteJob: Job? = null
    private var collectionFavoriteTarget: Triple<Long?, Long, Long>? = null
    private var collectionFavoriteVersion = 0

    fun refreshCollectionFavorite(aid: Long, seasonId: Long) {
        val accountId = currentAccountId()
        val target = Triple(accountId, aid, seasonId)
        val sameTarget = collectionFavoriteTarget == target
        if (sameTarget && collectionFavoriteJob?.isActive == true) return
        collectionFavoriteJob?.cancel()
        val version = ++collectionFavoriteVersion
        collectionFavoriteTarget = target
        val previous = if (sameTarget) _collectionFavorite.value else CollectionFavoriteState(
            accountId = accountId,
            seasonId = seasonId,
        )
        _collectionFavorite.value = previous.copy(
            isLoading = accountId != null && previous.isFavorite == null,
            isSaving = false,
            error = null,
        )
        if (accountId == null || seasonId <= 0) return
        collectionFavoriteJob = launchTask {
            try {
                val favorite = favoriteApi.isCollectionFavorite(aid)
                if (isCurrentCollectionTarget(target, version)) {
                    _collectionFavorite.value =
                        _collectionFavorite.value.copy(isFavorite = favorite)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (isCurrentCollectionTarget(target, version)) {
                    _collectionFavorite.value = _collectionFavorite.value.copy(
                        error = exception.message ?: "合集收藏状态加载失败",
                    )
                }
            } finally {
                if (isCurrentCollectionTarget(target, version)) {
                    _collectionFavorite.value = _collectionFavorite.value.copy(isLoading = false)
                }
            }
        }
    }

    fun toggleCollectionFavorite(aid: Long, seasonId: Long) {
        val accountId = currentAccountId()
        if (accountId == null) {
            showMessageDialog(message = "请先登录后收藏合集")
            return
        }
        val target = Triple(accountId, aid, seasonId)
        val current = _collectionFavorite.value
        // 状态未知时先查询，不能将查询失败当成“未收藏”。
        if (collectionFavoriteTarget != target || current.isFavorite == null) {
            refreshCollectionFavorite(aid, seasonId)
            return
        }
        if (current.isLoading || current.isSaving || seasonId <= 0) return
        // 用户点击优先于静默查询，旧查询不能覆盖这次提交的结果。
        collectionFavoriteJob?.cancel()
        val version = ++collectionFavoriteVersion
        val favorite = !current.isFavorite
        _collectionFavorite.value = current.copy(isSaving = true, error = null)
        collectionFavoriteJob = launchTask {
            try {
                val response = favoriteApi.setCollectionFavorite(seasonId, favorite)
                if (response.code != 0) error(response.message)
                if (isCurrentCollectionTarget(target, version)) {
                    _collectionFavorite.value =
                        _collectionFavorite.value.copy(isFavorite = favorite)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (isCurrentCollectionTarget(target, version)) {
                    showMessageDialog(message = exception.message ?: "合集收藏操作失败，请重试")
                }
            } finally {
                if (isCurrentCollectionTarget(target, version)) {
                    _collectionFavorite.value = _collectionFavorite.value.copy(isSaving = false)
                }
            }
        }
    }

    private fun currentAccountId(): Long? =
        if (LoginMapper.isLogin()) LoginMapper.getMid() else null

    private fun isCurrentCollectionTarget(
        target: Triple<Long?, Long, Long>,
        version: Int,
    ): Boolean =
        collectionFavoriteVersion == version && collectionFavoriteTarget == target &&
                currentAccountId() == target.first

    fun refreshCollectionWatchProgress() {
        collectionProgressCache.clear()
    }

    /** 仅在列表项可见时读取进度，按账号和分P缓存，并限制并发请求。 */
    suspend fun getCollectionWatchProgress(aid: Long, cid: Long): Long? {
        if (!LoginMapper.isLogin() || aid <= 0 || cid <= 0) return null
        val accountId = LoginMapper.getMid()
        val key = Triple(accountId, aid, cid)
        return collectionProgressRequests.withPermit {
            if (!LoginMapper.isLogin() || LoginMapper.getMid() != accountId) return@withPermit null
            if (collectionProgressCache.containsKey(key)) {
                return@withPermit collectionProgressCache[key]
            }
            try {
                val progress = api.getWatchProgress(aid, cid)
                if (!LoginMapper.isLogin() || LoginMapper.getMid() != accountId) return@withPermit null
                // 上次播放可能来自另一分P，不能把它的时间绘制在当前封面上。
                val position = progress.lastPlayTime.takeIf {
                    progress.lastPlayCid == cid && it != 0L
                }
                collectionProgressCache[key] = position
                position
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // 进度为可选信息，失败时保留视频列表，重新打开可重试。
                null
            }
        }
    }

    private val _videoUrlData = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val videoUrlData = _videoUrlData.asStateFlow()

    private val _preparedPlayerParams =
        MutableStateFlow<BiliResponse<PlayerParams>>(BiliResponse.Wait)
    val preparedPlayerParams = _preparedPlayerParams.asStateFlow()
    private var preparePlayerJob: Job? = null
    private var requestedPlayerParams: PlayerParams? = null

    fun preparePlayer(params: PlayerParams) {
        // 返回当前播放页时沿用已准备好的参数，避免重新加载并重建播放器 UI。
        if (requestedPlayerParams == params &&
            (_preparedPlayerParams.value is BiliResponse.Success || preparePlayerJob?.isActive == true)
        ) {
            return
        }
        requestedPlayerParams = params
        // 重试或切换视频时取消旧请求，避免旧结果覆盖当前状态。
        preparePlayerJob?.cancel()
        _preparedPlayerParams.value = BiliResponse.Loading
        preparePlayerJob = launchTask {
            _preparedPlayerParams.value = apiCall {
                BiliResponse.buildSuccess(getFixPlayerParam(params))
            }
        }
    }

    val controller: PlayerSyncController
        get() {
            if (!PlayerPool.has(playerId)) {
                PlayerPool.create(playerId, context)
            }
            return PlayerPool.get(playerId)
        }

    override fun onCleared() {
        super.onCleared()
        PlayerPool.remove(playerId)
    }

    suspend fun getFixPlayerParam(playerParams: PlayerParams): PlayerParams {
        //检查是否有cid
        if (playerParams.cid != null) {
            return playerParams
        }
        //没有则根据aid/bvid获取cid
        val cid = PlayerUtils.getCidByAidOrBvid(playerParams.aid, playerParams.bvid)
        return playerParams.copy(cid = cid)
    }

    fun getPlayerUrl(
        avid: Long? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: Long,
        qn: Int = 80,
    ) = launchTask {
        if (_videoUrlData.value is BiliResponse.Success) {
            return@launchTask
        }
        _videoUrlData.value = apiCall {
            playerApi.getPlayerInfo(
                avid = avid,
                bvid = bvid,
                epid = epid,
                seasonId = seasonId,
                cid = cid,
                qn = qn
            )
        }
    }

    fun doPlayer(controller: PlayerSyncController) {
        if (controller.isStarted) {
            return
        }
        val video = _videoUrlData.value.getOrThrow()
        val allVideo = video.dash.video
        val audio = video.dash.audio
        val maxVideoUrl = allVideo.maxBy { it.id }
        val maxAudioUrl = audio?.maxBy { it.id }
        controller.play(maxVideoUrl.baseUrl, maxAudioUrl?.baseUrl ?: "")
        // 历史进度只在播放器首次启动时恢复，任务随 ViewModel 保留，返回页面不会再次跳转。
        launchTask {
            val totalDurationMillis = controller.totalDurationMillis.first { it > 0 }
            if (video.lastPlayTime > 0 && video.lastPlayTime < totalDurationMillis - 500) {
                controller.seekTo(video.lastPlayTime)
            }
        }
    }


    private val _videoDetailsInfo = MutableStateFlow<BiliResponse<VideoInfo>>(BiliResponse.Loading)
    val videoDetailsInfo = _videoDetailsInfo.asStateFlow()
    fun getVideoDetailsInfo(
        avid: Long? = null,
        bvid: String? = null,
    ) = launchTask {
        if (_videoDetailsInfo.value is BiliResponse.Success) {
            return@launchTask
        }
        _videoDetailsInfo.value = BiliResponse.Loading
        _videoDetailsInfo.value = apiCall {
            api.getVideoDetails(
                aid = avid,
                bvid = bvid,
            )
        }
    }

    private val _onlineCountText = MutableStateFlow("")
    val onlineCountText = _onlineCountText.asStateFlow()

    /**
     * 获取在线观看人数
     */
    fun getVideoOnlineCountText(
        aid: Long,
        cid: Long,
    ) = launchTask {
        if (_onlineCountText.value.isNotEmpty()) {
            return@launchTask
        }
        _onlineCountText.value = api.getVideoOnlineCountText(
            aid = aid,
            cid = cid
        ).data.online.totalText
    }


    private val _videoTags = MutableStateFlow<List<VideoTag>>(listOf())
    val videoTags = _videoTags.asStateFlow()

    /**
     * 获取视频标签
     */
    fun getVideoTags(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
    ) = launchTask {
        if (_videoTags.value.isNotEmpty()) {
            return@launchTask
        }
        _videoTags.value = api.getVideoTags(
            aid = aid,
            bvid = bvid,
            cid = cid
        ).data
    }

    private val recommendedVideoFlowMap =
        mutableMapOf<Long, Flow<PagingData<RecommendedVideoByVideo.Item>>>()

    fun getRecommendedVideoFlow(
        aid: Long,
    ): Flow<PagingData<RecommendedVideoByVideo.Item>> {
        return recommendedVideoFlowMap.getOrPut(aid) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    RecommendedVideoPagingSource(aid)
                }
            ).flow.cachedIn(viewModelScope)
        }
    }

    val operationState = MutableStateFlow<ActionState>(ActionState.None)

    //是否点赞
    val isLike = MutableStateFlow(false)

    //是否投币
    val coinQuotationCount = MutableStateFlow(-1)

    //是否收藏
    val isFavorite = MutableStateFlow(false)
    val isShowFavoriteDialog = MutableStateFlow(false)

    private val _isInWatchLater = MutableStateFlow<Boolean?>(null)
    val isInWatchLater = _isInWatchLater.asStateFlow()
    private val _isWatchLaterBusy = MutableStateFlow(false)
    val isWatchLaterBusy = _isWatchLaterBusy.asStateFlow()

    fun updateWatchLaterState(aid: Long) = launchTask {
        if (_isWatchLaterBusy.value) return@launchTask
        _isInWatchLater.value = null
        if (!LoginMapper.isLogin()) return@launchTask
        _isWatchLaterBusy.value = true
        try {
            _isInWatchLater.value = queryWatchLaterState(aid)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            showMessageDialog(message = exception.message ?: "稍后再看状态加载失败，请点击按钮重试")
        } finally {
            _isWatchLaterBusy.value = false
        }
    }

    fun toggleWatchLater(aid: Long) = launchTask {
        if (_isWatchLaterBusy.value) return@launchTask
        if (!LoginMapper.isLogin()) {
            showMessageDialog(message = "请先登录后使用稍后再看")
            return@launchTask
        }
        _isWatchLaterBusy.value = true
        try {
            // 初始化失败时先重查，避免将已有的视频错误地当作新增。
            val wasAdded = _isInWatchLater.value ?: queryWatchLaterState(aid)
            val response = if (wasAdded) watchLaterApi.remove(aid) else watchLaterApi.add(aid)
            if (response.code != 0) {
                error(
                    when (response.code) {
                        90001 -> "稍后再看列表已满，请先移除部分视频"
                        90003 -> "视频已被删除，无法添加到稍后再看"
                        else -> response.message
                    }
                )
            }
            _isInWatchLater.value = !wasAdded
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            showMessageDialog(message = exception.message ?: "稍后再看操作失败，请重试")
        } finally {
            _isWatchLaterBusy.value = false
        }
    }

    private suspend fun queryWatchLaterState(aid: Long): Boolean {
        val response = watchLaterApi.getList()
        if (response.code != 0) error(response.message)
        val data = response.data ?: error("稍后再看列表数据为空")
        return data.list.orEmpty().any { it.aid == aid }
    }

    fun updateUserActionState(
        aid: Long,
    ) = launchTask {
        operationState.value = ActionState.AllLoading
        updateLikeWait(aid)
        updateCoinQuotationWait(aid)
        updateFavoriteWait(aid)
        operationState.value = ActionState.None
    }

    private suspend fun updateLikeWait(aid: Long) {

        isLike.value = api.isLike(aid = aid).data
    }

    private suspend fun updateCoinQuotationWait(aid: Long) {

        coinQuotationCount.value = api.isCoins(aid = aid).data
    }

    private suspend fun updateFavoriteWait(aid: Long) {

        isFavorite.value = api.isFavoured(aid = aid).data
    }

    fun like(
        aid: Long,
        like: Boolean,
    ) = launchTask {
        if (operationState.value == ActionState.Like) {
            return@launchTask
        }
        operationState.value = ActionState.Like

        val response = api.like(aid = aid, isLike = like)
        if (response.isSuccess()) {
            isLike.value = like
            val currentDetails = _videoDetailsInfo.value
            if (currentDetails is BiliResponse.Success) {
                val videoInfo = currentDetails.data
                val likeDelta = if (like) 1 else -1
                _videoDetailsInfo.value = currentDetails.copy(
                    data = videoInfo.copy(
                        stat = videoInfo.stat.copy(
                            like = maxOf(0, videoInfo.stat.like + likeDelta)
                        )
                    )
                )
            }
        }
        operationState.value = ActionState.None
    }

    fun favoriteChanged(favored: Boolean) {
        val wasFavored = isFavorite.value
        isFavorite.value = favored
        if (wasFavored == favored) return
        val current = _videoDetailsInfo.value
        if (current is BiliResponse.Success) {
            current.data.let { video ->
                _videoDetailsInfo.value = current.copy(
                    data = video.copy(
                        stat = video.stat.copy(
                            favorite = maxOf(0, video.stat.favorite + if (favored) 1 else -1)
                        )
                    )
                )
            }
        }
    }

    val isShowAddCoinDialog = MutableStateFlow(false)

    fun addCoin(
        aid: Long,
        multiply: Int,
        selectLike: Boolean = false,
    ) = launchTask {
        if (operationState.value == ActionState.Coin) {
            return@launchTask
        }
        operationState.value = ActionState.Coin

        val response = api.coin(aid = aid, multiply = multiply, selectLike = selectLike)
        if (response.isSuccess()) {
            coinQuotationCount.value += multiply
        } else {
            showMessageDialog("提示", response.message)
        }
        operationState.value = ActionState.None
    }

    /**
     * 报告播放进度
     */
    fun reportViewingProgress(
        aid: Long,
        cid: Long,
        seconds: Long,
    ) = launchTask {
        api.reportViewingProgress(aid = aid, cid = cid, seconds = seconds)
    }
}

data class CollectionFavoriteState(
    val accountId: Long? = null,
    val seasonId: Long = 0,
    val isFavorite: Boolean? = null,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

sealed class ActionState {
    object Like : ActionState()
    object Coin : ActionState()
    object Favorite : ActionState()
    object AllLoading : ActionState()
    object None : ActionState()
}
