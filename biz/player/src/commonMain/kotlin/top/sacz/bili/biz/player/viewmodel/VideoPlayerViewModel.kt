package top.sacz.bili.biz.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.player.api.VideoPlayerApi
import top.sacz.bili.biz.player.model.PlayerArgsItem

class VideoPlayerViewModel : ViewModel() {
    private val api = VideoPlayerApi()
    private val _data = MutableStateFlow<BiliResponse<PlayerArgsItem>>(BiliResponse.Loading)
    val video = _data.asStateFlow()
    fun getVideoInfo(
        avid: String? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: String,
        qn: Int = 80
    ) = viewModelScope.launch {
        _data.value = apiCall {
            api.getVideoInfo(
                avid = avid,
                bvid = bvid,
                epid = epid,
                seasonId = seasonId,
                cid = cid,
                qn = qn
            )
        }

    }
}