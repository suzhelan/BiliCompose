package top.sacz.bili.api


import io.ktor.client.HttpClient
import io.ktor.client.plugins.DefaultRequest
import io.ktor.client.plugins.HttpSend
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.plugin
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.header
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders.Cookie
import io.ktor.http.HttpMethod
import io.ktor.http.URLProtocol
import io.ktor.http.parameters
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import top.sacz.bili.api.config.BiliWbi
import top.sacz.bili.api.config.commonHeaders
import top.sacz.bili.api.config.commonParams
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.common.logger.LogUtils

val HttpJsonDecoder = Json {
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
}

/**
 * 主要是做了添加常见参数和签名的拦截器
 *             参数
 *             "build" to "8410300",
 *             "c_locale" to "zh_CN",
 *             "channel" to "bili",
 *             "mobi_app" to "android",
 *             "platform" to "android",
 *             "s_locale" to "zh_CN",
 *             "statistics" to BiliHeaders.statistics,
 *             "ts" to Clock.System.now().epochSeconds.toString(),
 *             "access_key" to LoginMapper.getAccessKey()
 * @param baseUrl 请求的域名
 * @param appKeyType 签名所使用的appKey
 * @param withCookie 是否携带cookie,如果是web专属接口最好带上
 * @param withWbi 是否携带wbi鉴权,默认不带
 */
fun getKtorClient(
    baseUrl: String, appKeyType: AppKeyType = AppKeyType.APP_COMMON,
    withCookie: Boolean = false,
    withWbi: Boolean = false
): HttpClient {
    val ktorClient = HttpClient {
        //安装默认请求插件
        install(DefaultRequest) {
            url {
                protocol = URLProtocol.HTTPS
                host = baseUrl
            }
            //默认请求头
            for ((key, value) in commonHeaders) {
                header(key, value)
            }
            //带cookie请求头
            if (withCookie && LoginMapper.isLogin() && LoginMapper.getCookie().isNotEmpty()) {
                val cookie = LoginMapper.getCookie()
                header(Cookie, cookie)
            }
        }
        //安装日志插件
        install(Logging) {
            level = LogLevel.ALL
            logger = object : Logger {
                override fun log(message: String) {
                    LogUtils.d("KtorClient", message)
                }
            }
        }
        //泛型结果返回
        install(ContentNegotiation) {
            json(HttpJsonDecoder)
        }
        //设置请求超时时间
        install(HttpTimeout) {
            requestTimeoutMillis = 10000
            connectTimeoutMillis = 10000
            socketTimeoutMillis = 10000

        }
    }
    //进行签名和添加常用参数
    ktorClient.plugin(HttpSend).intercept { request ->
        val method = request.method
        when (method) {
            //GET请求
            HttpMethod.Get -> {
                //将原本的参数转换成标准map
                val rawParamMap = mutableMapOf<String, String>()
                request.url.parameters.entries().forEach {
                    rawParamMap[it.key] = it.value.first()
                }
                //添加公共参数
                rawParamMap.putAll(commonParams)
                //进行签名
                rawParamMap["appkey"] = appKeyType.appKey
                val sign = BiliSignUtils(appKeyType).sign(rawParamMap)
                rawParamMap["sign"] = sign
                //添加wbi参数
                if (withWbi) {
                    val wbiParams = BiliWbi.getWRid(rawParamMap)
                    val wts = BiliWbi.getWTs()
                    rawParamMap["w_rid"] = wbiParams
                    rawParamMap["wts"] = wts.toString()
                }
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
                paramMap["appkey"] = appKeyType.appKey
                val sign = BiliSignUtils(appKeyType).sign(paramMap)
                paramMap["sign"] = sign
                //添加wbi参数
                if (withWbi) {
                    val wbiParams = BiliWbi.getWRid(paramMap)
                    val wts = BiliWbi.getWTs()
                    paramMap["w_rid"] = wbiParams
                    paramMap["wts"] = wts.toString()
                }
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

