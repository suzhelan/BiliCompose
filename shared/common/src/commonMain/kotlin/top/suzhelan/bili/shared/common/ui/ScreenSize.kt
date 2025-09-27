package top.suzhelan.bili.shared.common.ui

import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


// 屏幕尺寸分类
enum class ScreenSize {
    Compact,    // 紧凑型（手机竖屏）
    Medium,     // 中型（手机横屏/小平板）
    Expanded    // 扩展型（大平板/桌面）
}

fun ScreenSize.isPhone(): Boolean = this == ScreenSize.Compact
fun ScreenSize.isTablet(): Boolean = this == ScreenSize.Medium
fun ScreenSize.isDesktop(): Boolean = this == ScreenSize.Expanded

@Composable
fun ScreenSizeCalculation(
    modifier: Modifier = Modifier,
    content: @Composable (ScreenSize) -> Unit
) {
    BoxWithConstraints(modifier = modifier) {
        val screenSize = when {
            maxWidth < 600.dp -> ScreenSize.Compact
            maxWidth < 840.dp -> ScreenSize.Medium
            else -> ScreenSize.Expanded
        }
        content(screenSize)
    }
}