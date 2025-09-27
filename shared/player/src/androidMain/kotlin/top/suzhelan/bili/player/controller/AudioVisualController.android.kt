package top.suzhelan.bili.player.controller

import android.app.Activity
import android.content.Context
import android.media.AudioManager
import android.view.WindowManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import kotlin.math.roundToInt


internal class AndroidAudioVisualManager(
    private val context: Context
) : AudioVisualManager {
    private val audioManager by lazy {
        context.getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    private var streamType = AudioManager.STREAM_MUSIC
    override fun getCurrentVolume(): Float {
        return audioManager.getStreamVolume(streamType).toFloat() / audioManager.getStreamMaxVolume(
            streamType
        )
    }

    override fun setVolume(volume: Float) {
        val max = audioManager.getStreamMaxVolume(streamType)
        return audioManager.setStreamVolume(
            streamType,
            (volume * max).roundToInt()
                .coerceIn(minimumValue = 0, maximumValue = max),
            0,
        )
    }

    override fun getCurrentBrightness(): Float {
        val activity = context as Activity
        val window = activity.window ?: return -1f
        val current = window.attributes.screenBrightness
        if (current == WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE) {
            return android.provider.Settings.System.getInt(
                context.contentResolver,
                android.provider.Settings.System.SCREEN_BRIGHTNESS,
            ).toFloat() / 255
        }
        return current
    }

    override fun setBrightness(brightness: Float) {
        val activity = context as Activity
        val window = activity.window ?: return
        window.attributes.screenBrightness = brightness
        window.attributes = window.attributes
    }
}

@Composable
actual fun rememberAudioLevelController(): AudioVisualState {
    val context = LocalContext.current
    return remember {
        AudioVisualState(AndroidAudioVisualManager(context))
    }
}