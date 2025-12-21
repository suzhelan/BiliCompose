package top.suzhelan.bili.biz.biliplayer.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.base.util.PlayerUtils
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.suzhelan.bili.biz.biliplayer.entity.VideoInfo
import top.suzhelan.bili.biz.biliplayer.entity.VideoTag
import top.suzhelan.bili.biz.biliplayer.ui.controller.PlayerPool
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliContext
import top.suzhelan.bili.shared.common.base.BaseViewModel
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class VideoPlayerViewModel(
    private val context: BiliContext
) : BaseViewModel() {

    //生成时间戳构建的随机数
    @OptIn(ExperimentalTime::class)
    private val playerId: String = Clock.System.now().epochSeconds.toString()

    private val playerApi = VideoPlayerApi()
    private val api = VideoInfoApi()
    private val _videoUrlData = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val videoUrlData = _videoUrlData.asStateFlow()

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
        controller.close() // 释放播放器资源
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
        qn: Int = 80
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
        if (controller.isPlaying) {
            return
        }
        val video = _videoUrlData.value.getOrThrow()
        val allVideo = video.dash.video
        val audio = video.dash.audio
        val maxVideoUrl = allVideo.maxBy { it.id }
        val maxAudioUrl = audio?.maxBy { it.id }
        controller.play(maxVideoUrl.baseUrl, maxAudioUrl?.baseUrl ?: "")
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
        cid: Long
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

    private val _recommendedVideo: MutableList<RecommendedVideoByVideo.Item> = mutableStateListOf()
    val recommendedVideo: List<RecommendedVideoByVideo.Item> = _recommendedVideo

    /**
     * 获取推荐的视频列表
     */
    fun getRecommendedVideoByVideo(
        aid: Long
    ) = launchTask {
        if (_recommendedVideo.isNotEmpty()) {
            return@launchTask
        }
        while (_recommendedVideo.size < 50) {
            val result = api.getRecommendedVideosByVideo(aid)
            _recommendedVideo.addAll(result.data.items)
        }
    }

    val operationState = MutableStateFlow<ActionState>(ActionState.None)

    //是否点赞
    val isLike = MutableStateFlow(false)

    //是否投币
    val coinQuotationCount = MutableStateFlow(-1)

    //是否收藏
    val isFavorite = MutableStateFlow(false)
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
        like: Boolean
    ) = launchTask {
        if (operationState.value == ActionState.Like) {
            return@launchTask
        }
        operationState.value = ActionState.Like

        val response = api.like(aid = aid, isLike = like)
        if (response.isSuccess()) {
            isLike.value = like
        }
        operationState.value = ActionState.None
    }

    val isShowAddCoinDialog = MutableStateFlow(false)

    fun addCoin(
        aid: Long,
        multiply: Int,
        selectLike: Boolean = false
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
        seconds: Long
    ) = launchTask {
        api.reportViewingProgress(aid = aid, cid = cid, seconds = seconds)
    }
}

sealed class ActionState {
    object Like : ActionState()
    object Coin : ActionState()
    object Favorite : ActionState()
    object AllLoading : ActionState()
    object None : ActionState()
}