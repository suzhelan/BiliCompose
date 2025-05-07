package top.sacz.bili.api

import kotlinx.serialization.Serializable


sealed class BiliResponse<out T> {
    data object Loading : BiliResponse<Nothing>()

    @Serializable
    data class Success<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T
    ) : BiliResponse<T>()

    data class Error(
        val code: Int, val msg: String, val cause: Throwable = ApiException(
            code, msg,
            cause = Throwable()
        )
    ) : BiliResponse<Nothing>()
}

