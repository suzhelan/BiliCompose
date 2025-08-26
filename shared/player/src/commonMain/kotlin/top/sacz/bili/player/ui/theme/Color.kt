package top.sacz.bili.player.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color


object PlayerColor {
    val primary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
    val primaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer

    /**
     * 正常情况为白色,当深色模式时为黑色,通常用来当背景色
     */
    val surface @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

    /**
     * 默认为黑,当深色模式时为白色,通常用来当文本颜色
     */
    val onSurface @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface
}