package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.player.ui.progress.MediaProgressSliderColors
import top.suzhelan.bili.player.ui.progress.MediaProgressSliderDefaults
import top.suzhelan.bili.player.ui.progress.PlayerProgressSliderState

/**
 * 竖屏底部进度条。
 *
 * 始终使用同一个 Slider 承接手势，避免缩略态拖动时因为切换组件导致手势中断；
 * 展开状态只改变轨道和滑块尺寸，交互链路保持一致。
 */
data class ProgressPreview(
    val positionMillis: Long,
    val totalDurationMillis: Long,
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VerticalAdaptiveProgressSlider(
    state: PlayerProgressSliderState,
    isExpanded: Boolean,
    onUserInteraction: () -> Unit,
    modifier: Modifier = Modifier,
    sliderColors: MediaProgressSliderColors = MediaProgressSliderDefaults.colors(),
) {
    val trackHeight = if (isExpanded) 6.dp else 2.dp
    val thumbWidth = if (isExpanded) 8.dp else 6.dp
    val thumbRadius = if (isExpanded) 8.dp else 5.dp

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(24.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        LinearProgressIndicator(
            progress = { state.displayPositionRatio },
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight),
            color = sliderColors.trackProgressColor,
            trackColor = sliderColors.trackBackgroundColor.copy(alpha = 0.8f)
        )
        Slider(
            value = state.displayPositionRatio,
            valueRange = 0f..1f,
            onValueChange = {
                onUserInteraction()
                state.previewPositionRatio(it)
            },
            thumb = {
                Canvas(Modifier.width(thumbWidth).fillMaxHeight()) {
                    drawCircle(
                        color = sliderColors.thumbColor,
                        radius = thumbRadius.toPx()
                    )
                }
            },
            track = { sliderState ->
                SliderDefaults.Track(
                    sliderState,
                    colors = SliderDefaults.colors(
                        activeTrackColor = Color.Transparent,
                        inactiveTrackColor = Color.Transparent,
                        disabledActiveTrackColor = Color.Transparent,
                        disabledInactiveTrackColor = Color.Transparent,
                    )
                )
            },
            onValueChangeFinished = {
                state.changeFinished()
            },
            modifier = Modifier.fillMaxSize()
        )
    }
}
