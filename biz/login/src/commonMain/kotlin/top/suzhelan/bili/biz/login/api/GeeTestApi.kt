package top.suzhelan.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.AppKeyType
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.login.model.Captcha

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
