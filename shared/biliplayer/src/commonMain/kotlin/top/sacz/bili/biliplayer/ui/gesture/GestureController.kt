package top.sacz.bili.biliplayer.ui.gesture

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


@Composable
fun rememberGestureController(
    widthOrHeight: Int,
    initialProgress: () -> Float,
    onChange: (Float) -> Unit,
    onFinished: () -> Unit = {}
): GestureController {
    return remember {
        GestureController(widthOrHeight, initialProgress, onChange, onFinished)
    }
}

/**
 * 进度控制器,可以控制拖动手势和计算
 * @param widthOrHeight 屏幕高度或宽度
 * @param initialProgress 初始进度
 * @param onChange 进度改变的回调
 * @param onFinished 拖动结束的回调
 */
@Stable
class GestureController(
    private val widthOrHeight: Int,
    private val initialProgress: () -> Float,
    private val onChange: (Float) -> Unit,
    private val onFinished: () -> Unit = {}
) {
    var currentPositionRatio by mutableStateOf(Float.NaN)

    fun onStart() {
        this.currentPositionRatio = initialProgress()
    }

    fun onMove(delta: Float) {
        //如果说调用方没有调用onStart方法,则初始化进度为初始进度
        if (currentPositionRatio.isNaN()) {
            currentPositionRatio = initialProgress()
        }
        val progressChange = delta / widthOrHeight
        currentPositionRatio = (currentPositionRatio + progressChange).coerceIn(0f, 1f)
        onChange(currentPositionRatio)
    }

    fun onFinish() {
        onFinished()
        currentPositionRatio = Float.NaN
    }
}