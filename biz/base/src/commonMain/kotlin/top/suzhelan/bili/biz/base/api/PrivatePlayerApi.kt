package top.suzhelan.bili.biz.base.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.base.entity.VideoPageItem

class PrivatePlayerApi {
    private val client = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true
    )
    /**
     * 获取视频分P列表(buid/aid转cid)
     */
    suspend fun getVideoPageList(
        aid: Long? = null,
        bvid: String? = null,
    ): BiliResponse.Success<List<VideoPageItem>> {
        return client.get(
            "/x/player/pagelist"
        ) {
            url {
                if (aid != null) {
                    parameter("aid", aid)
                }
                if (bvid != null) {
                    parameter("bvid", bvid)
                }
            }
        }.body()
    }
}