package top.sacz.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.headers.BiliHeaders
import top.sacz.bili.api.Response
import top.sacz.bili.api.getKtorClient
import top.sacz.biz.login.model.Captcha
import top.sacz.biz.login.model.CountryList
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

suspend fun main() {
    val captchaLoginApi = SmsLoginApi()
    val captcha = captchaLoginApi.getCountryCode()
    captchaLoginApi.captcha()
}

class SmsLoginApi {

    private val baseUrl = AppConfig.LOGIN_URL

    /**
     * 发送短信
     * @param cid 国际冠字码
     * @param tel 手机号码
     * @param recaptchaToken 登录 API token
     * @param geeChallenge 极验 challenge
     * @param geeValidate 极验 result
     * @param geeSeccode 极验 result +'|jordan'
     */
    @OptIn(ExperimentalUuidApi::class)
    suspend fun sendSms(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
        recaptchaToken: String, // 登录 API token
        geeChallenge: String, // 极验 challenge
        geeValidate: String, // 极验 result
        geeSeccode: String, // 极验 result +'|jordan'
    ): Response.Success<Unit> {
        return getKtorClient(baseUrl).post("/x/passport-login/sms/send") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(parameters {
                append("cid", cid)
                append("tel", tel)
                append("login_session_id", Uuid.random().toString().replace("-", ""))
                append("recaptcha_token", recaptchaToken)
                append("gee_challenge", geeChallenge)
                append("gee_validate", geeValidate)
                append("gee_seccode", geeSeccode)
                append("channel", "bili")
                append("buvid", BiliHeaders.buvid)
                append("local_id", BiliHeaders.buvid)
                append("statistics", BiliHeaders.statistics)
            })
        }.body()
    }

    /**
     * 获取国家代码(国际冠字码)
     */
    suspend fun getCountryCode(): Response.Success<CountryList> {
        return getKtorClient(baseUrl).get("/web/generic/country/list").body()
    }

    /**
     * 进行人机验证
     */
    suspend fun captcha(): Response.Success<Captcha> {
        return getKtorClient(baseUrl).get("x/passport-login/captcha?source=main_web").body()
    }
}