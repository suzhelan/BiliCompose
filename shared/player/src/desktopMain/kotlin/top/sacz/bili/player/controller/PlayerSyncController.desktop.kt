package top.sacz.bili.player.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.source.UriMediaData

actual object PlayerMediaDataUtils {
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
        //协程
        CoroutineScope(Dispatchers.IO).launch {
            mediampPlayer.setMediaData(UriMediaData(uri = videoUrl, headers = PlayerSyncController.headers))
        }
    }
}