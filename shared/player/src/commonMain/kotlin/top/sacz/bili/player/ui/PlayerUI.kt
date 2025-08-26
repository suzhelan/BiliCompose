package top.sacz.bili.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.openani.mediamp.compose.MediampPlayerSurface
import top.sacz.bili.player.controller.PlayerSyncController
import top.sacz.bili.player.controller.PlayerToolBarVisibility
import top.sacz.bili.player.controller.rememberAudioLevelController
import top.sacz.bili.player.controller.rememberPlayerSyncController
import top.sacz.bili.player.ui.bottombar.PlayerBottomBar
import top.sacz.bili.player.ui.gesture.GestureHost
import top.sacz.bili.player.ui.indicator.AudioVisualIndicator
import top.sacz.bili.player.ui.indicator.BiliVideoLoadingIndicator
import top.sacz.bili.player.ui.indicator.BrightnessIndicator
import top.sacz.bili.player.ui.indicator.VolumeIndicator
import top.sacz.bili.player.ui.progress.PlayerProgressIndicator
import top.sacz.bili.player.ui.progress.rememberPlayerProgressSliderState
import top.sacz.bili.player.ui.video.VideoScaffold

@Composable
fun VideoPlayerUI(
    videoUrl: String,
    audioUrl: String,
) {
    val controller = rememberPlayerSyncController()
    controller.play(videoUrl, audioUrl)
    VideoPlayer(controller)
}

@Composable
private fun VideoPlayer(controller: PlayerSyncController) = Box(
    modifier = Modifier.fillMaxWidth().height(230.dp)
) {
    // 总时长
    val totalDurationMillis by controller.videoPlayer.mediaProperties.collectAsState()
    // 当前播放进度
    val currentPositionMillis by controller.videoPlayer.currentPositionMillis.collectAsState()
    // 添加一个用于跟踪Slider位置的状态
    val progressSliderState = rememberPlayerProgressSliderState(
        currentPositionMillis = {
            currentPositionMillis
        },
        totalDurationMillis = {
            totalDurationMillis?.durationMillis ?: 0L
        },
        onProView = {
        },
        onFinished = {
            controller.seekTo(it)
        }
    )
    val audioVisualState = rememberAudioLevelController()
    var indicatorType: AudioVisualIndicator by remember {
        mutableStateOf(AudioVisualIndicator.None)
    }
    VideoScaffold(
        playerSyncController = controller,
        floatMessageCenter = {
            //缓冲/错误指示器
            BiliVideoLoadingIndicator(
                modifier = Modifier.align(Alignment.Center),
                mediampPlayer = controller.videoPlayer
            )
            //亮度和音量调整指示器
            when (indicatorType) {
                AudioVisualIndicator.None -> {}
                AudioVisualIndicator.Brightness -> {
                    BrightnessIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        progress = {
                            audioVisualState.brightnessPercentage
                        }
                    )
                }

                AudioVisualIndicator.Volume -> {
                    VolumeIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        progress = {
                            audioVisualState.volumePercentage
                        }
                    )
                }
            }
        },
        video = {
            //视频实际组件
            MediampPlayerSurface(
                controller.videoPlayer,
                Modifier.fillMaxSize()
            )
        },
        bottomBar = {
            //底栏,暂停/播放,进度条,全屏
            PlayerBottomBar(
                progressSliderState = progressSliderState,
                controller = controller
            )
        },
        progressIndicator = {
            //进度条
            PlayerProgressIndicator(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                state = progressSliderState,
            )
        },
        gesture = {
            // 统一手势控制器，处理点击、双击和拖拽手势
            GestureHost(
                currentProgress = { progressSliderState.displayPositionRatio },
                onClick = {
                    controller.showOrHideToolBar()
                },
                onDoubleTap = {
                    controller.playOrPause()
                },
                onProgress = { progress ->
                    if (controller.visibility != PlayerToolBarVisibility.Visible) {
                        controller.updateVisibility(PlayerToolBarVisibility.Visible)
                    }
                    progressSliderState.previewPositionRatio(progress)
                },
                onProgressFinished = {
                    controller.updateVisibility(PlayerToolBarVisibility.Invisible)
                    progressSliderState.changeFinished()
                },
                currentVolume = {
                    audioVisualState.volumePercentage
                },
                onVolume = {
                    if (indicatorType != AudioVisualIndicator.Volume) {
                        indicatorType = AudioVisualIndicator.Volume
                    }
                    audioVisualState.setVolume(it)
                },
                onVolumeFinished = {
                    indicatorType = AudioVisualIndicator.None
                },
                currentBrightness = {
                    audioVisualState.brightnessPercentage
                },
                onBrightness = {
                    if (indicatorType != AudioVisualIndicator.Brightness) {
                        indicatorType = AudioVisualIndicator.Brightness
                    }
                    audioVisualState.setBrightness(it)
                },
                onBrightnessFinished = {
                    indicatorType = AudioVisualIndicator.None
                }
            )
        }
    )


}