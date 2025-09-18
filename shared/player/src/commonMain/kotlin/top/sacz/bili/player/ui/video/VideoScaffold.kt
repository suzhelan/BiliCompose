package top.sacz.bili.player.ui.video

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.sacz.bili.player.controller.PlayerSyncController

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
    topBar: @Composable BoxScope.() -> Unit,
    floatMessageCenter: @Composable BoxScope.() -> Unit,
    video: @Composable BoxScope.() -> Unit,
    bottomBar: @Composable BoxScope.() -> Unit,
    progressIndicator: @Composable BoxScope.() -> Unit,
    gesture: @Composable BoxWithConstraintsScope.() -> Unit,
    modifier: Modifier
) = Box(modifier = modifier.background(color = Color.Black)) {
    //是否展示工具栏
    val toolBarVisibility = playerSyncController.visibility

    //对于视频主界面 总是显示
    video()

    val stateBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    val navigationBarPadding =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    //手势控制器 控制全屏的手势
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize().padding(
            top = stateBarPadding + 30.dp,
            bottom = navigationBarPadding
        )
    ) {
        gesture()
    }

    //缓冲中...
    Box(modifier = Modifier.align(Alignment.Center)) {
        floatMessageCenter()
    }
    //顶栏
    Box(
        modifier = Modifier.align(Alignment.TopStart)
    ) {
        if (toolBarVisibility.topBar) {
            topBar()
        }
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