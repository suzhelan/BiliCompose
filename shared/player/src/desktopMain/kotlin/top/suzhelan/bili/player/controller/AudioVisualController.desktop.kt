package top.suzhelan.bili.player.controller

import androidx.compose.runtime.Composable

private class DesktopAudioVisualManager : AudioVisualManager {
    override fun getCurrentVolume(): Float {
        return 0f
    }

    override fun setVolume(volume: Float) {
    }

    override fun getCurrentBrightness(): Float {
        return 0f
    }

    override fun setBrightness(brightness: Float) {
    }
}
@Composable
actual fun rememberAudioLevelController(): AudioVisualState {
    return AudioVisualState(DesktopAudioVisualManager())
}