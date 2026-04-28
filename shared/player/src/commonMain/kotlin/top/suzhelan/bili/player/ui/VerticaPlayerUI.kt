package top.suzhelan.bili.player.ui

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.openani.mediamp.compose.MediampPlayerSurface
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.ui.indicator.BiliVideoLoadingIndicator
import top.suzhelan.bili.player.ui.indicator.GestureVisualIndicator
import top.suzhelan.bili.player.ui.indicator.OnPreviewIndicator
import top.suzhelan.bili.player.ui.progress.PlayerProgressIndicator
import top.suzhelan.bili.player.ui.progress.rememberPlayerProgressSliderState
import top.suzhelan.bili.player.ui.video.VideoScaffold


/**
 * 竖屏播放器UI
 * 和业务ui高度贴合的，所以需要考虑自定义嵌入而不是播放器提供控件
 */
@Composable
fun VerticalPlayerUI(
    controller: PlayerSyncController,
    showProgressIndicator: Boolean = true,
) {
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
    var indicatorType: GestureVisualIndicator by remember {
        mutableStateOf(GestureVisualIndicator.None)
    }
    VideoScaffold(
        playerSyncController = controller,
        topBar = {},
        floatMessageCenter = {
            //缓冲/错误指示器
            BiliVideoLoadingIndicator(
                modifier = Modifier.align(Alignment.Center),
                mediampPlayer = controller.videoPlayer
            )
            when (indicatorType) {
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

                else -> {}
            }
        },
        video = {
            //视频实际组件
            MediampPlayerSurface(
                controller.videoPlayer,
                Modifier.fillMaxSize()
            )
        },
        bottomBar = {},
        progressIndicator = {
            if (showProgressIndicator) {
                //进度条
                PlayerProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter),
                    state = progressSliderState,
                )
            }
        },
        gesture = {
            //手势几乎不怎么需要处理
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = {
                                controller.playOrPause()
                            }
                        )
                    }
            )
        },
        modifier = Modifier.fillMaxSize(),
    )
    //自动根据生命周期暂停后台播放
    AutoPaused(controller)
}

@Composable
private fun AutoPaused(playerSyncController: PlayerSyncController) {
    var pausedVideo by rememberSaveable { mutableStateOf(true) }
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (playerSyncController.isPlaying) {
                        pausedVideo = true
                        playerSyncController.pause()
                    } else {
                        pausedVideo = false // 明确表示不是自动暂停
                    }
                }

                Lifecycle.Event.ON_START -> {
                    if (pausedVideo) {
                        playerSyncController.resume()
                        pausedVideo = false
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}
