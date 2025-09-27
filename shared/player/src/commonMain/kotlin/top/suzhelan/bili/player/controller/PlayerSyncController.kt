package top.suzhelan.bili.player.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.compose.rememberMediampPlayer
import top.suzhelan.bili.player.platform.BiliContext
import top.suzhelan.bili.player.platform.BiliLocalContext

/**
 * 音频流和视频流同步控制
 * 设计原则
 * UI控制用State(mutableStateOf)
 * 数据订阅用Flow(StateFlow)
 */
@Stable
class PlayerSyncController(
    val context: BiliContext
) {
    val videoPlayer: MediampPlayer = MediampPlayer(context)
    var visibility by mutableStateOf(PlayerToolBarVisibility.Visible)
        private set

    var isFullScreen by mutableStateOf(false)
        private set

    var isFillMaxSize by mutableStateOf(false)

    val playbackState: StateFlow<PlaybackState> get() = videoPlayer.playbackState

    val isPlaying: Boolean get() = videoPlayer.playbackState.value.isPlaying

    //当前播放时长
    val currentPositionMillis: StateFlow<Long> get() = videoPlayer.currentPositionMillis

    /**
     * 总时长 没有获取到视频时长时为0L
     */
    val totalDurationMillis: StateFlow<Long> = MutableStateFlow(0L).apply {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
        scope.launch {
            videoPlayer.mediaProperties.collect {
                value = (it?.durationMillis ?: 0)
            }
        }
    }

    fun updateVisibility(newVisibility: PlayerToolBarVisibility) {
        visibility = newVisibility
    }

    companion object {
        val headers = mutableMapOf(
            "User-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "Referer" to "https://www.bilibili.com",
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


    fun reversalFullScreen() {
        isFullScreen = !isFullScreen
    }


    var onBack: () -> Unit = {}

}

/**
 * 播放器工具栏显示状态
 * @param topBar 顶部工具栏,包含 返回,标题,时间等
 * @param bottomBar 底部工具栏,包含 播放/暂停,进度条,全屏按钮等
 * @param gestureLock 手势锁定 如果锁定则不允许手势操作
 */
@Immutable
data class PlayerToolBarVisibility(
    val topBar: Boolean,
    val bottomBar: Boolean,
    val gestureLock: Boolean,
) {

    companion object {
        @Stable
        val Visible = PlayerToolBarVisibility(
            topBar = true,
            bottomBar = true,
            gestureLock = true,
        )

        @Stable
        val Invisible = PlayerToolBarVisibility(
            topBar = false,
            bottomBar = false,
            gestureLock = false,
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

    fun setFullScreen(context: BiliContext, fullScreen: Boolean)
}


/**
 * 同步音频控制器
 * @return PlayerSyncController
 */
@Composable
fun rememberPlayerSyncController(
): PlayerSyncController {
    val context = BiliLocalContext.current
    val videoPlayer = rememberMediampPlayer()
    val controller = remember { PlayerSyncController(context) }
    return controller
}