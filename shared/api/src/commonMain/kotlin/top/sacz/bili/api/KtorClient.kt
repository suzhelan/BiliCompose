package top.sacz.bili.api


import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import top.sacz.bili.api.headers.BiliHeaders
import top.sacz.bili.api.headers.commonHeaders
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 常用参数 拼接在 url 和 body 中
 */
@OptIn(ExperimentalTime::class)
val commonParams = mutableMapOf(
//    "access_key" to "",
    "build" to "8410300",
    "c_locale" to "zh_CN",
    "channel" to "bili",
    "mobi_app" to "android",
    "platform" to "android",
    "s_locale" to "zh_CN",
    "statistics" to BiliHeaders.statistics,
    "ts" to Clock.System.now().epochSeconds.toString(),
)


fun getKtorClient(baseUrl: String, appKeyType: AppKeyType = AppKeyType.APP_COMMON): HttpClient {
    val ktorClient = HttpClient {
        //安装默认请求插件
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
            for ((key, value) in commonHeaders) {
                header(key, value)
            }
        }
        //安装日志插件
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    top.sacz.bili.shared.common.logger.Logger.d("KtorClient", message)
                }
            }
        }
        //泛型结果返回
        install(ContentNegotiation) {
            json(Json {
                //忽略未知jsonKey
                ignoreUnknownKeys = true
                //是否将null的属性写入json 默认true
                explicitNulls = true
                //是否使用默认值 默认false
                encodeDefaults = true
                //是否格式化json
                prettyPrint = true
                //宽容解析模式 可以解析不规范的json格式
                isLenient = false
            })
        }
    }
    //进行签名
    ktorClient.plugin(HttpSend).intercept { request ->
        val method = request.method
        when (method) {
            //GET请求
            HttpMethod.Get -> {
                val rawParamMap = mutableMapOf<String, String>()
                request.url.parameters.entries().forEach {
                    rawParamMap[it.key] = it.value.first()
                }
                //添加公共参数
                rawParamMap.putAll(commonParams)
                //进行签名
                val sign = BiliSignUtils(appKeyType).sign(rawParamMap)
                rawParamMap["sign"] = sign
                //构建新的请求体
                request.url.parameters.clear()
                rawParamMap.forEach {
                    request.url.parameters.append(it.key, it.value)
                }

            }
            //POST请求
            HttpMethod.Post -> {
                val rawBody = request.body as FormDataContent
                //转换成标准map
                val paramMap = mutableMapOf<String, String>()
                rawBody.formData.forEach { key, valueList ->
                    paramMap[key] = valueList.first()
                }
                //添加公共参数
                paramMap.putAll(commonParams)
                //进行签名
                val sign = BiliSignUtils(AppKeyType.USER_INFO).sign(paramMap)
                paramMap["sign"] = sign
                //构建新的请求体
                val newBody = FormDataContent(parameters {
                    paramMap.forEach {
                        append(it.key, it.value)
                    }
                })
                request.setBody(newBody)
            }
        }
        execute(request)
    }
    return ktorClient
}

