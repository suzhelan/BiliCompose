package top.sacz.bili.biliplayer.ui.gesture

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged


/**
 * 手势控制器
 */
@Composable
fun GestureHost(
    currentProgress: () -> Float,
    onClick: () -> Unit,
    onShowSlider: (Float) -> Unit,
    onFinished: () -> Unit,
) {
    var drag by remember { mutableStateOf(0f) }
    var width by remember { mutableStateOf(0) }
    var targetProgress by remember { mutableStateOf(0f) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { width = it.width }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        drag = 0f
                        targetProgress = currentProgress()
                    },
                    onHorizontalDrag = { change, dragAmount: Float ->
                        change.consume()
                        drag += dragAmount
                        val progressChange = dragAmount / width
                        targetProgress = (currentProgress() + progressChange).coerceIn(0f, 1f)
                        onShowSlider(targetProgress)
                    },
                    onDragEnd = {
                        onFinished()
                    }
                )
            }
    )
}
