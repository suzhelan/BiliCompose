package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.player.platform.BiliLocalContext
import top.sacz.bili.shared.common.ui.CommonComposeUI
import top.sacz.bili.shared.common.ui.DefaultViewModel
import top.sacz.bili.shared.common.ui.ScreenSizeCalculation
import top.sacz.bili.shared.common.ui.dialog.DialogHandler
import top.sacz.bili.shared.common.ui.isPhone


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


@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun PlayerUI(
    playerParams: PlayerParams,
    viewModel: VideoPlayerViewModel,
) {

    ScreenSizeCalculation(modifier = Modifier.fillMaxSize()) { screenType ->
        if (screenType.isPhone()) {
            Column(modifier = Modifier.fillMaxSize()) {
                MediaUI(playerParams, viewModel)
                VideoInfoUI(playerParams, viewModel)
            }
        } else {
            val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
            SupportingPaneScaffold(
                directive = scaffoldNavigator.scaffoldDirective,
                scaffoldState = scaffoldNavigator.scaffoldState,
                modifier = Modifier.fillMaxSize(),
                mainPane = {
                    Column(
                        modifier = Modifier
                    ) {
                        //播放器
                        MediaUI(playerParams, viewModel)
                        if (scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Hidden) {
                            VideoInfoUI(playerParams, viewModel)
                        } else {
                            viewModel.controller.isFillMaxSize = true
                        }
                    }
                },
                supportingPane = {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        VideoInfoUI(playerParams, viewModel)
                    }
                },
            )
        }
    }


}


