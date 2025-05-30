package top.sacz.bili.biz.recvids.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.recvids.model.PopularListItem

class PopularApi {
    /**
     * web端获取热门视频
     * @param  page 页码
     * @param  size 每页数量
     */
    suspend fun fetchPopular(page: Int = 1): BiliResponse.Success<PopularListItem> {
        return getKtorClient(
            AppConfig.API_BASE_URL,
            withCookie = true
        ).get("/x/web-interface/popular") {
            url {
                parameter("pn", page)
                parameter("ps", 20)
            }
        }.body()
    }
}