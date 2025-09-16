package top.sacz.bili.player.controller

import org.openani.mediamp.MediampPlayer
import top.sacz.bili.player.platform.BiliContext

actual object PlayerMediaDataUtils {
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}