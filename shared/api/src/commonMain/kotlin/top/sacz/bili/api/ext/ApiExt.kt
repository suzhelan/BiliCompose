package top.sacz.bili.api.ext

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import top.sacz.bili.api.ApiException
import top.sacz.bili.api.Response
import top.sacz.bili.shared.common.logger.Logger


suspend inline fun <T> apiCall(crossinline call: suspend CoroutineScope.() -> Response<T>): Response<T> {
    return withContext(Dispatchers.IO) {
        try {
            return@withContext call()
        } catch (e: Throwable) {
            Logger.e("ApiCall", e)
            return@withContext ApiException.build(e).toResponse()
        }
    }
}

inline fun <T> Response.SuccessOrError<T>.toError(): Response.Error {
    return Response.Error(code, message)
}
