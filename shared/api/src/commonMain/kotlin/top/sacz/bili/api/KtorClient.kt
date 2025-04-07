package top.sacz.bili.api

import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.http.headers
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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


fun getKtorClient(baseUrl: String) : HttpClient {
    return HttpClient {
        headers {
            for ((key, value) in commonHeaders) {
                append(key, value)
            }
        }
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
        }
        install(Logging) {
            level = LogLevel.ALL
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
val ktorClient = HttpClient {
    install(DefaultRequest) {
        url {
            protocol = URLProtocol.HTTPS
            host = AppConfig.APP_BASE_URL
        }
    }
    install(Logging) {
        level = LogLevel.ALL
    }
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            isLenient = true
            explicitNulls = false
        })
    }
}
