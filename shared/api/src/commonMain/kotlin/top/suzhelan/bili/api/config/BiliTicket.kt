package top.suzhelan.bili.api.config

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.serialization.kotlinx.json.json
import korlibs.crypto.HMAC
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.storage.Storage
import top.suzhelan.bili.storage.ext.contains
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


/**
 * x-bili-ticket 添加在请求头中
 */
@Serializable
data class BiliTicketData(
    @SerialName("ticket")
    val ticket: String,
    @SerialName("created_at")
    val createdAt: Long,//创建时间 秒级
    @SerialName("ttl")
    val ttl: Int,//过期时间 三天 259200
    @SerialName("nav")
    val nav: Nav
) {
    @Serializable
    data class Nav(
        @SerialName("img")
        val img: String, // https://i0.hdslb.com/bfs/wbi/7cd084941338484aae1ad9425b84077c.png
        @SerialName("sub")
        val sub: String // https://i0.hdslb.com/bfs/wbi/4932caff0ff746eab6f01bf08b70ac45.png
    )
}

object BiliTicket {
    suspend fun getBiliTickerData(): BiliTicketData {
        return Storage("biliticket").run {
            // 尝试获取有效缓存
            getValidTicket()?.let { return it }

            // 生成新ticket并缓存
            BiliTicketGenerator().generateTicket().data.also {
                putObject("ticket", it)
            }
        }
    }

    private fun Storage.getValidTicket(): BiliTicketData? {
        return takeIf { contains("ticket") }
            ?.getObjectOrNull<BiliTicketData>("ticket")
            ?.takeUnless { it.isExpired }
    }

    // 扩展属性优化过期判断
    private val BiliTicketData.isExpired: Boolean
        @OptIn(ExperimentalTime::class)
        get() = createdAt + ttl <= Clock.System.now().epochSeconds

}

private class BiliTicketGenerator {
    companion object {
        private const val SECRET = "XgwSnGZ1p"
        private const val KEY_ID = "ec02"
    }

    // HMAC-SHA256 替代实现
    private fun hmacSha256(message: String): String {
        return HMAC.hmacSHA256(
            key = SECRET.encodeToByteArray(),
            data = message.encodeToByteArray()
        ).hexLower
    }

    @OptIn(ExperimentalTime::class)
    suspend fun generateTicket(csrf: String = ""): BiliResponse.Success<BiliTicketData> {
        val timestamp = Clock.System.now().epochSeconds
        val hexsign = hmacSha256("ts$timestamp")
        val client = HttpClient {
            install(ContentNegotiation) {
                json(Json {
                    ignoreUnknownKeys = true
                })
            }
        }
        return client.post("https://api.bilibili.com/bapis/bilibili.api.ticket.v1.Ticket/GenWebTicket") {
            parameter("key_id", KEY_ID)
            parameter("hexsign", hexsign)
            parameter("context[ts]", timestamp)
            parameter("csrf", csrf)
        }.body<BiliResponse.Success<BiliTicketData>>()
    }
}
