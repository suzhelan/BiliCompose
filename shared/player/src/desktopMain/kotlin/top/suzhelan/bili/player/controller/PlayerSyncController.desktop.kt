package top.suzhelan.bili.player.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.source.UriMediaData
import top.suzhelan.bili.player.platform.BiliContext


actual object PlayerMediaDataUtils {
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
            mediampPlayer.setMediaData(
                UriMediaData(
                    uri = videoUrl,
                    headers = PlayerSyncController.headers,
                    options = listOf("input-slave=$audioUrl")
                )
            )
        }
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}