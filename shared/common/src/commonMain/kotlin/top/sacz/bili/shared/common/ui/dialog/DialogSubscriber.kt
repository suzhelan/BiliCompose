package top.sacz.bili.shared.common.ui.dialog

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import top.sacz.bili.shared.common.base.BaseViewModel

/**
 * 弹窗处理器
 * 配合BaseViewModel使用
 * 然后添加DialogHandler(BaseViewModel) ,即可在BaseViewModel中处理弹窗
 */
sealed class DialogState {
    object Dismiss : DialogState()
    class Loading(val title: String = "加载中...", val message: String = "请耐心等待") :
        DialogState()

    class Message(
        val title: String = "提示",
        val text: String = "消息",
        val icon: ImageVector? = null,
        val cancelButtonText: String = "",
        val confirmButtonText: String = "",
        val onDismissRequest: () -> Unit = {},
        val onConfirmRequest: () -> Unit = {}
    ) : DialogState()
}

@Composable
fun DialogHandler(vm: BaseViewModel) {
    val collectAsState by vm.showDialog.collectAsState()
    when (val dialogState = collectAsState) {
        is DialogState.Loading -> {
            LoadingDialog(
                title = dialogState.title,
                text = dialogState.message,
                onDismissRequest = { }
            )
        }

        is DialogState.Message -> {
            MessageDialog(
                icon = dialogState.icon,
                title = dialogState.title,
                text = dialogState.text,
                cancelButtonText = dialogState.cancelButtonText,
                confirmButtonText = dialogState.confirmButtonText,
                onDismissRequest = dialogState.onDismissRequest,
                onConfirmRequest = dialogState.onConfirmRequest
            )
        }

        is DialogState.Dismiss -> {}
    }
}