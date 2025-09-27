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
        //协程
        CoroutineScope(Dispatchers.IO).launch {
//            val url = "http://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4"
            mediampPlayer.setMediaData(UriMediaData(uri = videoUrl, headers = PlayerSyncController.headers))
            mediampPlayer.resume()
        }
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}