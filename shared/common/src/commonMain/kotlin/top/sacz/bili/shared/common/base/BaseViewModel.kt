package top.sacz.bili.shared.common.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import top.sacz.bili.shared.common.ui.dialog.DialogState

/**
 * 常用设计架构
 */
abstract class BaseViewModel : ViewModel() {
    val isShowLoading = MutableStateFlow(false)
    fun setShowLoading(show: Boolean) {
        isShowLoading.value = show
    }

    val showDialog = MutableStateFlow<DialogState>(DialogState.Dismiss)

    fun dismissDialog() {
        showDialog.value = DialogState.Dismiss
    }


}