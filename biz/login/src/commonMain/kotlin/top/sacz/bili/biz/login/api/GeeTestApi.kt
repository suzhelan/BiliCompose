package top.sacz.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.Response
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.login.model.Captcha

class GeeTestApi {
    private val baseUrl = AppConfig.LOGIN_URL

    /**
     * 进行人机验证
     */
    suspend fun captcha(): Response.Success<Captcha> {
        return getKtorClient(baseUrl).get("x/passport-login/captcha?source=main_web").body()
    }
}