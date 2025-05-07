package top.sacz.bili.biz.player.player

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import chaintech.videoplayer.host.MediaPlayerError
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.PlayerSpeed
import chaintech.videoplayer.model.ScreenResize
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import top.sacz.bili.api.config.commonHeaders

@Composable
fun VideoPlayerUI(url: String) {
    val playerHost = remember {
        getPlayerHost(url)
    }

    VideoPlayerComposable(
        modifier = Modifier.fillMaxSize(),
        playerHost = playerHost
    )
}

fun getPlayerHost(url: String): MediaPlayerHost {
    val videoPlayerHost = MediaPlayerHost(
        mediaUrl = url,
        isPaused = false,
        isMuted = false,
        initialSpeed = PlayerSpeed.X1,
        initialVideoFitMode = ScreenResize.FIT,
        isLooping = false,
        startTimeInSeconds = 10f,
        headers = commonHeaders
    )
    /*
    // Play the video
        videoPlayerHost.play()

    // Pause the video
        videoPlayerHost.pause()

    // Toggle mute
        videoPlayerHost.toggleMuteUnmute()

    // Seek to 30 seconds
        videoPlayerHost.seekTo(30f)

    // Change playback speed to 1.5x
        videoPlayerHost.setSpeed(PlayerSpeed.X1_5)

    // Enable looping
        videoPlayerHost.setLooping(true)

    // Enable Full screen
        videoPlayerHost.setFullScreen(true)*/

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
                println("Current playback time: ${event.currentTime}s")
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