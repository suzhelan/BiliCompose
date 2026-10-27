package top.suzhelan.bili.player.ui.gesture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue


@Composable
fun rememberGestureController(
    widthOrHeight: Int,
    initialProgress: () -> Float,
    onChange: (Float) -> Unit,
    onFinished: () -> Unit = {},
    sensitivity: () -> Float = { 1f },
): GestureController {
    val latestInitialProgress by rememberUpdatedState(initialProgress)
    val latestOnChange by rememberUpdatedState(onChange)
    val latestOnFinished by rememberUpdatedState(onFinished)
    val latestSensitivity by rememberUpdatedState(sensitivity)
    return remember(widthOrHeight) {
        GestureController(
            widthOrHeight = widthOrHeight,
            initialProgress = { latestInitialProgress() },
            onChange = { latestOnChange(it) },
            onFinished = { latestOnFinished() },
            sensitivity = { latestSensitivity() }
        )
    }
}

/**
 * 进度控制器,可以控制拖动手势和计算
 * @param widthOrHeight 屏幕高度或宽度
 * @param initialProgress 初始进度
 * @param onChange 进度改变的回调
 * @param onFinished 拖动结束的回调
 * @param sensitivity 进度变化倍率，在每次手势开始时读取；音量和亮度默认保持 1
 */
@Stable
class GestureController(
    private val widthOrHeight: Int,
    private val initialProgress: () -> Float,
    private val onChange: (Float) -> Unit,
    private val onFinished: () -> Unit = {},
    private val sensitivity: () -> Float = { 1f },
) {
    var currentPositionRatio by mutableStateOf(Float.NaN)
    private var gestureSensitivity = 1f

    fun onStart() {
        this.currentPositionRatio = initialProgress()
        // 一次手势内保持倍率不变，避免媒体时长更新导致拖动速度突变。
        gestureSensitivity = sensitivity().coerceIn(0f, 1f)
    }

    fun onMove(delta: Float) {
        //如果说调用方没有调用onStart方法,则初始化进度为初始进度
        if (currentPositionRatio.isNaN()) {
            onStart()
        }
        if (widthOrHeight <= 0 || gestureSensitivity <= 0f) return
        val progressChange = delta / widthOrHeight * gestureSensitivity
        currentPositionRatio = (currentPositionRatio + progressChange).coerceIn(0f, 1f)
        onChange(currentPositionRatio)
    }

    fun onFinish() {
        onFinished()
        currentPositionRatio = Float.NaN
    }
}
