package top.sacz.bili.shared.common.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color


/**
 * 收拢所有颜色
 */
val ColorPrimary: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
val ColorPrimaryContainer: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
val ColorError: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error

val TextColor: Color @Composable @ReadOnlyComposable get() = if (isSystemInDarkTheme()) Color.White else Color.Black
val TipTextColor: Color @Composable @ReadOnlyComposable get() = if (isSystemInDarkTheme()) Color.LightGray else Color.Gray
val ErrorTextColor: Color @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error

val DividingLineColor: Color @Composable @ReadOnlyComposable get() = TipTextColor.copy(alpha = 0.5f)