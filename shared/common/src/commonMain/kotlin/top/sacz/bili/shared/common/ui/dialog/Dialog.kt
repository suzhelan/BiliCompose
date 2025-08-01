package top.sacz.bili.shared.common.ui.dialog

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoadingDialog(
    onDismissRequest: () -> Unit = {},
) {
    BasicAlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = Modifier.size(200.dp)
    ) {
        Card {
            CircularProgressIndicator(
                strokeWidth = 10.dp,
                modifier = Modifier.fillMaxSize().padding(20.dp)
            )
        }
    }
}

@Composable
fun LoadingDialog(
    icon: ImageVector? = null,
    title: String = "",
    text: String = "加载中",
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        icon = {
            if (icon != null) {
                Icon(icon, "DialogIcon")
            }
        },
        title = {
            if (title.isNotEmpty()) {
                Text(title)
            }
        },
        text = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                CircularProgressIndicator(
                    strokeWidth = 5.dp,
                )
                Spacer(modifier = Modifier.width(20.dp))
                Text(text)
            }
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
        },
        dismissButton = {
        }
    )
}


@Composable
fun SimpleMessageDialog(
    icon: ImageVector? = null,
    title: String = "",
    text: String = "",
    confirmText: String = "确认",
    onDismissRequest: () -> Unit = {},
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        icon = { icon?.let { Icon(imageVector = it, contentDescription = null) } },
        title = { Text(text = title) },
        text = { Text(text = text) },
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = confirmText)
            }
        }
    )
}

@Composable
fun MessageDialog(
    icon: ImageVector? = null,
    title: String = "",
    text: String = "",
    cancelButtonText: String = "",
    confirmButtonText: String = "",
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
                Text(text = confirmButtonText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text(text = cancelButtonText)
            }
        }
    )
}
