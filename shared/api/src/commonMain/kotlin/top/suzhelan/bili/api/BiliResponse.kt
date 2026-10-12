package top.suzhelan.bili.api

import androidx.compose.runtime.Composable
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull


sealed class BiliResponse<out T> {
    data object Loading : BiliResponse<Nothing>()
    data object Wait : BiliResponse<Nothing>()

    /**
     * 成功返回数据
     * 包含data
     */
    @Serializable
    data class Success<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T
    ) : BiliResponse<T>()

    /**
     * 获取数据成功，但数据为null,或者没返回data字段
     */
    @Serializable
    data class SuccessOrNull<T>(
        val code: Int,
        val message: String,
        val ttl: Int,
        val data: T? = null
    ) : BiliResponse<T>()

    data class Error(
        val code: Int, val msg: String, val cause: Throwable = ApiException(
            code, msg,
            cause = Throwable()
        )
    ) : BiliResponse<Nothing>()


    companion object {
        fun <T> buildSuccess(data: T): BiliResponse<T> {
            return Success(0, "Cache", 1, data)
        }
    }
}

fun <T> BiliResponse<T>.isSuccess(): Boolean = when (this) {
    is BiliResponse.Success -> code == 0
    is BiliResponse.SuccessOrNull -> code == 0
    else -> false
}

fun <T> BiliResponse<T>.isLoading(): Boolean = this is BiliResponse.Loading
fun <T> BiliResponse<T>.isWait(): Boolean = this is BiliResponse.Wait
fun <T> BiliResponse<T>.isError(): Boolean = this is BiliResponse.Error

fun <T> BiliResponse<T>.getOrThrow(): T {
    return when (this) {
        is BiliResponse.Success -> data
        is BiliResponse.SuccessOrNull -> data!!
        is BiliResponse.Error -> throw cause
        is BiliResponse.Loading -> throw IllegalStateException("Loading")
        is BiliResponse.Wait -> throw IllegalStateException("Wait")
    }
}

/** 先检查业务码，避免失败响应缺少 data 时被误报为反序列化失败。 */
suspend inline fun <reified T> HttpResponse.bodyBiliSuccess(): BiliResponse.Success<T> =
    HttpJsonDecoder.decodeBiliSuccess(body<JsonObject>())

inline fun <reified T> Json.decodeBiliSuccess(response: JsonObject): BiliResponse.Success<T> {
    val code = (response["code"] as? JsonPrimitive)?.intOrNull
        ?: throw SerializationException("响应缺少有效的 code")
    if (code != 0) {
        val message = (response["message"] as? JsonPrimitive)?.contentOrNull
            ?.takeIf { it.isNotBlank() } ?: "请求失败"
        throw ApiException(code, message, IllegalStateException("业务请求失败：$code"))
    }
    return decodeFromJsonElement<BiliResponse.Success<T>>(response)
}

/**
 * DSL构建器风格的状态监听器
 */
@Suppress("ComposableNaming")
@Composable
fun <T> BiliResponse<T>.registerStatusListener(block: StatusListenerBuilder<T>.() -> Unit) {
    val builder = StatusListenerBuilder<T>().apply(block)
    when (this) {
        is BiliResponse.Loading -> builder.onLoading?.invoke()
        is BiliResponse.Wait -> builder.onWait?.invoke()
        is BiliResponse.Success -> builder.onSuccess?.invoke(data)
        is BiliResponse.SuccessOrNull -> builder.onSuccessOrNull?.invoke(data)
        is BiliResponse.Error -> builder.onError?.invoke(code, msg, cause)
    }
}

class StatusListenerBuilder<T> {
    var onLoading: (@Composable () -> Unit)? = null
    var onWait: (@Composable () -> Unit)? = null
    var onSuccess: (@Composable (data: T) -> Unit)? = null
    var onSuccessOrNull: (@Composable (data: T?) -> Unit)? = null
    var onError: (@Composable (code: Int, msg: String, cause: Throwable) -> Unit)? = null
    fun onLoading(block: @Composable () -> Unit) {
        onLoading = block
    }

    fun onWait(block: @Composable () -> Unit) {
        onWait = block
    }

    fun onSuccess(block: @Composable (data: T) -> Unit) {
        onSuccess = block
    }

    fun onSuccessOrNull(block: @Composable (data: T?) -> Unit) {
        onSuccessOrNull = block
    }

    fun onError(block: @Composable (code: Int, msg: String, cause: Throwable) -> Unit) {
        onError = block
    }
}
