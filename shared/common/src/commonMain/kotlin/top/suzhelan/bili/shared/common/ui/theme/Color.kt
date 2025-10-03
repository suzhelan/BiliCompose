package top.suzhelan.bili.shared.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle


/**
 * 收拢所有颜色
 */
val ColorPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
val ColorPrimaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
val ColorSurface @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface

/**
 * 标准字体颜色
 * 常态为黑，深色模式为白
 */
val TextColor: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurface

/**
 * 提示颜色
 * 常态为灰色，深色模式为浅灰色
 */
val TipColor: Color @Composable @ReadOnlyComposable get() = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray

/**
 * 分割线颜色
 */
val DividingLineColor: Color @Composable @ReadOnlyComposable get() = TipColor.copy(alpha = 0.5f)

object ErrorColor {
    val onErrorContainer : Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onErrorContainer
    val errorContainer : Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.errorContainer
    val error : Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error
    val onError : Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onError
}
//字体风格
object TextStyle {
    val Title: TextStyle @Composable @ReadOnlyComposable get() = MaterialTheme.typography.titleLarge
}