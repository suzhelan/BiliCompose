package top.suzhelan.bili.player.controller

import org.openani.mediamp.MediampPlayer
import top.suzhelan.bili.player.platform.BiliContext

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