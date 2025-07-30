package top.sacz.bili.shared.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import top.sacz.bili.shared.common.ui.dialog.DialogState

/**
 * 常用设计架构
 */
abstract class BaseViewModel : ViewModel() {

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

}