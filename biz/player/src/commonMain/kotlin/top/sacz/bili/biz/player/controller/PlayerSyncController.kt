package top.sacz.bili.biz.player.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.compose.rememberMediampPlayer

/**
 * 音频流和视频流同步控制
 */
class PlayerSyncController(
    val videoPlayer: MediampPlayer,
    private val scope: CoroutineScope
) {
    companion object {
        val headers = mutableMapOf(
            "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "referer" to "https://www.bilibili.com",
            //试过app端user-agent,但是仍然403,决定用web端了
        )
    }

    fun play(videoUrl: String, audioUrl: String) {
        scope.launch {
            doLoadMediaData(videoPlayer, videoUrl, audioUrl)
        }
    }

    fun close() {
        videoPlayer.close()
    }

    fun resume() {
        videoPlayer.resume()
    }

    fun pause() {
        videoPlayer.pause()
    }

    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
    }

    private fun sync() {

    }

}

expect fun doLoadMediaData(
    mediampPlayer: MediampPlayer,
    videoUrl: String,
    audioUrl: String
)

/**
 * 同步音频控制器
 * @return PlayerSyncController
 */
@Composable
fun rememberPlayerSyncController(): PlayerSyncController {
    val videoPlayer = rememberMediampPlayer()
    val coroutineScope = rememberCoroutineScope()
    val controller = PlayerSyncController(videoPlayer, coroutineScope)
    return controller
}