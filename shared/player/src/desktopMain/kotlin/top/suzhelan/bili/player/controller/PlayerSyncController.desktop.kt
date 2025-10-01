package top.suzhelan.bili.player.controller

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.vlc.VlcMediampPlayer
import top.suzhelan.bili.player.platform.BiliContext


actual object PlayerMediaDataUtils {
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
        CoroutineScope(Dispatchers.IO).launch {
           /* mediampPlayer.setMediaData(
                UriMediaData(
                    uri = videoUrl,
                    headers = PlayerSyncController.headers,
                    options = listOf("input-slave=$audioUrl")
                )
            )*/
            val vlcPlayer = mediampPlayer as VlcMediampPlayer
            val embeddedMediaPlayer = vlcPlayer.impl
            embeddedMediaPlayer.media().play(
                videoUrl,
                *buildList {
                    //音频流合并
                    add("input-slave=$audioUrl")
                    //请求头
                    add("http-user-agent=${PlayerSyncController.headers["User-Agent"]}")
                    add("http-referrer=${PlayerSyncController.headers["Referer"]}")
                }.toTypedArray()
            )
        }
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}