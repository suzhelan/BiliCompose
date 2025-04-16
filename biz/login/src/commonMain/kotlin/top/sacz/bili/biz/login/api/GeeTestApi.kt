package top.sacz.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Url
import io.ktor.http.contentType
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.AppKeyType
import top.sacz.bili.api.Response
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.api.headers.BiliHeaders
import top.sacz.bili.biz.login.model.Captcha
import top.sacz.bili.biz.login.model.SmsLoginToken
import kotlin.uuid.ExperimentalUuidApi

class GeeTestApi {
    private val baseUrl = AppConfig.LOGIN_URL

    private val ktorClient = getKtorClient(baseUrl, AppKeyType.USER_INFO)

    /**
     * 进行人机验证
     */
    suspend fun captcha(): Response.Success<Captcha> {
        return ktorClient.get("x/passport-login/captcha?source=main_web").body()
    }

    /**
     * 申请短信验证
     * @param cid 国际冠字码
     * @param tel 手机号码
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun getCaptchaBySms(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
    ): Response.Success<SmsLoginToken> {
        return ktorClient.post("/x/passport-login/sms/send") {
            contentType(ContentType.Application.FormUrlEncoded)
            val body = FormDataContent(parameters {
                append("cid", cid)
                append("tel", tel)
                append("login_session_id", SmsLoginApi.loginSessionId)
                append("channel", "bili")
                append("buvid", BiliHeaders.buvid)
                append("local_id", BiliHeaders.buvid)
                append("statistics", BiliHeaders.statistics)
            })
            setBody(body)
        }.body()
    }
}

suspend fun main() {
    val api = GeeTestApi()
    val geetest = api.getCaptchaBySms("86", "17322302215")
    val urlParams = Url(geetest.data.recaptchaUrl).parameters
    val paramMap = mutableMapOf<String, String>()
    urlParams.entries().forEach {
        paramMap[it.key] = it.value.first()
    }
    paramMap.forEach {
        println("${it.key}: ${it.value}")
    }
}