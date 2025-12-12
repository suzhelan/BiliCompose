package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.controller.rememberPlayerController
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.DefaultViewModel
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.ScreenSizeCalculation
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.isPhone


@Composable
fun VideoPlayerScreen(
  playerParams: PlayerParams
) {
    CommonComposeUI<DefaultViewModel>(
        viewModel = DefaultViewModel()
    ) { _ ->
        val context = BiliLocalContext.current
        val vm = viewModel {
            VideoPlayerViewModel(context)
        }
        DialogHandler(vm)
        PlayerUI(
            playerParams = playerParams,
            viewModel = vm
        )
    }
}


@Composable
private fun PlayerUI(
    playerParams: PlayerParams,
    viewModel: VideoPlayerViewModel,
) {
    var fixParam by remember {
        mutableStateOf(playerParams)
    }
    if (fixParam.cid == null) {
        LaunchedEffect(Unit) {
            fixParam = viewModel.getFixPlayerParam(fixParam)
        }
    }
    if (fixParam.cid == null) {
        LoadingIndicator(text = "获取视频信息中...")
    } else {
        val controller = rememberPlayerController(viewModel)
        ScreenSizeCalculation(modifier = Modifier.fillMaxSize()) { screenType ->
            if (screenType.isPhone()) {
                viewModel.controller.isFillMaxSize = false
                Column(modifier = Modifier.fillMaxSize()) {
                    MediaUI(fixParam, viewModel)
                    if (!controller.isFullScreen) {
                        VideoInfoUI(fixParam, viewModel)
                    }
                }
            } else {
                viewModel.controller.isFillMaxSize = true
                Row(modifier = Modifier.fillMaxSize()) {
                    MediaUI(fixParam, viewModel, modifier = Modifier.weight(2f))
                    if (!controller.isFullScreen) {
                        VideoInfoUI(fixParam, viewModel, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }


}