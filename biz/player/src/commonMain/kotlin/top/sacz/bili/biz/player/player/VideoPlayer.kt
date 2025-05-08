package top.sacz.bili.biz.player.player

import androidx.compose.runtime.Composable
import chaintech.videoplayer.host.MediaPlayerError
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.PlayerSpeed
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.shared.common.logger.Logger

/**
 * 视频播放器
 */
@Composable
fun VideoPlayerUI(playerHost: MediaPlayerHost) {
    VideoPlayerComposable(
        playerHost = playerHost
    )
}

fun getVideoPlayerHost(url: String, controller: PlayerSyncController): MediaPlayerHost {
    val videoPlayerHost = MediaPlayerHost(
        mediaUrl = url,
        isPaused = false,
        isMuted = false,
        initialSpeed = PlayerSpeed.X1,
        initialVideoFitMode = ScreenResize.FIT,
        isLooping = false,
        headers = mapOf(
            "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "referer" to "https://www.bilibili.com",
            //试过app端user-agent,但是仍然403,决定用web端了
        )
    )

    videoPlayerHost.onEvent = { event ->
        when (event) {
            is MediaPlayerEvent.MuteChange -> {
                println("Mute status changed: ${event.isMuted}")
            }

            is MediaPlayerEvent.PauseChange -> {
                println("Pause status changed: ${event.isPaused}")
            }

            is MediaPlayerEvent.BufferChange -> {
                println("Buffering status: ${event.isBuffering}")
            }

            is MediaPlayerEvent.CurrentTimeChange -> {
                Logger.d("当前视频播放时间: ${event.currentTime}")
                controller.updateVideoTime(event.currentTime)
            }

            is MediaPlayerEvent.TotalTimeChange -> {
                println("Video duration updated: ${event.totalTime}s")
            }

            is MediaPlayerEvent.FullScreenChange -> {
                println("FullScreen status changed: ${event.isFullScreen}")
            }

            MediaPlayerEvent.MediaEnd -> {
                println("Video playback ended")
            }
        }
    }

    videoPlayerHost.onError = { error ->
        when (error) {
            is MediaPlayerError.VlcNotFound -> {
                println("Error: VLC library not found. Please ensure VLC is installed.")
            }

            is MediaPlayerError.InitializationError -> {
                println("Initialization Error: ${error.details}")
            }

            is MediaPlayerError.PlaybackError -> {
                println("Playback Error: ${error.details}")
            }

            is MediaPlayerError.ResourceError -> {
                println("Resource Error: ${error.details}")
            }
        }
    }
    return videoPlayerHost
}