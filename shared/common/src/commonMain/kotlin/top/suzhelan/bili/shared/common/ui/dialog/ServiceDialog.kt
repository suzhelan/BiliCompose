package top.suzhelan.bili.shared.common.ui.dialog

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import top.suzhelan.bili.shared.common.ui.theme.ErrorTextColor

/**
 * 警告对话框 用于显示警告信息
 * 如二次确认等
 */
@Composable
fun WarnDialog(
    icon: ImageVector? = Icons.Outlined.Warning,
    title: String = "",
    text: String = "",
    cancelButtonText: String = "",
    confirmButtonText: String = "",
    confirmTextColor: Color = ErrorTextColor,
    onDismissRequest: () -> Unit = {},
    onConfirmRequest: () -> Unit = {},
) {

    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { icon?.let { Icon(imageVector = it, contentDescription = null) } },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text(text = confirmButtonText, color = confirmTextColor)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = cancelButtonText)
            }
        }
    )
}