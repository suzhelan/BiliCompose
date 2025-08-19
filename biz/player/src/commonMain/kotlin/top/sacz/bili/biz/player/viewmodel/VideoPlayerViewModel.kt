package top.sacz.bili.biz.player.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.player.api.VideoPlayerApi
import top.sacz.bili.biz.player.model.PlayerArgsItem
import top.sacz.bili.biz.player.model.VideoInfo
import top.sacz.bili.biz.player.model.VideoTag
import top.sacz.bili.shared.common.base.BaseViewModel

class VideoPlayerViewModel : BaseViewModel() {
    private val api = VideoPlayerApi()
    private val _data = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val video = _data.asStateFlow()
    fun getPlayerInfo(
        avid: String? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: String,
        qn: Int = 80
    ) = viewModelScope.launch {
        _data.value = apiCall {
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


    private val _videoInfo = MutableStateFlow<BiliResponse<VideoInfo>>(BiliResponse.Loading)
    val videoInfo = _videoInfo.asStateFlow()
    fun getVideoInfo(
        avid: String? = null,
        bvid: String? = null,
    ) = viewModelScope.launch {
        _videoInfo.value = BiliResponse.Loading
        _videoInfo.value = apiCall {
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