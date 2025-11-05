package top.suzhelan.bili.shared.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.shared.common.logger.error
import top.suzhelan.bili.shared.common.ui.dialog.DialogState

/**
 * 常用设计架构
 */
abstract class BaseViewModel : ViewModel() {

    private val _initLoading = MutableStateFlow(false)
    val initLoading = _initLoading.asStateFlow()
    fun setLoading(isLoading: Boolean) {
        _initLoading.value = isLoading
    }

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
    ) = viewModelScope.launch(exceptionHandler) {
        task()
    }


}