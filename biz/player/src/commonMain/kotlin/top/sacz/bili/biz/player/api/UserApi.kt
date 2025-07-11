package top.sacz.bili.biz.player.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.player.model.UserCard

class UserApi {
    private val client = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true
    )

    suspend fun getUserInfo(
        mid: String,
        isWithPhoto: Boolean = false
    ): BiliResponse.Success<UserCard> {
        return client.get("/x/web-interface/card") {
            url {
                parameter("mid", mid)
                parameter("photo", isWithPhoto)
            }
        }.body()
    }
}