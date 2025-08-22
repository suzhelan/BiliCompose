package top.sacz.bili.biliplayer.ui.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeDown
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


sealed class AudioVisualIndicator {
    object Volume : AudioVisualIndicator()
    object Brightness : AudioVisualIndicator()
    object None : AudioVisualIndicator()
}

/**
 * 音量指示器
 */
@Composable
fun VolumeIndicator(
    modifier: Modifier = Modifier,
    progress: Float
) {
    //布局内容 音量/亮度图标 + 进度条
    Row(
        modifier = modifier.background(Color.Black.copy(alpha = 0.5f)).padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.AutoMirrored.Outlined.VolumeDown,
            contentDescription = "Volume"
        )
        LinearProgressIndicator(progress = {
            progress
        })
    }
}