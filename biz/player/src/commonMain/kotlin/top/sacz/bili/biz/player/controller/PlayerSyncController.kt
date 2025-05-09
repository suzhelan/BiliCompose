package top.sacz.bili.biz.player.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.compose.rememberMediampPlayer
import org.openani.mediamp.source.UriMediaData

/**
 * 音频流和视频流同步控制
 */
class PlayerSyncController(
    val videoPlayer: MediampPlayer,
    val audioPlayer: MediampPlayer,
    private val scope: CoroutineScope
) {

    // 同步参数建议值
    private val SYNC_THRESHOLD = 50L // 允许的时间偏差阈值（毫秒）
    private val SYNC_INTERVAL = 100L // 同步检查间隔（毫秒）

    private var syncJob: Job? = null
    private val isActive get() = syncJob?.isActive ?: false

    val headers = mutableMapOf(
        "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
        "referer" to "https://www.bilibili.com",
        //试过app端user-agent,但是仍然403,决定用web端了
    )

    fun play(videoUrl: String, audioUrl: String) {
        scope.launch {
            videoPlayer.setMediaData(UriMediaData(videoUrl, headers))
            audioPlayer.setMediaData(UriMediaData(audioUrl, headers))
            sync()
        }
    }

    fun close() {
        videoPlayer.close()
        audioPlayer.close()
        syncJob?.cancel()
    }

    fun resume() {
        videoPlayer.resume()
        audioPlayer.resume()
        sync()
    }

    fun pause() {
        videoPlayer.pause()
        audioPlayer.pause()
        syncJob?.cancel()
    }

    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
        audioPlayer.seekTo(position)
    }


    private fun sync() {
        syncJob?.cancel() // 取消之前的同步任务

        syncJob = scope.launch {
            val videoTime = videoPlayer.currentPositionMillis
            val audioTime = audioPlayer.currentPositionMillis

            while (isActive) {
                // 获取当前时间戳
                val vPos = videoTime.value
                val aPos = audioTime.value

                // 计算时间差
                val diff = aPos - vPos

                when {
                    // 音频超前超过阈值
                    diff > SYNC_THRESHOLD -> {
                        audioPlayer.seekTo(vPos)
                    }
                    // 音频滞后超过阈值
                    diff < -SYNC_THRESHOLD -> {
                        audioPlayer.seekTo(vPos)
                    }
                }

                delay(SYNC_INTERVAL)
            }
        }
    }



}

/**
 * 同步音频控制器
 * @return PlayerSyncController
 */
@Composable
fun rememberPlayerSyncController(): PlayerSyncController {
    val videoPlayer = rememberMediampPlayer()
    val audioPlayer = rememberMediampPlayer()
    val coroutineScope = rememberCoroutineScope()
    val controller = PlayerSyncController(videoPlayer, audioPlayer, coroutineScope)
    return controller
}