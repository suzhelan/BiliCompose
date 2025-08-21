package top.sacz.bili.biliplayer.ui.gesture

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
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
 * 统一手势控制器，处理点击、双击和水平/垂直拖拽手势
 * 使用draggable也可以,我这里选择了pointerInput
 */
@Composable
fun GestureHost(
    currentProgress: () -> Float,
    onClick: () -> Unit,
    onDoubleTap: () -> Unit,
    onShowSlider: (Float) -> Unit,
    onFinished: () -> Unit,
) {
    val width = remember { mutableStateOf(0) }
    var targetProgress by remember { mutableStateOf(Float.NaN) }
    //手势控制器
    Row(
        modifier = Modifier
            .fillMaxSize()
            .onSizeChanged { width.value = it.width }
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onDoubleClick = {
                    onDoubleTap()
                }
            )//使用combinedClickable不会和pointerInput冲突
            .pointerInput(Unit) {
                //监听水平拖动
                detectHorizontalDragGestures(
                    onDragStart = {
                        targetProgress = currentProgress()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        val progressChange = dragAmount / width.value
                        targetProgress = (targetProgress + progressChange).coerceIn(0f, 1f)
                        onShowSlider(targetProgress)
                    },
                    onDragEnd = {
                        onFinished()
                    }
                )
            }
    ) {
        //左半屏
        Box(modifier = Modifier.weight(1f).fillMaxHeight().pointerInput(Unit) {
            //监听垂直拖动
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    println("亮度:$dragAmount")
                },
            )
        })
        //右半屏
        Box(modifier = Modifier.weight(1f).fillMaxHeight().pointerInput(Unit) {
            //监听垂直拖动
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    println("音量:$dragAmount")
                },
            )
        })
    }
}