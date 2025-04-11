package top.sacz.bili.api

import kotlinx.serialization.Serializable


sealed class Response<out T> {
    data object Loading : Response<Nothing>()
    @Serializable
    data class Success<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T
    ) : Response<T>()
    data class Error(val code: Int, val msg: String, val cause : Throwable = ApiException(
        code, msg,
        cause = Throwable()
    )) : Response<Nothing>()
}

