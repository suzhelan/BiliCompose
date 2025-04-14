package top.sacz.bili.api


import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.request.header
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import top.sacz.bili.api.headers.commonHeaders
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
val commonParams = mutableMapOf(
    "access_key" to "",
    "build" to "1462100",
    "c_locale" to "zh_CN",
    "channel" to "yingyongbao",
    "mobi_app" to "android_hd",
    "platform" to "android",
    "s_locale" to "zh_CN",
    "statistics" to AppConfig.statistics,
    "ts" to Clock.System.now().epochSeconds.toString(),
)


fun getKtorClient(baseUrl: String): HttpClient {
    return HttpClient {
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
            for ((key, value) in commonHeaders) {
                header(key, value)
            }
        }
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    top.sacz.bili.shared.common.logger.Logger.d("KtorClient", message)
                }
            }
        }

        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                isLenient = true
                explicitNulls = false
            })
        }
    }
}

