package top.sacz.bili.biliplayer.ui.video

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import top.sacz.bili.biliplayer.controller.PlayerSyncController

/**
 * 播放视频脚手架
 * @param playerSyncController 播放控制器
 * @param floatMessageCenter 例如缓冲中... 会显示到正中央
 * @param bottomBar 底部操作栏 操作模式下显示
 * @param progressIndicator 进度指示器 预览模式下显示
 * @param gesture 手势控制器
 */
@Composable
fun VideoScaffold(
    playerSyncController: PlayerSyncController,
    floatMessageCenter: @Composable BoxScope.() -> Unit,
    video: @Composable BoxScope.() -> Unit,
    bottomBar: @Composable BoxScope.() -> Unit,
    progressIndicator: @Composable BoxScope.() -> Unit,
    gesture: @Composable BoxWithConstraintsScope.() -> Unit
) = Box(modifier = Modifier.fillMaxSize()) {
    //是否展示工具栏
    val toolBarVisibility = playerSyncController.visibility

    //对于视频主界面 总是显示
    video()

    //缓冲中...
    Box(modifier = Modifier.align(Alignment.Center)) {
        floatMessageCenter()
    }
    //手势控制器 控制全屏的手势
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        gesture()
    }
    //进度条 + 操作栏
    Box(modifier = Modifier.align(Alignment.BottomCenter)) {
        if (toolBarVisibility.bottomBar) {
            bottomBar()
        } else {
            progressIndicator()
        }
    }


}