package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.shared.common.ui.CommonComposeUI
import top.sacz.bili.shared.common.ui.DefaultViewModel
import top.sacz.bili.shared.navigation.DialogHandler


class VideoPlayerScreen(private val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video?$body"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        CommonComposeUI<DefaultViewModel>(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        OutlinedIconButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = {
                        Text(text = "视频播放页")
                    }
                )
            }
        ) { _ ->
            val vm = rememberScreenModel {
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
        //视频信息
        VideoInfoUI(playerParams, viewModel)
    }
}


