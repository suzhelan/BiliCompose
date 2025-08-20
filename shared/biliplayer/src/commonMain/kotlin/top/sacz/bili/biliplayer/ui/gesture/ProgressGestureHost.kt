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
 * 进度手势控制器,仅处理进度
 */
@Composable
fun ProgressGestureHost(
    currentProgress: () -> Float,
    onShowSlider: (Float) -> Unit,
    onFinished: () -> Unit,
) {
    val width = remember { mutableStateOf(0) }
    var targetProgress by remember { mutableStateOf(Float.NaN) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { width.value = it.width }
            .pointerInput(Unit) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        targetProgress = currentProgress()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        change.consume()
                        val progressChange = dragAmount / width.value
                        targetProgress = (targetProgress + progressChange).coerceIn(0f, 1f)
                        onShowSlider(targetProgress)
                    },
                    onDragEnd = onFinished
                )
            }
    )
}

