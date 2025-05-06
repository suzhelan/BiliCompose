package top.sacz.bili.biz.player.ui

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import top.sacz.bili.biz.player.player.VideoPlayerUI

class VideoPlayerScreen(val url: String) : Screen {
    override val key: ScreenKey
        get() = "/video"

    @Composable
    override fun Content() {
        VideoPlayerUI(url)
    }
}

