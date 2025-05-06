package top.sacz.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.AppKeyType
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.login.model.Captcha

class GeeTestApi {
    private val baseUrl = AppConfig.LOGIN_URL

    private val ktorClient = getKtorClient(baseUrl, AppKeyType.USER_INFO)

    /**
     * 进行人机验证
     */
    suspend fun captcha(): BiliResponse.Success<Captcha> {
        return ktorClient.get("x/passport-login/captcha?source=main_web").body()
    }


}
