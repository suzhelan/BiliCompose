package top.sacz.bili.biliplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.flow.StateFlow
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.compose.rememberMediampPlayer

/**
 * 音频流和视频流同步控制
 */
@Stable
class PlayerSyncController(
    val videoPlayer: MediampPlayer,
) {
    var visibility by mutableStateOf(PlayerToolBarVisibility.Visible)
        private set

    val playbackState: StateFlow<PlaybackState> get() = videoPlayer.playbackState

    fun updateVisibility(newVisibility: PlayerToolBarVisibility) {
        visibility = newVisibility
    }

    companion object {
        val headers = mutableMapOf(
            "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "referer" to "https://www.bilibili.com",
            //试过app端user-agent,但是仍然403,决定用web端了
        )
    }

    fun play(videoUrl: String, audioUrl: String) {
        PlayerMediaDataUtils.doLoadMediaData(videoPlayer, videoUrl, audioUrl)
    }

    /**
     * 停止播放
     */
    fun close() {
        videoPlayer.close()
    }

    /**
     * 播放
     */
    fun resume() {
        videoPlayer.resume()
    }

    /**
     * 暂停播放
     */
    fun pause() {
        videoPlayer.pause()
    }

    /**
     * 跳转到指定位置 毫秒
     */
    fun seekTo(position: Long) {
        videoPlayer.seekTo(position)
    }

    /**
     * 在原视频的基础上跳过多少毫秒
     * 可为正数或者负数
     */
    fun skipTo(deltaMillis: Long) {
        videoPlayer.skip(deltaMillis)
    }

    fun showOrHideToolBar() {
        visibility = if (visibility == PlayerToolBarVisibility.Visible) {
            PlayerToolBarVisibility.Invisible
        } else {
            PlayerToolBarVisibility.Visible
        }
    }

    fun playOrPause() {
        if (videoPlayer.playbackState.value.isPlaying) {
            videoPlayer.pause()
        } else {
            videoPlayer.resume()
        }
    }

}

@Immutable
data class PlayerToolBarVisibility(
    val topBar: Boolean,
    val bottomBar: Boolean,
    val floatingBottomEnd: Boolean,
    val rhsBar: Boolean,
    val gestureLock: Boolean,
    val detachedSlider: Boolean
) {
    companion object {
        @Stable
        val Visible = PlayerToolBarVisibility(
            topBar = true,
            bottomBar = true,
            floatingBottomEnd = false,
            rhsBar = true,
            gestureLock = true,
            detachedSlider = false,
        )

        @Stable
        val Invisible = PlayerToolBarVisibility(
            topBar = false,
            bottomBar = false,
            floatingBottomEnd = true,
            rhsBar = false,
            gestureLock = false,
            detachedSlider = false,
        )
    }
}


/**
 * 音频流和视频流合并
 */
expect object PlayerMediaDataUtils {
    fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    )
}


/**
 * 同步音频控制器
 * @return PlayerSyncController
 */
@Composable
fun rememberPlayerSyncController(): PlayerSyncController {
    val videoPlayer = rememberMediampPlayer()
    val controller = remember { PlayerSyncController(videoPlayer) }
    return controller
}