package top.sacz.bili.biz.biliplayer.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.biliplayer.entity.RecommendedVideoByVideo

class VideoInfoApi {
    /**
     * 根据主页点击进来的视频推荐
     */
    suspend fun getRecommendedVideosByVideo(
        aid: Long,
    ): BiliResponse.Success<RecommendedVideoByVideo> {
        return getKtorClient(AppConfig.APP_BASE_URL).get("/x/v2/feed/index/story") {
            parameter("aid", aid)
        }.body()
    }
}