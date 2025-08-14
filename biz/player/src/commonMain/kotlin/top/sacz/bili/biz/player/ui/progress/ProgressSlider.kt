package top.sacz.bili.biz.player.ui.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


@Immutable
class MediaProgressSliderColors(
    val trackBackgroundColor: Color,
    val trackProgressColor: Color,
    val thumbColor: Color,
    val downloadingColor: Color,
    val chapterColor: Color,
    val previewTimeBackgroundColor: Color,
    val previewTimeTextColor: Color,
)

object MediaProgressSliderDefaults {
    @Composable
    fun colors(
        trackBackgroundColor: Color = MaterialTheme.colorScheme.surface,
        trackProgressColor: Color = MaterialTheme.colorScheme.primary,
        thumbColor: Color = MaterialTheme.colorScheme.primary,
        downloadingColor: Color = Color.Yellow,
        chapterColor: Color = MaterialTheme.colorScheme.onSurface,
        previewTimeBackgroundColor: Color = MaterialTheme.colorScheme.surface,
        previewTimeTextColor: Color = MaterialTheme.colorScheme.onSurface,
    ): MediaProgressSliderColors {
        return MediaProgressSliderColors(
            trackBackgroundColor,
            trackProgressColor,
            thumbColor,
            downloadingColor,
            chapterColor,
            previewTimeBackgroundColor,
            previewTimeTextColor
        )
    }
}

/**
 * 播放进度条
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressSlider(
    modifier: Modifier = Modifier,
    sliderColors: MediaProgressSliderColors = MediaProgressSliderDefaults.colors(),
) {
    /* //定义播放进度条
     Box(modifier = modifier.fillMaxWidth().height(24.dp).clip(RoundedCornerShape(16.dp))) {
         //轨道
         Canvas(modifier = Modifier.matchParentSize()) {
             drawRect(
                 color = sliderColors.trackBackgroundColor,
                 topLeft = Offset(0f, 0f),
                 size = Size(size.width, size.height)
             )
         }
         //进度
         Canvas(modifier = Modifier.matchParentSize()) {
             drawRect(
                 color = sliderColors.trackProgressColor,
                 topLeft = Offset(0f, 0f),
                 size = Size(size.width / 2, size.height)
             )
         }
     }*/
    // draw thumb
    val interactionSource = remember { MutableInteractionSource() }
    Slider(
        value = 0.5f,
        valueRange = 0f..1f,
        onValueChange = {
            //TODO 控制进度
        },
        interactionSource = interactionSource,
        thumb = {
            Canvas(Modifier.width(12.dp).height(24.dp)) {
                drawCircle(
                    sliderColors.thumbColor,
                    radius = 8.dp.toPx(),
                )
            }
            // 仅在 detached slider 上显示
            // 如果有预览
        },
        track = { state ->
            SliderDefaults.Track(
                state,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Transparent,
                    inactiveTrackColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Transparent,
                    disabledInactiveTrackColor = Color.Transparent,
                ),
            )
        },
        onValueChangeFinished = {
            //结束预览并跳转进度
        },
        enabled = true,
        modifier = Modifier.fillMaxWidth().height(24.dp)
    )
}