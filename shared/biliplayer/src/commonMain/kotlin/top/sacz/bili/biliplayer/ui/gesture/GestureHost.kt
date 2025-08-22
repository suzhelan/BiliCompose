package top.sacz.bili.biliplayer.ui.gesture

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput


/**
 * 统一手势控制器，处理点击、双击和水平/垂直拖拽手势,不存放任何可以显示的UI
 * 使用draggable也可以,我这里选择了pointerInput
 * @param onClick 点击
 * @param onDoubleTap 双击
 *
 * @param currentProgress 当前进度
 * @param onProgress 左右滑进度
 * @param onProgressFinished 拖动进度条结束
 *
 * @param currentVolume 当前音量
 * @param onVolume 更新音量
 *
 * @param currentBrightness 当前亮度
 * @param onBrightness 更新亮度
 *
 */
@Composable
fun BoxWithConstraintsScope.GestureHost(
    //单击和长按
    onClick: () -> Unit,
    onDoubleTap: () -> Unit,
    //进度相关
    currentProgress: () -> Float,
    onProgress: (Float) -> Unit,
    onProgressFinished: () -> Unit,
    //当前系统音量
    currentVolume: () -> Float,
    onVolume: (Float) -> Unit,
    //当前亮度
    currentBrightness: () -> Float,
    onBrightness: (Float) -> Unit
) {
    //拖拽进度
    val targetProgress = rememberGestureController(
        widthOrHeight = constraints.maxWidth,
        initialProgress = currentProgress,
        onChange = {
            onProgress(it)
        },
        onFinished = {
            onProgressFinished()
        }
    )
    //拖拽音量
    val targetVolume = rememberGestureController(
        widthOrHeight = constraints.maxHeight,
        initialProgress = currentVolume,
        onChange = {
            onVolume(it)
        }
    )
    //亮度
    val targetBrightness = rememberGestureController(
        widthOrHeight = constraints.maxHeight,
        initialProgress = currentBrightness,
        onChange = {
            onBrightness(it)
        }
    )
    //手势控制器
    Row(
        modifier = Modifier
            .fillMaxSize()
            .combinedClickable(
                onClick = {
                    onClick()
                },
                onDoubleClick = {
                    onDoubleTap()
                },
                indication = null,
                interactionSource = null
            )//使用combinedClickable不会和pointerInput冲突
            .pointerInput(Unit) {
                //监听水平拖动
                detectHorizontalDragGestures(
                    onDragStart = {
                        targetProgress.onStart()
                    },
                    onHorizontalDrag = { change, dragAmount ->
                        targetProgress.onMove(dragAmount)
                    },
                    onDragEnd = {
                        targetProgress.onFinish()
                    }
                )
            }
    ) {
        //左半屏
        Box(
            modifier = Modifier.weight(1f).fillMaxHeight()
                .pointerInput(Unit) {
                    //监听垂直拖动
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            targetBrightness.onMove(dragAmount)
                        },
                    )
                })
        //中间纯分隔即可
        Box(modifier = Modifier.weight(1f).fillMaxHeight())
        //右半屏
        Box(
            modifier = Modifier.weight(1f)
                .fillMaxHeight()
                .pointerInput(Unit) {
            //监听垂直拖动
            detectVerticalDragGestures(
                onVerticalDrag = { change, dragAmount ->
                    targetVolume.onMove(dragAmount)
                },
            )
        })
    }
}