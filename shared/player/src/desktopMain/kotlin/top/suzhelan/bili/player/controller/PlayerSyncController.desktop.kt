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
        vlcPlayer.media().prepare(
            videoUrl,
            "input-slave=$audioUrl"  // 直接指定外部音频作为从属输入
        )
        /*vlcPlayer.media().prepare(
            videoUrl,
            "input-slave=$audioUrl",
            ":network-caching=1000",       // 缓冲1秒减少抖动
            ":clock-jitter=0",             // 关闭时钟抖动补偿
            ":clock-synchronization=exact" // 强制精确同步模式
        )*/
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean) {
    }
}