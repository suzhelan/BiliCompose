package top.sacz.bili.biz.player.controller

import chaintech.videoplayer.host.MediaPlayerEvent
import chaintech.videoplayer.host.MediaPlayerHost
import chaintech.videoplayer.model.ScreenResize

/**
 * 音频流和视频流同步控制
 */
class PlayerSyncController(private val videoUrl: String, private val audioUrl: String) {

    private val headers = mapOf(
        "user-Agent" to "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36",
        "referer" to "https://www.bilibili.com",
        //试过app端user-agent,但是仍然403,决定用web端了
    )
    val videoPlayerHost: MediaPlayerHost by lazy {
        buildPlayerHost(videoUrl).apply {
            onEvent = { event ->
                when(event) {
                    is MediaPlayerEvent.CurrentTimeChange -> {
                        audioPlayerHost.seekTo(event.currentTime.toFloat())
                    }
                    is MediaPlayerEvent.PauseChange -> {
                        if (event.isPaused) {
                            audioPlayerHost.pause()
                        } else {
                            audioPlayerHost.play()
                        }
                    }
                    else ->{
                        
                    }
                }
            }
        }
    }
    val audioPlayerHost: MediaPlayerHost by lazy {
        buildPlayerHost(audioUrl).apply {
            onEvent = { event ->

            }
        }
    }

    fun startSync() {
        videoPlayerHost.play()
        audioPlayerHost.play()
    }

    fun stopSync() {
        videoPlayerHost.pause()
        audioPlayerHost.pause()
    }

    // 进度跳转同步
    fun seekBothTo(seconds: Float) {
        videoPlayerHost.seekTo(seconds)
        audioPlayerHost.seekTo(seconds)
    }

    private fun buildPlayerHost(url: String): MediaPlayerHost {
        return MediaPlayerHost(
            mediaUrl = url,
            initialVideoFitMode = ScreenResize.FIT,
//            isPaused = true,
            headers = headers
        )
    }

}