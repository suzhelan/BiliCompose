package top.suzhelan.bili.player.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue

@Composable
expect fun rememberAudioLevelController(): AudioVisualState

@Stable
class AudioVisualState(
    val audioVisualManager: AudioVisualManager
) {
    var volumePercentage: Float by mutableFloatStateOf(audioVisualManager.getCurrentVolume())
    var brightnessPercentage: Float by mutableFloatStateOf(audioVisualManager.getCurrentBrightness())

    fun setVolume(volume: Float) {
        audioVisualManager.setVolume(volume)
        this.volumePercentage = volume
    }

    fun setBrightness(brightness: Float) {
        audioVisualManager.setBrightness(brightness)
        this.brightnessPercentage = brightness
    }
}

/**
 * 音量亮度管理器
 * @author: suzhelan
 * @date: 2025/8/22
 */
interface AudioVisualManager {

    fun getCurrentVolume(): Float
    fun setVolume(volume: Float)

    fun getCurrentBrightness(): Float
    fun setBrightness(brightness: Float)
}
