package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.shared.common.ui.CommonComposeUI
import top.sacz.bili.shared.common.ui.DefaultViewModel
import top.sacz.bili.shared.common.ui.dialog.DialogHandler


@Composable
fun VideoPlayerScreen(body: String) {
    CommonComposeUI<DefaultViewModel> { _ ->
        val vm = viewModel {
            VideoPlayerViewModel()
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
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        //播放器
        MediaUI(playerParams, viewModel)
        VideoInfoUI(playerParams, viewModel)
    }
}


