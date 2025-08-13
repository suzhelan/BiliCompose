package top.sacz.bili.shared.common.ui

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

/**
 * 合并TextStyle和ContentColor到内容中
 */
@Composable
fun ProvideTextStyleContentColor(
    value: TextStyle = LocalTextStyle.current,
    color: Color = LocalContentColor.current,
    content: @Composable () -> Unit
) {
    val mergedStyle = LocalTextStyle.current.merge(value)
    CompositionLocalProvider(
        LocalTextStyle provides mergedStyle,
        LocalContentColor provides color, content = content,
    )
}

/**
 * 改变内容颜色
 */
@Composable
fun ProvideContentColor(
    color: Color,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(
        LocalContentColor provides color, content = content,
    )
}