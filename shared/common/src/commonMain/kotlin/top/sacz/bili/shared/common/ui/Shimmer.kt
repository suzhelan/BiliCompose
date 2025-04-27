package top.sacz.bili.shared.common.ui

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * 骨架屏
 */
@Composable
fun Modifier.shimmerEffect(showShimmer: Boolean = true): Modifier =
    composed {
        val infiniteTransition = rememberInfiniteTransition()
        val translateAnim by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,  // 调整为更自然的位移范围
            animationSpec = infiniteRepeatable(
                animation = tween(1000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse  // 关键修改：反向重复
            )
        )

        drawWithCache {
            // 对角线位移参数（优化后的计算公式）
            val gradientWidth = size.width.coerceAtLeast(size.height) * 2
            val startOffset = gradientWidth * (translateAnim - 1f)
            val endOffset = gradientWidth * translateAnim

            val gradient = Brush.linearGradient(
                colors = listOf(
                    Color.Gray.copy(alpha = 0.2f),
                    Color.White.copy(alpha = 0.9f),
                    Color.Gray.copy(alpha = 0.2f)
                ),
                start = Offset(startOffset, startOffset),
                end = Offset(endOffset, endOffset)
            )

            onDrawWithContent {
                if (showShimmer) {
                    drawRoundRect(
                        brush = gradient,
                        size = size,
                        cornerRadius = CornerRadius.Zero
                    )
                } else {
                    drawContent()
                }
            }
        }
    }


fun Modifier.autoSkeleton(
    isLoading: Boolean,
    shape: Shape = RoundedCornerShape(0.dp), // 默认使用圆角形状
): Modifier = composed {
    if (!isLoading) return@composed this
    this.clip(shape) // 先裁剪形状
//        .background(Color.LightGray.copy(alpha = 0.15f), shape)
        .shimmerEffect(showShimmer = true) // 传递形状参数
}

