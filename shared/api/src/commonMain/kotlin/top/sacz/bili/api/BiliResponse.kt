package top.sacz.bili.api

import kotlinx.serialization.Serializable


sealed class BiliResponse<out T> {
    data object Loading : BiliResponse<Nothing>()
    data object Wait : BiliResponse<Nothing>()

    @Serializable
    data class Success<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T
    ) : BiliResponse<T>()

    @Serializable
    data class SuccessOrNull<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T?
    ) : BiliResponse<T>()

    data class Error(
        val code: Int, val msg: String, val cause: Throwable = ApiException(
            code, msg,
            cause = Throwable()
        )
    ) : BiliResponse<Nothing>()
}

