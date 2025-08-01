package top.sacz.bili.shared.common.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.shared.common.logger.error
import top.sacz.bili.shared.common.ui.dialog.DialogState

/**
 * 常用设计架构
 */
abstract class BaseViewModel : ViewModel() {

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.error()
    }

    val showDialog = MutableStateFlow<DialogState>(DialogState.Dismiss)

    fun dismissDialog() {
        showDialog.value = DialogState.Dismiss
    }

    fun updateDialog(dialogState: DialogState) {
        showDialog.value = dialogState
    }

    fun setShowLoading() {
        showDialog.value = DialogState.Loading()
    }

    fun launchTask(
        task: suspend CoroutineScope.() -> Unit
    ) = viewModelScope.launch(exceptionHandler) {
        task()
    }


}