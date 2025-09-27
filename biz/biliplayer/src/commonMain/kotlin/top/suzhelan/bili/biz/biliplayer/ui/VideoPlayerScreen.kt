package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.DefaultViewModel
import top.suzhelan.bili.shared.common.ui.ScreenSizeCalculation
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.isPhone


@Composable
fun VideoPlayerScreen(body: String) {
    CommonComposeUI<DefaultViewModel>(
        viewModel = { DefaultViewModel() }
    ) { _ ->
        val context = BiliLocalContext.current
        val vm = viewModel {
            VideoPlayerViewModel(context)
        }
        DialogHandler(vm)
        PlayerUI(
            playerParams = PlayerParams.fromJson(body),
            viewModel = vm
        )
    }
}


@Composable
private fun PlayerUI(
    playerParams: PlayerParams,
    viewModel: VideoPlayerViewModel,
) {

    ScreenSizeCalculation(modifier = Modifier.fillMaxSize()) { screenType ->
        if (screenType.isPhone()) {
            viewModel.controller.isFillMaxSize = false
            Column(modifier = Modifier.fillMaxSize()) {
                MediaUI(playerParams, viewModel)
                VideoInfoUI(playerParams, viewModel)
            }
        } else {
            viewModel.controller.isFillMaxSize = true
            Row(modifier = Modifier.fillMaxSize()){
                MediaUI(playerParams, viewModel, modifier = Modifier.weight(2f))
                Column(modifier = Modifier.weight(1f)){
                    VideoInfoUI(playerParams, viewModel)
                }
            }
        }
    }


}


