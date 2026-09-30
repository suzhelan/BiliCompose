package top.suzhelan.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.user.entity.LikeVideoList
import top.suzhelan.bili.biz.user.entity.UserCard
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.entity.UserSpaceInfo
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage

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

    suspend fun getUserLikeVideoList(mid: Long, page: Int,pageSize : Int): BiliResponse.Success<LikeVideoList> {
        val appClient = getKtorClient(
            baseUrl = AppConfig.APP_BASE_URL,
        )
        return appClient.get("/x/v2/space/likearc") {
            url {
                parameter("vmid", mid)
                parameter("pn", page)
                parameter("ps", pageSize)
            }
        }.body()
    }

    /** 获取当前登录用户的观看历史，分页参数由接口返回的 cursor 续传。 */
    suspend fun getWatchHistory(
        max: Long? = null,
        business: String? = null,
        viewAt: Long? = null,
        pageSize: Int = 30,
    ): BiliResponse.Success<WatchHistoryPage> {
        return client.get("/x/web-interface/history/cursor") {
            url {
                max?.let { parameter("max", it) }
                business?.takeIf { it.isNotBlank() }?.let { parameter("business", it) }
                viewAt?.let { parameter("view_at", it) }
                parameter("type", "all")
                parameter("ps", pageSize)
            }
        }.body()
    }
}
