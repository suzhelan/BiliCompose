package top.suzhelan.bili.biz.biliplayer.viewmodel

import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.biz.common.util.PlayerUtils
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliContext
import top.suzhelan.bili.shared.common.base.BaseViewModel
import kotlin.math.max


class VerticalVideoViewModel : BaseViewModel() {
    private val playerApi = VideoPlayerApi()
    private val infoApi = VideoInfoApi()
    private val userApi = UserApi()
    private val relationApi = RelationApi()

    val controllerList = mutableMapOf<Int, PlayerSyncController>()

    override fun onCleared() {
        super.onCleared()
        controllerList.values.forEach { it.close() }
        controllerList.clear()
    }

    fun getController(page: Int, context: BiliContext): PlayerSyncController = controllerList.getOrPut(page) { PlayerSyncController(context) }

    fun updateActivePage(activePage: Int?) {
        controllerList.forEach { (page, controller) ->
            if (page == activePage) {
                if (controller.isStarted) {
                    controller.resume()
                }
            } else {
                controller.pause()
            }
        }
    }

    fun updatePagePlayback(controller: PlayerSyncController, isActivePage: Boolean) {
        if (isActivePage) {
            if (controller.isStarted) {
                controller.resume()
            }
        } else {
            controller.pause()
        }
    }

    /**
     * 播放视频链接
     */
    private val _videoUrlList = MutableStateFlow<List<BiliResponse<PlayerArgsItem>>>(emptyList())
    val videoUrlList: StateFlow<List<BiliResponse<PlayerArgsItem>>> = _videoUrlList.asStateFlow()
    private val playerParams = mutableListOf<PlayerParams>()
    private val prepareVideoJobs = mutableMapOf<Int, Job>()
    private var isLoadingMore = false

    /**
     * 视频信息缓存 把信息和链接缓存 避免状态更新导致范围大影响播放器
     */
    private val _videoPool = MutableStateFlow<List<VerticalVideoWrap>>(emptyList())
    val videoPoolData: StateFlow<List<VerticalVideoWrap>> = _videoPool.asStateFlow()

    /**
     * 当前播放的视频
     */
    private val _currentIndex = MutableStateFlow(0)

    fun initData(param: PlayerParams) {
        if (_videoUrlList.value.isNotEmpty()) return
        appendVideo(param)
        // 推荐列表独立加载，首个视频补全参数或获取链接失败也能继续滑动。
        loadMoreData(param.aid ?: 0L)
    }

    private fun appendVideo(param: PlayerParams) {
        val page = playerParams.size
        playerParams += param
        // 每一页先占位，错误和重试不会改变页码或造成详情与播放链接错位。
        _videoPool.value += VerticalVideoWrap.empty("${page}_${param.aid}_${param.bvid}_${param.cid}")
        _videoUrlList.value += BiliResponse.Loading
        prepareVideo(page)
    }

    fun retryVideo(page: Int) {
        prepareVideo(page)
        if (_videoUrlList.value.size - _currentIndex.value <= 5) {
            loadMoreData()
        }
    }

    private fun prepareVideo(page: Int) {
        val param = playerParams.getOrNull(page) ?: return
        prepareVideoJobs[page]?.cancel()
        updateVideoUrl(page, BiliResponse.Loading)
        prepareVideoJobs[page] = launchTask {
            val preparedParams = apiCall {
                val cid = param.cid ?: PlayerUtils.getCidByAidOrBvid(param.aid, param.bvid)
                BiliResponse.buildSuccess(param.copy(cid = cid))
            }
            if (preparedParams is BiliResponse.Error) {
                updateVideoUrl(page, preparedParams)
                return@launchTask
            }
            val fixParam = preparedParams.getOrThrow()
            loadVideoInfo(_videoPool.value[page].id, fixParam.aid, fixParam.bvid, fixParam.cid)
            val playerArgs = apiCall {
                val response = playerApi.getPlayerInfo(
                    avid = fixParam.aid,
                    bvid = fixParam.bvid,
                    epid = fixParam.epid,
                    seasonId = fixParam.seasonId,
                    cid = requireNotNull(fixParam.cid),
                    qn = fixParam.qn
                )
                if (!response.isSuccess()) {
                    throw ApiException(
                        response.code,
                        response.message,
                        IllegalStateException("获取视频链接失败")
                    )
                }
                check(response.data.dash.video.any { it.baseUrl.isNotBlank() }) { "暂无可播放的视频链接" }
                response
            }
            updateVideoUrl(page, playerArgs)
        }
    }

    private fun updateVideoUrl(page: Int, state: BiliResponse<PlayerArgsItem>) {
        _videoUrlList.value = _videoUrlList.value.mapIndexed { index, item ->
            if (index == page) state else item
        }
    }

    private fun loadVideoInfo(
        id: String,
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
    ) {
        launchTask {
            val videoInfo = infoApi.getVideoDetails(
                aid = aid,
                bvid = bvid
            ).data
            updateVideoWrap(id) {
                it.copy(detailsInfo = videoInfo, isLoading = false)
            }
            loadAuthorInfo(id, videoInfo.owner.mid)
        }
        launchTask {
            val videoTags = infoApi.getVideoTags(
                aid = aid,
                bvid = bvid,
                cid = cid
            )
            updateVideoWrap(id) { it.copy(videoTags = videoTags.data) }
        }
        if (aid != null && cid != null) {
            launchTask {
                val onlineCountText = infoApi.getVideoOnlineCountText(
                    aid = aid,
                    cid = cid
                )
                updateVideoWrap(id) { it.copy(onlineCountText = onlineCountText.data.online.totalText) }
            }
        }
        launchTask {
            val isLike = infoApi.isLike(
                aid = aid,
                bvid = bvid
            )
            updateVideoWrap(id) { it.copy(isLike = isLike.data) }
        }
        if (aid != null) {
            launchTask {
                val isFavorite = infoApi.isFavoured(
                    aid = aid
                )
                updateVideoWrap(id) { it.copy(isFavorite = isFavorite.data) }
            }
        }
        launchTask {
            val coinQuotationCount = infoApi.isCoins(
                aid = aid,
                bvid = bvid
            )
            updateVideoWrap(id) { it.copy(coinQuotationCount = coinQuotationCount.data) }
        }
    }

    private fun updateVideoWrap(
        id: String,
        update: (VerticalVideoWrap) -> VerticalVideoWrap,
    ) {
        _videoPool.value = _videoPool.value.map { item ->
            if (item.id == id) {
                update(item)
            } else {
                item
            }
        }
    }

    private fun loadAuthorInfo(id: String, mid: Long) {
        if (mid == 0L) {
            return
        }
        launchTask {
            val userCard = userApi.getUserInfo(mid).data
            updateVideoWrap(id) {
                it.copy(
                    authorFollowerCount = userCard.follower,
                    isFollowingAuthor = userCard.following
                )
            }
        }
    }

    fun doPlayer(videoUrl: PlayerArgsItem, controller: PlayerSyncController) {
        if (controller.isStarted) {
            return
        }
        val allVideo = videoUrl.dash.video
        val audio = videoUrl.dash.audio
        val maxVideoUrl =
            allVideo.filter { it.baseUrl.isNotBlank() }.maxByOrNull { it.id } ?: return
        val maxAudioUrl = audio?.maxByOrNull { it.id }
        controller.play(maxVideoUrl.baseUrl, maxAudioUrl?.baseUrl ?: "")
    }

    fun likeVideo(videoId: String, aid: Long, like: Boolean) = launchTask {
        if (aid == 0L) {
            return@launchTask
        }
        val response = infoApi.like(aid = aid, isLike = like)
        if (!response.isSuccess()) {
            showMessageDialog("提示", response.message)
            return@launchTask
        }
        updateVideoWrap(videoId) { item ->
            val stat = item.detailsInfo.stat
            item.copy(
                isLike = like,
                detailsInfo = item.detailsInfo.copy(
                    stat = stat.copy(
                        like = max(0, stat.like + if (like) 1 else -1)
                    )
                )
            )
        }
    }

    fun favoriteChanged(videoId: String, favored: Boolean) {
        updateVideoWrap(videoId) { item ->
            if (item.isFavorite == favored) return@updateVideoWrap item
            val stat = item.detailsInfo.stat
            item.copy(
                isFavorite = favored,
                detailsInfo = item.detailsInfo.copy(
                    stat = stat.copy(
                        favorite = max(0, stat.favorite + if (favored) 1 else -1)
                    )
                )
            )
        }
    }

    fun addCoin(videoId: String, aid: Long, multiply: Int, selectLike: Boolean = false) = launchTask {
        if (aid == 0L) {
            return@launchTask
        }
        val current = _videoPool.value.firstOrNull { it.id == videoId }
        val currentCoinCount = current?.coinQuotationCount ?: 0
        if (currentCoinCount >= MAX_COIN_COUNT) {
            showMessageDialog("提示", "您已经投过币了,请勿重复投币")
            return@launchTask
        }
        if (currentCoinCount + multiply > MAX_COIN_COUNT) {
            showMessageDialog("提示", "单个视频最多只能投2枚硬币")
            return@launchTask
        }
        val response = infoApi.coin(aid = aid, multiply = multiply, selectLike = selectLike)
        if (!response.isSuccess()) {
            showMessageDialog("提示", response.message)
            return@launchTask
        }
        updateVideoWrap(videoId) { item ->
            item.copy(
                coinQuotationCount = item.coinQuotationCount + multiply
            )
        }
    }

    fun followAuthor(videoId: String, mid: Long, follow: Boolean) = launchTask {
        if (mid == 0L) {
            return@launchTask
        }
        updateVideoWrap(videoId) { it.copy(isAuthorActionLoading = true) }
        val response = relationApi.modify(mid = mid, action = if (follow) 1 else 2)
        if (response.isSuccess()) {
            updateVideoWrap(videoId) { item ->
                item.copy(
                    isFollowingAuthor = follow,
                    authorFollowerCount = item.authorFollowerCount?.let { count ->
                        max(0, count + if (follow) 1 else -1)
                    },
                    isAuthorActionLoading = false
                )
            }
        } else {
            updateVideoWrap(videoId) { it.copy(isAuthorActionLoading = false) }
            showMessageDialog("提示", response.message)
        }
    }

    fun onUpdate(index: Int) {
        _currentIndex.value = index
        if (_videoUrlList.value.size - index <= 5) {
            loadMoreData()
        }
    }

    private fun loadMoreData(
        aid: Long = 0L,
    ) {
        if (isLoadingMore) return
        isLoadingMore = true
        launchTask {
            try {
                val feed = apiCall { infoApi.getRecommendedVideosByVideo(aid = aid) }
                if (feed !is BiliResponse.Success || !feed.isSuccess()) return@launchTask
                for (item in feed.data.items) {
                    // 每条视频独立准备，一条失败不会中断后续视频加载。
                    appendVideo(
                        PlayerParams(
                            aid = item.playerArgs.aid,
                            bvid = item.bvid,
                            cid = item.playerArgs.cid
                        )
                    )
                }
            } finally {
                isLoadingMore = false
            }
        }
    }
}

private const val MAX_COIN_COUNT = 2
