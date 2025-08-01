package top.sacz.bili.api.ext

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.common.logger.error
import top.sacz.bili.storage.Storage

/**
 * 获取可缓存的 MutableStateFlow
 */
inline fun <reified T> ViewModel.getCacheMutableStateFlow(cacheKey: String): MutableStateFlow<BiliResponse<T>> {
    val storage = Storage(this::class.simpleName!!)
    val cachedData: T? = storage.getObjectOrNull(cacheKey)
    return MutableStateFlow(if (cachedData != null) BiliResponse.buildSuccess(cachedData) else BiliResponse.Loading)
}

/**
 * 先查缓存 再查服务器
 * 服务器只接受正确的结果
 */
inline fun <reified T> ViewModel.launchCacheUpdateTask(
    cacheKey: String,
    stateFlow: MutableStateFlow<BiliResponse<T>>,
    crossinline call: suspend () -> BiliResponse<T>
) = viewModelScope.launch(
    CoroutineExceptionHandler { _, throwable ->
        throwable.error()
    }
) {
    val storage = Storage(this::class.simpleName!!)
    // 读取缓存
    val cachedData: T? = storage.getObjectOrNull(cacheKey)
    if (LoginMapper.isLogin() && cachedData != null) {
        stateFlow.value = BiliResponse.buildSuccess(cachedData)
    }
    // 网络请求
    val response = apiCall {
        call()
    }
    when (response) {
        is BiliResponse.Success<T> -> {
            stateFlow.value = response
            storage.putObject(cacheKey, response.data)
        }

        is BiliResponse.SuccessOrNull<T> -> {
            stateFlow.value = response
            storage.putObject(cacheKey, response.data)
        }

        else -> {}
    }
}
