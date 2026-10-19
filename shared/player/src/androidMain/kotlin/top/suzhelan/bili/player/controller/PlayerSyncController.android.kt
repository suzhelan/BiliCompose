package top.suzhelan.bili.player.controller

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.view.View
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
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.metadata.MediaProperties
import top.suzhelan.bili.player.platform.BiliContext


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
        audioUrl: String,
        onVideoSizeChanged: ((width: Int, height: Int) -> Unit)?,
    ): () -> Unit {
        val exoPlayer = mediampPlayer.impl as ExoPlayer
        val videoSource = AndroidPlayerParam.buildMediaSource(videoUrl)
        val audioSource = AndroidPlayerParam.buildMediaSource(audioUrl)
        val mediaProperties = mediampPlayer.mediaProperties as MutableStateFlow<MediaProperties?>
        val playbackState = mediampPlayer.playbackState as MutableStateFlow<PlaybackState>
        val listener = object : Player.Listener {
            override fun onVideoSizeChanged(videoSize: VideoSize) {
                onVideoSizeChanged?.invoke(videoSize.width, videoSize.height)
            }

            override fun onEvents(player: Player, events: Player.Events) {
                if (events.contains(Player.EVENT_TIMELINE_CHANGED) ||
                    events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED)
                ) {
                    // TIME_UNSET 等未知时长统一为 0，时长更新不再依赖视频尺寸变化。
                    mediaProperties.value = MediaProperties(
                        title = player.mediaMetadata.title?.toString(),
                        durationMillis = player.duration.coerceAtLeast(0),
                    )
                }
                if (events.contains(Player.EVENT_PLAYBACK_STATE_CHANGED) ||
                    events.contains(Player.EVENT_IS_PLAYING_CHANGED)
                ) {
                    // Mediamp 的 onIsPlayingChanged(false) 可能覆盖 FINISHED。
                    // onEvents 在本轮单项回调之后执行，以最终的原生状态为准。
                    if (player.playbackState == Player.STATE_ENDED) {
                        playbackState.value = PlaybackState.FINISHED
                    } else if (playbackState.value == PlaybackState.FINISHED) {
                        playbackState.value = if (player.isPlaying) PlaybackState.PLAYING
                        else PlaybackState.PAUSED
                    }
                }
            }
        }
        mediaProperties.value = null
        exoPlayer.apply {
            addListener(listener)
            setMediaSource(MergingMediaSource(videoSource, audioSource))
            prepare()
        }
        return { exoPlayer.removeListener(listener) }
    }

    actual fun setFullScreen(context: BiliContext, fullScreen: Boolean, isPortraitVideo: Boolean) {
        if (context is Activity) {
            if (fullScreen) {
                // 根据传入的视频比例信息决定屏幕方向

                // 根据视频类型选择合适的屏幕方向
                if (isPortraitVideo) {
                    // 竖屏视频：保持竖屏方向，不做横屏切换
                    context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                } else {
                    // 横屏视频：切换到横屏
                    context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
                }

                // 使用 WindowInsetsController 隐藏系统 UI（推荐方式）
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    context.window.insetsController?.let { controller ->
                        controller.hide(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                        controller.systemBarsBehavior =
                            android.view.WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
                    }
                } else {
                    @Suppress("DEPRECATION")
                    context.window.decorView.systemUiVisibility = (
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                    or View.SYSTEM_UI_FLAG_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                    or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                    or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                    or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            )
                }
            } else {
                //取消方向锁定
                context.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                // 恢复系统 UI 显示
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
                    context.window.insetsController?.show(android.view.WindowInsets.Type.statusBars() or android.view.WindowInsets.Type.navigationBars())
                } else {
                    @Suppress("DEPRECATION")
                    context.window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE
                }
            }
        } else {
            val orientation = if (fullScreen) {
                Configuration.ORIENTATION_LANDSCAPE
            } else {
                Configuration.ORIENTATION_PORTRAIT
            }
            context.resources.configuration.orientation = orientation
        }
    }
}
