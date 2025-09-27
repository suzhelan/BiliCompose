package top.suzhelan.bili.api.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 调用API接口 并在失败时自动返回Error
 */
suspend inline fun <T> apiCall(crossinline call: suspend CoroutineScope.() -> BiliResponse<T>): BiliResponse<T> {
    return withContext(Dispatchers.IO) {
        try {
            return@withContext call()
        } catch (e: Throwable) {
            LogUtils.e("ApiCall", e)
            return@withContext ApiException.build(e).toResponse()
        }
    }
}

