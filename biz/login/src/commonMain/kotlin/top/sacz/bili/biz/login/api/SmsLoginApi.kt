package top.sacz.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.AppKeyType
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.api.config.BiliHeaders
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.model.SendSmsLoginCodeResult
import top.sacz.bili.shared.auth.entity.LoginResult

import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class SmsLoginApi {

    companion object {
        @OptIn(ExperimentalUuidApi::class)
        val loginSessionId = Uuid.random().toString().replace("-", "")
    }

    private val baseUrl = AppConfig.LOGIN_URL

    private val ktorClient = getKtorClient(baseUrl, AppKeyType.USER_INFO)

    /**
     * 发送短信
     * @param cid 国际冠字码
     * @param tel 手机号码
     * @param recaptchaToken 登录 API token
     * @param geeChallenge 极验 challenge
     * @param geeValidate 极验 result
     * @param geeSeccode 极验 result +'|jordan'
     */
    suspend fun sendSms(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
        recaptchaToken: String, // 登录 API token
        geeChallenge: String, // 极验 challenge
        geeValidate: String, // 极验 result
        geeSeccode: String, // 极验 result +'|jordan'
    ): BiliResponse.Success<SendSmsLoginCodeResult> {
        return ktorClient.post("/x/passport-login/sms/send") {
            contentType(ContentType.Application.FormUrlEncoded)
            val body = FormDataContent(parameters {
                append("cid", cid)
                append("tel", tel)
                append("login_session_id", loginSessionId)
                append("recaptcha_token", recaptchaToken)
                append("gee_challenge", geeChallenge)
                append("gee_validate", geeValidate)
                append("gee_seccode", geeSeccode)
                append("channel", "bili")
                append("buvid", BiliHeaders.buvid)
                append("local_id", BiliHeaders.buvid)
                append("statistics", BiliHeaders.statistics)
            })
            setBody(body)
        }.body()
    }

    /**
     * 进行短信的人机验证
     * @param cid 国际冠字码
     * @param tel 手机号码
     */
    suspend fun getCaptchaBySms(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
    ): BiliResponse.Success<SendSmsLoginCodeResult> {
        return ktorClient.post("/x/passport-login/sms/send") {
            contentType(ContentType.Application.FormUrlEncoded)
            val body = FormDataContent(parameters {
                append("cid", cid)
                append("tel", tel)
                append("login_session_id", loginSessionId)
                append("channel", "bili")
                append("buvid", BiliHeaders.buvid)
                append("local_id", BiliHeaders.buvid)
                append("statistics", BiliHeaders.statistics)
            })
            setBody(body)
        }.body()
    }

    /**
     * 获取国家代码(国际冠字码)
     */
    suspend fun getCountryCode(): BiliResponse.Success<CountryList> {
        return ktorClient.get("/web/generic/country/list").body()
    }


    /**
     * 短信登录
     * @param cid 国际冠字码
     * @param tel 手机号码
     * @param code 验证码
     * @param captchaKey 验证码 key
     */
    suspend fun smsLogin(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
        code: String,
        captchaKey: String,
    ): BiliResponse.Success<LoginResult> {
        return ktorClient.post("/x/passport-login/login/sms") {
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(FormDataContent(parameters {
                append("cid", cid)
                append("tel", tel)
                append("login_session_id", loginSessionId)
                append("code", code)
                append("captcha_key", captchaKey)
            }))
        }.body()
    }
}