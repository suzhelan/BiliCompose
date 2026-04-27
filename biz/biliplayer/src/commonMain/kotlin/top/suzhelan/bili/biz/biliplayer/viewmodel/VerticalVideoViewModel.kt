package top.suzhelan.bili.biz.biliplayer.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.base.util.PlayerUtils
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliContext
import top.suzhelan.bili.shared.common.base.BaseViewModel


class VerticalVideoViewModel : BaseViewModel() {
    private val playerApi = VideoPlayerApi()
    private val infoApi = VideoInfoApi()

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
    private val _videoPool = MutableStateFlow<MutableList<VerticalVideoWrap>>(mutableListOf())
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
        //假如没有缓存数据，则拼一个初始的进去
        _videoPool.value = _videoPool.value.apply {
            if (!any { it.id == id }) {
                add(VerticalVideoWrap.empty(id))
            }
        }
        updateVideoInfo(id) {
            val videoInfo = infoApi.getVideoDetails(
                aid = aid,
                bvid = bvid
            )
            it.copy(detailsInfo = videoInfo.data)
        }
        updateVideoInfo(id) {
            val videoTags = infoApi.getVideoTags(
                aid = aid,
                bvid = bvid,
                cid = cid
            )
            it.copy(videoTags = videoTags.data)
        }
        updateVideoInfo(id) {
            val onlineCountText = infoApi.getVideoOnlineCountText(
                aid = aid!!,
                cid = cid!!
            )
            it.copy(onlineCountText = onlineCountText.data.online.totalText)
        }
        updateVideoInfo(id) {
            val isLike = infoApi.isLike(
                aid = aid,
                bvid = bvid
            )
            it.copy(isLike = isLike.data)
        }
        updateVideoInfo(id) {
            val isFavorite = infoApi.isFavoured(
                aid = aid!!
            )
            it.copy(isFavorite = isFavorite.data)
        }
        updateVideoInfo(id) {
            val coinQuotationCount = infoApi.isCoins(
                aid = aid,
                bvid = bvid
            )
            it.copy(coinQuotationCount = coinQuotationCount.data)
        }
    }

    private fun updateVideoInfo(
        id: String,
        update: suspend (VerticalVideoWrap) -> VerticalVideoWrap,
    ) = launchTask {
        val rawList = _videoPool.value
        val newList = mutableListOf<VerticalVideoWrap>()
        for (item in rawList) {
            if (item.id == id) {
                newList.add(update(item))
            } else {
                newList.add(item)
            }
        }
        _videoPool.value = newList
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
