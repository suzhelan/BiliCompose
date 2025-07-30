package top.sacz.bili.shared.common.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp


val TitleTextStyle: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontSize = 16.sp,
        color = TextColor,
        fontWeight = FontWeight.Bold
    )

val TipTextStyle: TextStyle
    @Composable @ReadOnlyComposable get() = TextStyle(
        fontSize = 14.sp,
        color = TipTextColor,
        fontWeight = FontWeight.Thin
    )