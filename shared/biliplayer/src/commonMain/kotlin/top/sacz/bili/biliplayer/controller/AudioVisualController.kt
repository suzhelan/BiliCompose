package top.sacz.bili.biliplayer.controller

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue

@Composable
expect fun rememberAudioLevelController(): AudioVisualState

@Stable
class AudioVisualState(
    val audioVisualManager: AudioVisualManager
) {
    val volume: Float by derivedStateOf {
        audioVisualManager.getCurrentVolume()
    }
    val brightness: Float by derivedStateOf {
        audioVisualManager.getCurrentBrightness()
    }

    fun setVolume(volume: Float) {
        audioVisualManager.setVolume(volume)
    }

    fun setBrightness(brightness: Float) {
        audioVisualManager.setBrightness(brightness)
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
