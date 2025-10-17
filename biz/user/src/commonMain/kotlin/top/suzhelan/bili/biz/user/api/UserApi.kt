package top.suzhelan.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.user.entity.UserCard
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.entity.UserSpaceInfo

class UserApi {
    private val client = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true
    )

    suspend fun getUserInfo(
        mid: Long,
        isWithPhoto: Boolean = false
    ): BiliResponse.Success<UserCard> {
        return client.get("/x/web-interface/card") {
            url {
                parameter("mid", mid)
                parameter("photo", isWithPhoto)
            }
        }.body()
    }

    suspend fun getUserSpaceInfo(mid: Long): BiliResponse.Success<UserSpaceInfo> {
        val client = getKtorClient(
            baseUrl = AppConfig.API_BASE_URL,
            withWbi = true,
            withCookie = true
        )
        return client.get("/x/space/wbi/acc/info") {
            url {
                parameter("mid", mid)
            }
        }.body()
    }

    suspend fun getUserSpace(mid: Long): BiliResponse.Success<UserSpace> {
        val client = getKtorClient(
            baseUrl = AppConfig.APP_BASE_URL,
        )
        return client.get("/x/v2/space") {
            url {
                parameter("vmid", mid)
            }
        }.body()
    }
}