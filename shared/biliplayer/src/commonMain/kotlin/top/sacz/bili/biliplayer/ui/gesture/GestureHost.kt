package top.sacz.bili.biliplayer.ui.gesture

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraintsScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput


/**
 * 统一手势控制器，处理点击、双击和水平/垂直拖拽手势,不存放任何可以显示的UI,本文件和本项目没有任何耦合度
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
    onVolumeFinished: () -> Unit,
    //当前亮度
    currentBrightness: () -> Float,
    onBrightness: (Float) -> Unit,
    onBrightnessFinished: () -> Unit
) {
    val maxWidth = constraints.maxWidth
    val maxHeight = constraints.maxHeight

    //拖拽进度
    val targetProgress = rememberGestureController(
        widthOrHeight = maxWidth,
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
        widthOrHeight = maxHeight,
        initialProgress = currentVolume,
        onChange = {
            onVolume(it)
        },
        onFinished = {
            onVolumeFinished()
        }
    )
    //亮度
    val targetBrightness = rememberGestureController(
        widthOrHeight = maxHeight,
        initialProgress = currentBrightness,
        onChange = {
            onBrightness(it)
        },
        onFinished = {
            onBrightnessFinished()
        }
    )
    //手势控制器 操作空间比较大 也可以使用Row{Box(亮度)+Box(间距)+Box(音量)}
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onTap = { onClick() }
                )
            }
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
            }.pointerInput(Unit) {
                detectVerticalDragGestures(
                    onVerticalDrag = { change, dragAmount ->
                        //判断触摸点是否是在屏幕中间1/3,如果是 不做任何操作
                        if (change.position.x > maxWidth / 3 && change.position.x < maxWidth - maxWidth / 3) {
                            return@detectVerticalDragGestures
                        }
                        // 判断触摸点在左侧还是右侧
                        val isLeftSide = change.position.x < maxWidth / 2
                        if (isLeftSide) {
                            targetBrightness.onMove(-dragAmount) // 反转方向，向上滑动增加亮度
                        } else {
                            targetVolume.onMove(-dragAmount) // 反转方向，向上滑动增加音量
                        }
                    },
                    onDragEnd = {
                        // 结束时重置状态
                        targetBrightness.onFinish()
                        targetVolume.onFinish()
                    }
                )
            }
    )
}