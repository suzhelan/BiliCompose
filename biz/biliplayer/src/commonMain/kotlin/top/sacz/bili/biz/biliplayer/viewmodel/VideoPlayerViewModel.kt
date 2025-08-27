package top.sacz.bili.biz.biliplayer.viewmodel

import androidx.compose.runtime.mutableStateListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
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
        _recommendedVideo.clear()
        val api = VideoInfoApi()
        val result = api.getRecommendedVideosByVideo(aid)
        _recommendedVideo.addAll(result.data.items)
    }
}