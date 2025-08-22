package top.sacz.bili.biliplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.openani.mediamp.compose.MediampPlayerSurface
import top.sacz.bili.biliplayer.controller.PlayerSyncController
import top.sacz.bili.biliplayer.controller.PlayerToolBarVisibility
import top.sacz.bili.biliplayer.controller.rememberPlayerSyncController
import top.sacz.bili.biliplayer.ui.bottombar.PlayerBottomBar
import top.sacz.bili.biliplayer.ui.gesture.GestureHost
import top.sacz.bili.biliplayer.ui.loading.BiliVideoLoadingIndicator
import top.sacz.bili.biliplayer.ui.progress.PlayerProgressIndicator
import top.sacz.bili.biliplayer.ui.progress.rememberPlayerProgressSliderState
import top.sacz.bili.biliplayer.ui.video.VideoScaffold

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
    VideoScaffold(
        playerSyncController = controller,
        floatMessageCenter = {
            //加载指示器
            BiliVideoLoadingIndicator(
                modifier = Modifier.align(Alignment.Center),
                mediampPlayer = controller.videoPlayer
            )
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
                    0.5f
                },
                onVolume = {
                    // 音量控制
                    println("音量控制: $it")
                },
                currentBrightness = {
                    0.5f
                },
                onBrightness = {
                    // 亮度控制
                    println("亮度控制: $it")
                }
            )
        }
    )


}