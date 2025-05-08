package top.sacz.bili.biz.player.player

import androidx.compose.runtime.Composable
import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.ui.audio.AudioPlayer
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.shared.common.logger.Logger


/**
 * 音频播放器
 */
@Composable
fun AudioPlayerUI(playerHost: MediaPlayerHost) {
    AudioPlayer(playerHost)
}


// 添加公共方法供外部调用
fun getAudioPlayerHost(url: String, controller: PlayerSyncController): MediaPlayerHost {
    val mediaPlayerHost = MediaPlayerHost(
        mediaUrl = url,
        headers = mapOf(
            "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
            "referer" to "https://www.bilibili.com",
            //试过app端user-agent,但是仍然403,决定用web端了
        )
    )
    mediaPlayerHost.onEvent = { event ->
        when (event) {
            is MediaPlayerEvent.BufferChange -> {}
            is MediaPlayerEvent.CurrentTimeChange -> {
                Logger.d("音频播放器当前时间：${event.currentTime}")
                Logger.d("同步视频的时间：${controller.videoTime}")
                controller.updateAudioTime(event.currentTime) // 将秒转换为毫秒
                mediaPlayerHost.seekTo(controller.videoTime.toFloat())

            /*    controller.checkSync { time ->
                    mediaPlayerHost.seekTo(time.toFloat()) // 将毫秒转换回秒
                    Logger.d("音频播放器当前时间：${event.currentTime}")
                    Logger.d("同步视频的时间：${time}")
                }*/
            }

            is MediaPlayerEvent.FullScreenChange -> {}
            is MediaPlayerEvent.MediaEnd -> {}
            is MediaPlayerEvent.MuteChange -> {}
            is MediaPlayerEvent.PauseChange -> {}
            is MediaPlayerEvent.TotalTimeChange -> {}
        }
    }
    return mediaPlayerHost
}