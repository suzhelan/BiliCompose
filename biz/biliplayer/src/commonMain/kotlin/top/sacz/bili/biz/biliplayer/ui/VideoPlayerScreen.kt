package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.shared.common.ui.CommonComposeUI
import top.sacz.bili.shared.common.ui.DefaultViewModel
import top.sacz.bili.shared.common.ui.dialog.DialogHandler


class VideoPlayerScreen(private val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video?$body"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
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


