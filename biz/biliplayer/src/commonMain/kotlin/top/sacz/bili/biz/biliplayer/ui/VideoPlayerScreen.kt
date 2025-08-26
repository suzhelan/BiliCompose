package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import top.sacz.bili.api.HttpJsonDecoder
import top.sacz.bili.biz.biliplayer.entity.PlayerParams


class VideoPlayerScreen(private val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val body: PlayerParams = HttpJsonDecoder.decodeFromString(body)

        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    navigationIcon = {

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
                MediaUI(body)
                //视频信息
                VideoInfoUI(body)
            }
        }

    }
}


