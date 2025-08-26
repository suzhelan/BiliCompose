package top.sacz.bili.player.controller

import android.annotation.SuppressLint
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.VideoSize
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.DefaultHttpDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.MediaSource
import androidx.media3.exoplayer.source.MergingMediaSource
import androidx.media3.exoplayer.source.ProgressiveMediaSource
import androidx.media3.extractor.DefaultExtractorsFactory
import kotlinx.coroutines.flow.MutableStateFlow
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.metadata.MediaProperties


@SuppressLint("StaticFieldLeak", "UnsafeOptInUsageError")
object AndroidPlayerParam {
    // 明确指定使用 Application Context
    private lateinit var context: Context

    private val httpDataSourceFactory = DefaultHttpDataSource.Factory().apply {
        // 全局通用请求头
        setDefaultRequestProperties(PlayerSyncController.headers)
    }

    // 在应用启动时初始化（如自定义 Application 类中）
    fun init(appContext: Context) {
        context = appContext.applicationContext ?: appContext
    }


    fun buildMediaSource(url: String): MediaSource {
        return ProgressiveMediaSource.Factory(
            DefaultDataSource.Factory(context, httpDataSourceFactory),
            DefaultExtractorsFactory()
        ).createMediaSource(MediaItem.fromUri(url))
    }

}


actual object PlayerMediaDataUtils {

    @UnstableApi
    actual fun doLoadMediaData(
        mediampPlayer: MediampPlayer,
        videoUrl: String,
        audioUrl: String
    ) {
        val exoPlayer = mediampPlayer.impl as ExoPlayer
        val videoSource = AndroidPlayerParam.buildMediaSource(videoUrl)
        val audioSource = AndroidPlayerParam.buildMediaSource(audioUrl)
        exoPlayer.apply {
            setMediaSource(MergingMediaSource(videoSource, audioSource))
            prepare()
            addListener(object : Player.Listener {
                override fun onVideoSizeChanged(videoSize: VideoSize) {
                    val mediaProperties =
                        mediampPlayer.mediaProperties as MutableStateFlow<MediaProperties?>
                    mediaProperties.value = MediaProperties(
                        title = "",
                        durationMillis = duration,
                    )
                }
            })
        }
    }
}