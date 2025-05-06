package top.sacz.bili.biz.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.player.api.VideoPlayerApi
import top.sacz.bili.shared.common.logger.Logger

class VideoPlayerViewModel : ViewModel() {
    private val api = VideoPlayerApi()
    fun getVideoInfo(
        avid: String? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: String,
        qn: Int = 80
    ) = viewModelScope.launch {
        val data = apiCall {
            api.getVideoInfo(
                avid = avid,
                bvid = bvid,
                epid = epid,
                seasonId = seasonId,
                cid = cid,
                qn = qn
            )
        }
        Logger.d(data.toString())
    }
}