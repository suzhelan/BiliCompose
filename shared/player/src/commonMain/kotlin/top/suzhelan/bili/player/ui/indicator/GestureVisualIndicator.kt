package top.suzhelan.bili.player.ui.indicator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.player.ui.theme.PlayerColor
import top.suzhelan.bili.player.util.TimeUtil

/**
 * 手势发生时指示器
 */
sealed class GestureVisualIndicator {
    /**
     * 音量指示器
     */
    object Volume : GestureVisualIndicator()
    /**
     * 亮度指示器
     */
    object Brightness : GestureVisualIndicator()
    /**
     * 预览进度指示器
     */
    class OnPreview(val currentPositionMillis: Long, val totalDurationMillis: Long) :
        GestureVisualIndicator()

    /**
     * 无指示器,不显示任何UI
     */
    object None : GestureVisualIndicator()
}

/**
 * 预览进度指示器
 */
@Composable
fun OnPreviewIndicator(modifier: Modifier = Modifier, progress: Long, total: Long) {
    Box(
        modifier = modifier.wrapContentSize()
            .background(
                PlayerColor.background.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "${TimeUtil.formatMillisToTime(progress)}/${TimeUtil.formatMillisToTime(total)}",
            color = PlayerColor.control.copy(alpha = 0.8f),
        )
    }
}

/**
 * 音量指示器
 */
@Composable
fun VolumeIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float
) {
    Indicator(
        modifier = modifier,
        icon = Icons.AutoMirrored.Outlined.VolumeUp,
        progress = {
            progress()
        }
    )
}

/**
 * 亮度指示器
 */
@Composable
fun BrightnessIndicator(
    modifier: Modifier = Modifier,
    progress: () -> Float
) {
    Indicator(
        modifier = modifier,
        icon = Icons.Outlined.WbSunny,
        progress = {
            progress()
        }
    )
}

@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    progress: () -> Float
) {
    //布局内容 音量/亮度图标 + 进度条
    Row(
        modifier = modifier.fillMaxWidth(0.5f)
            .background(
                PlayerColor.background.copy(alpha = 0.5f),
                RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            tint = PlayerColor.control.copy(alpha = 0.8f),
            contentDescription = null
        )
        Spacer(modifier = Modifier.width(10.dp))
        LinearProgressIndicator(
            trackColor = PlayerColor.control.copy(alpha = 0.8f),
            progress = {
                progress()
            }
        )
    }
}