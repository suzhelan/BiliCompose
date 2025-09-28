package top.suzhelan.bili.biz.biliplayer.ui.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController


@Composable
fun rememberPlayerController(viewModel: VideoPlayerViewModel): PlayerSyncController {
    return remember { viewModel.controller }
}