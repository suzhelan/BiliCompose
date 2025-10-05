package top.suzhelan.bili.player.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.openani.mediamp.compose.MediampPlayerSurface
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.controller.PlayerToolBarVisibility
import top.suzhelan.bili.player.controller.rememberAudioLevelController
import top.suzhelan.bili.player.ui.bottombar.PlayerBottomBar
import top.suzhelan.bili.player.ui.gesture.GestureHost
import top.suzhelan.bili.player.ui.indicator.BiliVideoLoadingIndicator
import top.suzhelan.bili.player.ui.indicator.BrightnessIndicator
import top.suzhelan.bili.player.ui.indicator.GestureVisualIndicator
import top.suzhelan.bili.player.ui.indicator.OnPreviewIndicator
import top.suzhelan.bili.player.ui.indicator.VolumeIndicator
import top.suzhelan.bili.player.ui.progress.PlayerProgressIndicator
import top.suzhelan.bili.player.ui.progress.rememberPlayerProgressSliderState
import top.suzhelan.bili.player.ui.topbar.PlayerTopBar
import top.suzhelan.bili.player.ui.video.VideoScaffold


@Composable
fun VideoPlayer(controller: PlayerSyncController, modifier: Modifier = Modifier) {
    // 总时长
    val totalDurationMillis by controller.totalDurationMillis.collectAsStateWithLifecycle()
    // 当前播放进度
    val currentPositionMillis by controller.currentPositionMillis.collectAsStateWithLifecycle()
    // 添加一个用于跟踪Slider位置的状态
    val progressSliderState = rememberPlayerProgressSliderState(
        currentPositionMillis = {
            currentPositionMillis
        },
        totalDurationMillis = {
            totalDurationMillis
        },
        onPreView = {
        },
        onFinished = {
            controller.seekTo(it)
        }
    )
    val audioVisualState = rememberAudioLevelController()
    var indicatorType: GestureVisualIndicator by remember {
        mutableStateOf(GestureVisualIndicator.None)
    }
    VideoScaffold(
        playerSyncController = controller,
        modifier = modifier,
        topBar = {
            PlayerTopBar(controller = controller)
        },
        floatMessageCenter = {
            //缓冲/错误指示器
            BiliVideoLoadingIndicator(
                modifier = Modifier.align(Alignment.Center),
                mediampPlayer = controller.videoPlayer
            )
            //亮度和音量调整指示器
            when (indicatorType) {
                GestureVisualIndicator.None -> {}
                GestureVisualIndicator.Brightness -> {
                    BrightnessIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        progress = {
                            audioVisualState.brightnessPercentage
                        }
                    )
                }

                GestureVisualIndicator.Volume -> {
                    VolumeIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        progress = {
                            audioVisualState.volumePercentage
                        }
                    )
                }

                is GestureVisualIndicator.OnPreview -> {
                    val onPreview = indicatorType as GestureVisualIndicator.OnPreview
                    val current = onPreview.currentPositionMillis
                    val total = onPreview.totalDurationMillis
                    OnPreviewIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        progress = current,
                        total = total,
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
                    indicatorType = GestureVisualIndicator.OnPreview(
                        currentPositionMillis = (progress * totalDurationMillis).toLong(),
                        totalDurationMillis = totalDurationMillis
                    )
                    progressSliderState.previewPositionRatio(progress)
                },
                onProgressFinished = {
                    controller.updateVisibility(PlayerToolBarVisibility.Invisible)
                    indicatorType = GestureVisualIndicator.None
                    progressSliderState.changeFinished()
                },
                currentVolume = {
                    audioVisualState.volumePercentage
                },
                onVolume = {
                    if (indicatorType != GestureVisualIndicator.Volume) {
                        indicatorType = GestureVisualIndicator.Volume
                    }
                    audioVisualState.setVolume(it)
                },
                onVolumeFinished = {
                    indicatorType = GestureVisualIndicator.None
                },
                currentBrightness = {
                    audioVisualState.brightnessPercentage
                },
                onBrightness = {
                    if (indicatorType != GestureVisualIndicator.Brightness) {
                        indicatorType = GestureVisualIndicator.Brightness
                    }
                    audioVisualState.setBrightness(it)
                },
                onBrightnessFinished = {
                    indicatorType = GestureVisualIndicator.None
                }
            )
        }
    )


}