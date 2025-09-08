package top.sacz.bili.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.shared.common.logger.error
import top.sacz.bili.shared.common.ui.dialog.DialogState
import top.sacz.bili.shared.common.ui.dialog.LoadingDialog
import top.sacz.bili.shared.common.ui.dialog.MessageDialog

/**
 * 结构和[top.sacz.bili.shared.common.base.BaseViewModel]一样,不依赖BaseViewModel是因为会依赖冲突错误
 */
abstract class BaseScreenViewModel : ScreenModel {
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.error()
    }

    private val _showDialog = MutableStateFlow<DialogState>(DialogState.Dismiss)
    val showDialog = _showDialog.asStateFlow()
    fun dismissDialog() {
        _showDialog.value = DialogState.Dismiss
    }

    fun updateDialog(dialogState: DialogState) {
        _showDialog.value = dialogState
    }

    fun setShowLoading() {
        _showDialog.value = DialogState.Loading()
    }

    fun showMessageDialog(title: String = "提示", message: String) {
        _showDialog.value = DialogState.Message(
            title = title,
            text = message,
            confirmButtonText = "确定",
            onDismissRequest = {
                dismissDialog()
            },
            onConfirmRequest = {
                dismissDialog()
            }
        )
    }

    fun launchTask(
        task: suspend CoroutineScope.() -> Unit
    ) = screenModelScope.launch(exceptionHandler) {
        task()
    }
}

@Composable
fun DialogHandler(vm: BaseScreenViewModel) {
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


