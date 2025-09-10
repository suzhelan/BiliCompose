package top.sacz.bili.biz.biliplayer.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.api.isSuccess
import top.sacz.bili.biz.biliplayer.api.VideoInfoApi
import top.sacz.bili.biz.biliplayer.api.VideoPlayerApi
import top.sacz.bili.biz.biliplayer.entity.PlayerArgsItem
import top.sacz.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.sacz.bili.biz.biliplayer.entity.VideoInfo
import top.sacz.bili.biz.biliplayer.entity.VideoTag
import top.sacz.bili.shared.common.base.BaseViewModel

class VideoPlayerViewModel(
) : BaseViewModel() {

    private val api = VideoPlayerApi()
    private val _videoUrlData = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val videoUrlData = _videoUrlData.asStateFlow()
    fun getPlayerUrl(
        avid: Long? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: Long,
        qn: Int = 80
    ) = launchTask {
        _videoUrlData.value = apiCall {
            api.getPlayerInfo(
                avid = avid,
                bvid = bvid,
                epid = epid,
                seasonId = seasonId,
                cid = cid,
                qn = qn
            )
        }
    }


    private val _videoDetailsInfo = MutableStateFlow<BiliResponse<VideoInfo>>(BiliResponse.Loading)
    val videoDetailsInfo = _videoDetailsInfo.asStateFlow()
    fun getVideoDetailsInfo(
        avid: Long? = null,
        bvid: String? = null,
    ) = launchTask {
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
        val api = VideoInfoApi()
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
        val api = VideoInfoApi()
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
        val api = VideoInfoApi()
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
        val api = VideoInfoApi()
        isLike.value = api.isLike(aid = aid).data
    }

    private suspend fun updateCoinQuotationWait(aid: Long) {
        val api = VideoInfoApi()
        coinQuotationCount.value = api.isCoins(aid = aid).data
    }

    private suspend fun updateFavoriteWait(aid: Long) {
        val api = VideoInfoApi()
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
        val api = VideoInfoApi()
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
        val api = VideoInfoApi()
        val response = api.coin(aid = aid, multiply = multiply, selectLike = selectLike)
        if (response.isSuccess()) {
            coinQuotationCount.value += multiply
        } else {
            showMessageDialog("提示", response.message)
        }
        operationState.value = ActionState.None
    }
}

sealed class ActionState {
    object Like : ActionState()
    object Coin : ActionState()
    object Favorite : ActionState()
    object AllLoading : ActionState()
    object None : ActionState()
}