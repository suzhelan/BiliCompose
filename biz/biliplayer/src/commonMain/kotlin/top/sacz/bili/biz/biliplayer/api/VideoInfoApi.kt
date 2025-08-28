package top.sacz.bili.biz.biliplayer.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.biliplayer.entity.OnlineCount
import top.sacz.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.sacz.bili.biz.biliplayer.entity.VideoTag

class VideoInfoApi {
    private val client = getKtorClient(AppConfig.API_BASE_URL)
    private val appClient = getKtorClient(AppConfig.APP_BASE_URL)

    /**
     * 根据主页点击进来的视频推荐
     */
    suspend fun getRecommendedVideosByVideo(
        aid: Long,
    ): BiliResponse.Success<RecommendedVideoByVideo> {
        return appClient.get("/x/v2/feed/index/story") {
            parameter("aid", aid)
        }.body()
    }

    /**
     * 获取视频在线观看数量文本
     */
    suspend fun getVideoOnlineCountText(
        aid: Long,
        cid: Long
    ): BiliResponse.Success<OnlineCount> {
        return appClient.get(
            "/x/v2/view/video/online"
        ) {
            //添加视频信息参数
            parameter("aid", aid)
            parameter("cid", cid)
            //key sign ts等参数由client自行填充
        }.body()
    }

    /**
     * 获取视频标签信息
     */
    suspend fun getVideoTags(
        aid: Long? = null,
        bvid: String? = null,
        cid: Long? = null
    ): BiliResponse.Success<List<VideoTag>> {
        return client.get(
            "/x/web-interface/view/detail/tag"
        ) {
            url {
                if (aid != null) {
                    parameter("aid", aid)
                }
                if (bvid != null) {
                    parameter("bvid", bvid)
                }
                if (cid != null) {
                    parameter("cid", cid)
                }
            }
        }.body()
    }

    /**
     * 获取视频简介
     */
    suspend fun getVideoDesc(
        aid: String? = null,
        bvid: String? = null,
    ): BiliResponse.Success<String> {
        return client.get(
            "/x/web-interface/archive/desc"
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

    /**
     * 获取视频是否点赞,只能知道近期是否点赞，超过半年可能就返回0
     **/
    suspend fun isLike(
        aid: Long? = null,
        bvid: String? = null,
    ): BiliResponse.Success<Boolean> {
        val response: BiliResponse.SuccessOrNull<Int> = client.get(
            "/x/web-interface/archive/has/like"
        ) {
            if (aid != null) {
                parameter("aid", aid)
            }
            if (bvid != null) {
                parameter("bvid", bvid)
            }
        }.body()
        return BiliResponse.Success(
            code = response.code,
            message = response.message,
            ttl = response.ttl,
            data = response.data != null && response.data != 0
        )
    }

    /**
     * 点赞或者取消点赞
     **/
    suspend fun like(
        aid: Long,
        isLike: Boolean
    ): BiliResponse.Success<String> {
        val response: BiliResponse.SuccessOrNull<String> = appClient.get(
            "/x/v2/view/like"
        ) {
            parameter("aid", aid)
            parameter("like", if (isLike) 0 else 1)
        }.body()
        return BiliResponse.Success(
            code = response.code,
            message = response.message,
            ttl = response.ttl,
            data = response.data ?: ""
        )
    }
}