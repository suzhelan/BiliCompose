package top.sacz.bili.biz.player.controller

import kotlin.math.abs

/**
 * 音频流和视频流同步
 */
class PlayerSyncController {
    var videoTime = 0
    var audioTime = 0

    fun updateVideoTime(time: Int) {
        videoTime = time
    }

    fun updateAudioTime(time: Int) {
        audioTime = time
    }

    fun checkSync(onSync: (Int) -> Unit) {
        val diff = audioTime - videoTime
        if (abs(diff) > 50) { // 超过50ms触发同步
            // 触发音频同步
            onSync(videoTime)
        }
    }

}