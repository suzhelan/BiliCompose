package top.suzhelan.bili.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.URLBuilder
import io.ktor.http.encodeURLParameter
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.intOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import top.suzhelan.bili.api.config.WbiParams
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.storage.Storage
import kotlin.time.Clock

/** Web 查询共享的会话。不会向 Web 请求注入 App 参数、App 签名或 access_key。 */
object BiliWebClient {
    private const val API = "https://api.bilibili.com"
    private val storage by lazy { Storage("web_session") }
    private val cookieMutex = Mutex()
    private val keyMutex = Mutex()
    private var keys: WbiParams? = null
    private var keysFetchedAt = 0L
    private val client by lazy {
        HttpClient {
            install(HttpTimeout) {
                requestTimeoutMillis = 15_000
                connectTimeoutMillis = 10_000
                socketTimeoutMillis = 15_000
            }
            defaultRequest {
                header(
                    HttpHeaders.UserAgent,
                    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/130.0.0.0 Safari/537.36"
                )
                header(HttpHeaders.Referrer, "https://www.bilibili.com/")
            }
        }
    }

    suspend fun get(
        url: String,
        params: Map<String, String> = emptyMap(),
        signed: Boolean = false,
    ): JsonObject {
        val host = URLBuilder(url).host
        require(host == "bilibili.com" || host.endsWith(".bilibili.com"))
        ensureGuestCookies()
        val query = if (signed) signingKeys().enc(params) else params.entries.joinToString("&") {
            "${it.key.encodeURLParameter()}=${it.value.encodeURLParameter()}"
        }
        val response = client.get(if (query.isEmpty()) url else "$url?$query") {
            header(HttpHeaders.Cookie, cookies())
        }
        if (response.status.value == 412) throw rejection()
        if (response.status.value !in 200..299) {
            throw ApiException(
                response.status.value,
                "请求失败（${response.status.value}）",
                IllegalStateException("Web HTTP error")
            )
        }
        // suggest 等接口可能返回 JSON 但不发送 Content-Type，不能依赖内容协商。
        val body = decodeWebJson(response.bodyAsText())
        try {
            checkWebResponse(body)
        } catch (error: ApiException) {
            if (signed && error.code in listOf(-403, -352)) keyMutex.withLock { keys = null }
            throw error
        }
        return body
    }

    private suspend fun ensureGuestCookies() = cookieMutex.withLock {
        val now = Clock.System.now().epochSeconds
        if (!storage.getString("buvid3", "").isBlank() && now - storage.getLong(
                "created_at",
                0L
            ) < 7 * 86400
        ) return@withLock
        // 首页发放的 Cookie 同时包含 b_nut，优先使用同一会话的一组值。
        val response = client.get("https://www.bilibili.com/")
        val issued = response.headers.getAll(HttpHeaders.SetCookie).orEmpty().mapNotNull { header ->
            val pair = header.substringBefore(';').split('=', limit = 2)
            if (pair.size == 2) pair[0] to pair[1] else null
        }.toMap()
        var buvid3 = issued["buvid3"]
        var buvid4 = issued["buvid4"]
        if (buvid3.isNullOrBlank()) {
            val spi = decodeWebJson(client.get("$API/x/frontend/finger/spi").bodyAsText())
            checkWebResponse(spi)
            val data = spi["data"]?.jsonObject ?: throw SerializationException("缺少游客会话数据")
            buvid3 = data["b_3"]?.jsonPrimitive?.content
            buvid4 = data["b_4"]?.jsonPrimitive?.content
        }
        check(!buvid3.isNullOrBlank()) { "无法建立搜索会话，请重试" }
        storage.putString("buvid3", buvid3)
        storage.putString("buvid4", buvid4.orEmpty())
        storage.putString("b_nut", issued["b_nut"] ?: now.toString())
        storage.putLong("created_at", now)
    }

    private fun cookies(): String {
        val guest = listOf("buvid3", "buvid4", "b_nut").associateWith { storage.getString(it, "") }
        // 每次请求读取登录信息，登录或退出后无需重建客户端。登录 Cookie 优先。
        val login = if (LoginMapper.isLogin()) LoginMapper.getCookie().split(';').mapNotNull {
            val pair = it.trim().split('=', limit = 2)
            if (pair.size == 2) pair[0] to pair[1] else null
        }.toMap() else emptyMap()
        return (guest + login).filterValues { it.isNotBlank() }.entries.joinToString("; ") { "${it.key}=${it.value}" }
    }

    private suspend fun signingKeys(): WbiParams = keyMutex.withLock {
        val now = Clock.System.now().epochSeconds
        keys?.takeIf { now - keysFetchedAt < 6 * 3600 }?.let { return@withLock it }
        val nav = decodeWebJson(client.get("$API/x/web-interface/nav") {
            header(
                HttpHeaders.Cookie,
                cookies()
            )
        }.bodyAsText())
        // 未登录时 code=-101，但 wbi_img 依然有效。
        val images = nav["data"]?.jsonObject?.get("wbi_img")?.jsonObject
            ?: throw SerializationException("无法获取搜索签名，请重试")

        fun key(name: String) = images[name]?.jsonPrimitive?.content
            ?.substringAfterLast('/')?.substringBefore('.')?.takeIf { it.length == 32 }
            ?: throw SerializationException("无效的搜索签名")
        WbiParams(key("img_url"), key("sub_url")).also {
            keys = it
            keysFetchedAt = now
        }
    }

    private fun rejection() = ApiException(
        -412,
        "搜索请求暂时受限，请稍后重试",
        IllegalStateException("Web request rejected")
    )
}

fun decodeWebJson(text: String): JsonObject =
    HttpJsonDecoder.parseToJsonElement(text) as? JsonObject
        ?: throw SerializationException("无效的 Web 接口响应")

/** 风控响应也可能 code=0，必须先检查 v_voucher，不能当成空结果。 */
fun checkWebResponse(body: JsonObject) {
    val code =
        body["code"]?.jsonPrimitive?.intOrNull ?: throw SerializationException("响应缺少 code")
    val data = body["data"] as? JsonObject
    if (code == -412 || code == -352 || code == -403 || data?.containsKey("v_voucher") == true) {
        throw ApiException(code.takeIf { it != 0 } ?: -352,
            "搜索请求暂时受限，请稍后重试",
            IllegalStateException("Web verification required"))
    }
    if (code != 0) {
        throw ApiException(
            code,
            body["message"]?.jsonPrimitive?.content ?: "请求失败，请重试",
            IllegalStateException("Web API error")
        )
    }
}
