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
                audioUrl,
                *buildList {
                    // 主从同步设置
                    add("input-slave=$videoUrl")
                    add("input-slave-sync")  // 强制同步
                    add("avsync-force")      // 强制音视频同步

                    // 请求头
                    PlayerSyncController.headers.forEach  { (key, value) ->
                        when (key.lowercase())  {
                            "user-agent" -> add("http-user-agent=$value")
                            "referer" -> add("http-referrer=$value")
                            else -> add("http-$key=$value")
                        }
                    }
                }.toTypedArray()
            )
        }
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}