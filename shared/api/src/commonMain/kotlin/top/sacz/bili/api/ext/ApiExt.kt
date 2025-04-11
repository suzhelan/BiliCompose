package top.sacz.bili.api.ext

import io.github.aakira.napier.Napier
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import top.sacz.bili.api.ApiException
import top.sacz.bili.api.Response

suspend inline fun <T> apiCall(crossinline call: suspend CoroutineScope.() -> Response<T>): Response<T> {
    return withContext(Dispatchers.IO) {
        try {
            return@withContext call()
        } catch (e: Throwable) {
            Napier.e("HttpClient", e)
            return@withContext ApiException.build(e).toResponse()
        }
    }
}