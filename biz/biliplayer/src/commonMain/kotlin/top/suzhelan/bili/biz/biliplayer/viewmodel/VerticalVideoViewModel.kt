package top.suzhelan.bili.biz.biliplayer.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.base.util.PlayerUtils
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
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
    private val _videoUrlList = MutableStateFlow<List<PlayerArgsItem>>(emptyList())
    val videoUrlList: StateFlow<List<PlayerArgsItem>> = _videoUrlList.asStateFlow()

    /**
     * 视频信息缓存 把信息和链接缓存 避免状态更新导致范围大影响播放器
     */
    private val _videoPool = MutableStateFlow<List<VerticalVideoWrap>>(emptyList())
    val videoPoolData: StateFlow<List<VerticalVideoWrap>> = _videoPool.asStateFlow()

    /**
     * 当前播放的视频
     */
    private val _currentIndex = MutableStateFlow(0)

    fun initData(param: PlayerParams) = launchTask {
        //校验数据是否完整 不完整走fix流程补全
        var fixParam = param
        if (fixParam.cid == null) {
            val cid = PlayerUtils.getCidByAidOrBvid(param.aid, param.bvid)
            fixParam = param.copy(cid = cid)
        }
        //然后可以获取初始的视频数据了
        val playerArgs = playerApi.getPlayerInfo(
            avid = fixParam.aid,
            bvid = fixParam.bvid,
            epid = fixParam.epid,
            seasonId = fixParam.seasonId,
            cid = fixParam.cid!!,
            qn = fixParam.qn
        )
        //直接添加到url数据列表,让ui立刻开始播放视频
        _videoUrlList.value += playerArgs.data
        loadVideoInfo(fixParam.aid, fixParam.bvid, fixParam.cid)

        //初始获取更多视频
        loadMoreData(fixParam.aid ?: 0L)
    }

    private fun loadVideoInfo(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
    ) {
        val id = "${aid}_${bvid}_${cid}"
        ensureVideoWrap(id)
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

    private fun ensureVideoWrap(id: String) {
        if (_videoPool.value.any { it.id == id }) {
            return
        }
        _videoPool.value = _videoPool.value + VerticalVideoWrap.empty(id)
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
        val maxVideoUrl = allVideo.maxBy { it.id }
        val maxAudioUrl = audio?.maxBy { it.id }
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
        loadMoreData()
    }

    private fun loadMoreData(
        aid: Long = 0L,
    ) = launchTask {
        //当前剩余大于五条
        val feed = infoApi.getRecommendedVideosByVideo(aid = aid)
        val items = feed.data.items
        for (item in items) {
            //先获取视频链接
            val playerArgs = playerApi.getPlayerInfo(
                avid = item.playerArgs.aid,
                cid = item.playerArgs.cid
            )
            _videoUrlList.value += playerArgs.data
            //然后获取视频信息
            loadVideoInfo(
                aid = item.playerArgs.aid,
                bvid = item.bvid,
                cid = item.playerArgs.cid
            )
        }
    }
}

private const val MAX_COIN_COUNT = 2
