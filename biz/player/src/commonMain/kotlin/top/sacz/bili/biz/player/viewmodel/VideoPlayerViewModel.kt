package top.sacz.bili.biz.player.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.player.api.VideoPlayerApi
import top.sacz.bili.biz.player.entity.PlayerArgsItem
import top.sacz.bili.biz.player.entity.VideoInfo
import top.sacz.bili.biz.player.entity.VideoTag
import top.sacz.bili.shared.common.base.BaseViewModel

class VideoPlayerViewModel : BaseViewModel() {
    private val api = VideoPlayerApi()
    private val _videoUrlData = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val videoUrlData = _videoUrlData.asStateFlow()
    fun getPlayerUrl(
        avid: String? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: String,
        qn: Int = 80
    ) = viewModelScope.launch {
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
        avid: String? = null,
        bvid: String? = null,
    ) = viewModelScope.launch {
        _videoDetailsInfo.value = BiliResponse.Loading
        _videoDetailsInfo.value = apiCall {
            api.getVideoDetails(
                avid = avid,
                bvid = bvid,
            )
        }
    }

    val onlineCountText = MutableStateFlow("")

    /**
     * 获取在线观看人数
     */
    fun getVideoOnlineCountText(
        aid: Long,
        cid: Long
    ) = launchTask {
        onlineCountText.value = api.getVideoOnlineCountText(
            aid = aid,
            cid = cid
        ).data.online.totalText
    }

    val videoTags = MutableStateFlow<List<VideoTag>>(listOf())

    /**
     * 获取视频标签
     */
    fun getVideoTags(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null,
    ) = launchTask {
        videoTags.value = api.getVideoTags(
            aid = aid,
            bvid = bvid,
            cid = cid
        ).data
    }

}