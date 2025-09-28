package top.suzhelan.bili.player.controller

import org.openani.mediamp.MediampPlayer
import top.suzhelan.bili.player.platform.BiliContext
import uk.co.caprica.vlcj.player.embedded.EmbeddedMediaPlayer


actual object PlayerMediaDataUtils {
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
        val vlcPlayer = mediampPlayer.impl as EmbeddedMediaPlayer
        vlcPlayer.media().play(
            videoUrl,
            //播放参数
            *buildList {
                //音频流合并
                add("input-slave=$audioUrl")
                //请求头
                add("http-user-agent=${PlayerSyncController.headers["User-Agent"]}")
                add("http-referrer=${PlayerSyncController.headers["Referer"]}")
            }.toTypedArray()
        )
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}