package top.sacz.bili.shared.navigation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

abstract class BaseScreenViewModel : ScreenModel {
    fun launchTask(
        invokeOnCompletion: (Throwable?) -> Unit = {},
        task: suspend CoroutineScope.() -> Unit,
    ) {
        screenModelScope.launch {
            task()
        }.invokeOnCompletion {
            invokeOnCompletion(it)
        }
    }
}

