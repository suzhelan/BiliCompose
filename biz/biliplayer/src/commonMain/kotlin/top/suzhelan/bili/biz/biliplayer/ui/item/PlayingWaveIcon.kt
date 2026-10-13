package top.suzhelan.bili.biz.biliplayer.ui.item

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import kotlin.math.PI
import kotlin.math.sin

@Composable
internal fun PlayingWaveIcon(isPlaying: Boolean, modifier: Modifier = Modifier) {
    val phase = if (isPlaying) {
        val transition = rememberInfiniteTransition(label = "播放波浪")
        val animatedPhase by transition.animateFloat(
            initialValue = 0f,
            targetValue = (2 * PI).toFloat(),
            animationSpec = infiniteRepeatable(tween(900, easing = LinearEasing)),
            label = "波浪相位",
        )
        animatedPhase
    } else 0f
    val color = ColorPrimary
    Canvas(
        modifier = modifier.size(18.dp).semantics {
            contentDescription = if (isPlaying) "正在播放" else "播放已暂停"
        }
    ) {
        val barWidth = size.width / 7
        repeat(4) { index ->
            val fraction = if (isPlaying) {
                0.3f + 0.7f * ((sin(phase + index * 1.4f) + 1f) / 2f)
            } else 0.3f
            val height = size.height * fraction
            drawRoundRect(
                color = color,
                topLeft = Offset(index * barWidth * 2, size.height - height),
                size = Size(barWidth, height),
                cornerRadius = CornerRadius(barWidth / 2, barWidth / 2),
            )
        }
    }
}
