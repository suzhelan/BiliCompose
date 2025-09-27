package top.suzhelan.bili.player.ui.progress

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
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.player.ui.theme.PlayerColor
import kotlin.math.roundToLong

@Composable
fun rememberPlayerProgressSliderState(
    currentPositionMillis: () -> Long,
    totalDurationMillis: () -> Long,
    onProView: (Long) -> Unit = {},
    onFinished: (Long) -> Unit = {}
): PlayerProgressSliderState {
    return remember {
        PlayerProgressSliderState(
            currentPositionMillis = currentPositionMillis,
            totalDurationMillis = totalDurationMillis,
            onPreView = onProView,
            onFinished = onFinished,
        )
    }
}

/**
 * 播放器进度滑块的状态.
 *
 * - 支持从 [currentPositionMillis] 同步当前播放位置, 从 [totalDurationMillis] 同步总时长.
 * - 使用 [onPreView] 和 [onFinished] 来处理用户拖动进度条的事件.
 *
 */
@Stable
class PlayerProgressSliderState(
    /**
     * 当前播放位置.
     */
    currentPositionMillis: () -> Long,
    /**
     * 总时长.
     */
    totalDurationMillis: () -> Long,
    /**
     * 当用户正在拖动进度条时触发. 每有一个 change 都会调用,外部不需要更新进度
     */
    private val onPreView: (positionMillis: Long) -> Unit,
    /**
     * 当用户松开进度条时触发. 此时播放器应当要跳转到该位置.
     */
    private val onFinished: (positionMillis: Long) -> Unit,
) {
    val currentPositionMillis: Long by derivedStateOf(currentPositionMillis)
    val totalDurationMillis: Long by derivedStateOf(totalDurationMillis)

    /**
     * 预览百分比进度,如果为NaN则表示不在预览,其他值为正在预览
     */
    private var previewPositionRatio: Float by mutableStateOf(Float.POSITIVE_INFINITY)

    /**
     * 是否正在预览
     */
    val isPreviewing: Boolean by derivedStateOf {
        previewPositionRatio.isFinite()
    }

    /**
     * 显示的播放进度
     */
    val displayPositionRatio by derivedStateOf {
        when {
            // 正在预览 则显示预览拖动的值
            isPreviewing -> previewPositionRatio
            // 总时长为0 则显示0
            this.totalDurationMillis == 0L -> 0f
            // 非预览时跟踪播放进度
            else -> this.currentPositionMillis.toFloat() / this.totalDurationMillis.toFloat()
        }
    }

    /**
     * 拖动进度条时调用 只能被Slider调用
     */
    fun previewPositionRatio(ratio: Float) {
        //设置预览进度
        previewPositionRatio = ratio
        //更新外部状态
        onPreView((totalDurationMillis * ratio).roundToLong())
    }

    /**
     * 预览结束调用 只能被 Slider调用
     */
    fun changeFinished() {
        //如果不在预览状态被结束 说明状态非法
        if (!isPreviewing) return
        //通知外部进度
        onFinished((this.previewPositionRatio * totalDurationMillis).roundToLong())
        //重置预览进度
        previewPositionRatio = Float.NaN
    }

}


@Immutable
class MediaProgressSliderColors(
    val trackBackgroundColor: Color,
    val trackProgressColor: Color,
    val thumbColor: Color,
)

object MediaProgressSliderDefaults {
    @Composable
    fun colors(
        trackBackgroundColor: Color = PlayerColor.control,
        trackProgressColor: Color = PlayerColor.primary,
        thumbColor: Color = PlayerColor.primary,
    ): MediaProgressSliderColors {
        return MediaProgressSliderColors(
            trackBackgroundColor,
            trackProgressColor,
            thumbColor,
        )
    }
}

@Composable
fun PlayerProgressIndicator(
    modifier: Modifier = Modifier,
    state: PlayerProgressSliderState,
    sliderColors: MediaProgressSliderColors = MediaProgressSliderDefaults.colors(),
) {
    LinearProgressIndicator(
        progress = { state.displayPositionRatio },
        modifier = modifier.fillMaxWidth(),
        color = sliderColors.trackProgressColor,
        trackColor = sliderColors.trackBackgroundColor.copy(alpha = 0.8f)
    )
}

/**
 * 播放进度条
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerProgressSlider(
    modifier: Modifier = Modifier,
    state: PlayerProgressSliderState,
    sliderColors: MediaProgressSliderColors = MediaProgressSliderDefaults.colors(),
) = Box(
    modifier = modifier.fillMaxWidth().height(24.dp),
    contentAlignment = Alignment.CenterStart
) {

/*//轨道与背景
    Box(modifier = Modifier.fillMaxSize().padding(8.dp).clip(CircleShape)) {
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
                size = Size(size.width * state.displayPositionRatio, size.height) // 使用传入的进度值
            )
        }
    }*/
    LinearProgressIndicator(
        progress = { state.displayPositionRatio },
        modifier = Modifier.fillMaxWidth().height(6.dp),
        color = sliderColors.trackProgressColor,
        trackColor = sliderColors.trackBackgroundColor.copy(alpha = 0.8f)
    )
    //仅作拖动进度实现
    Slider(
        value = state.displayPositionRatio, // 使用传入的进度值
        valueRange = 0f..1f,
        onValueChange = {
            state.previewPositionRatio(it)
        },
        thumb = {
            Canvas(Modifier.width(8.dp).fillMaxHeight()) {
                drawCircle(
                    sliderColors.thumbColor,
                    radius = 8.dp.toPx(),
                )
            }
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
            state.changeFinished()
        },
        modifier = modifier.fillMaxSize()
    )
}
