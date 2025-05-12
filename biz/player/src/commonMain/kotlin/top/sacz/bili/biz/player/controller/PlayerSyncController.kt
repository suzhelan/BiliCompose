package top.sacz.bili.biz.player.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope
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


    val headers = mutableMapOf(
        "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
        "referer" to "https://www.bilibili.com",
        //试过app端user-agent,但是仍然403,决定用web端了
    )

    fun play(videoUrl: String, audioUrl: String) {
        scope.launch {
            videoPlayer.setMediaData(UriMediaData(videoUrl, headers))
            audioPlayer.setMediaData(UriMediaData(audioUrl, headers))
        }
    }

    fun close() {
        videoPlayer.close()
        audioPlayer.close()
    }

    fun resume() {
        videoPlayer.resume()
        audioPlayer.resume()
    }

    fun pause() {
        videoPlayer.pause()
        audioPlayer.pause()
    }

    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
        audioPlayer.seekTo(position)
    }
    
    private fun sync() {

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