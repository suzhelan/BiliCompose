package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.DoorBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.model.rememberScreenModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel


class VideoPlayerScreen(private val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video?$body"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel = rememberScreenModel(
            tag = body
        ) {
            VideoPlayerViewModel(body)
        }
        val playerParams = viewModel.playerArgs
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {
                        OutlinedIconButton(onClick = {
                            navigator.pop()
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.DoorBack,
                                contentDescription = "Back"
                            )
                        }
                    },
                    title = {
                        Text(text = "视频播放页")
                    }
                )
            }
        ) { paddingValues ->
            Column(
                modifier = Modifier.padding(paddingValues)
            ) {
                //播放器
                MediaUI(playerParams, viewModel)
                //视频信息
                VideoInfoUI(playerParams, viewModel)
            }
        }
    }
}


