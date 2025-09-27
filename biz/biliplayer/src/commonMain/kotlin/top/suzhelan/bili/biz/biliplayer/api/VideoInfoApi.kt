package top.suzhelan.bili.biz.biliplayer.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.boolean
import kotlinx.serialization.json.jsonPrimitive
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.biliplayer.entity.OnlineCount
import top.suzhelan.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.suzhelan.bili.biz.biliplayer.entity.VideoInfo
import top.suzhelan.bili.biz.biliplayer.entity.VideoTag

class VideoInfoApi {
    private val apiClient = getKtorClient(AppConfig.API_BASE_URL)
    private val appClient = getKtorClient(AppConfig.APP_BASE_URL)

    /**
     * 获取视频详细信息
     */
    suspend fun getVideoDetails(
        aid: Long? = null,
        bvid: String? = null,
    ): BiliResponse.Success<VideoInfo> {
        return apiClient.get(
            "/x/web-interface/view"
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
        return apiClient.get(
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
        return apiClient.get(
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
        val response: BiliResponse.SuccessOrNull<Int> = apiClient.get(
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
     * 视频是否投币
     * @return 已投硬币数量
     */
    suspend fun isCoins(
        aid: Long? = null,
        bvid: String? = null
    ): BiliResponse.Success<Int> {
        val response: BiliResponse.SuccessOrNull<Map<String, Int>> = apiClient.get(
            "/x/web-interface/archive/coins"
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
            data = response.data?.get("multiply") ?: 0
        )
    }

    /**
     * 视频是否被收藏
     */
    suspend fun isFavoured(
        aid: Long,
    ): BiliResponse.Success<Boolean> {
        val response: BiliResponse.SuccessOrNull<JsonObject> = apiClient.get(
            "/x/v2/fav/video/favoured"
        ) {
            parameter("aid", aid)
        }.body()
        /*
        "count": 1,
		"favoured": true
         */
        return BiliResponse.Success(
            code = response.code,
            message = response.message,
            ttl = response.ttl,
            data = response.data?.getValue("favoured")?.jsonPrimitive?.boolean ?: false
        )
    }

    /**
     * 点赞或者取消点赞
     **/
    suspend fun like(
        aid: Long,
        isLike: Boolean
    ): BiliResponse.Success<String> {
        val response: BiliResponse.SuccessOrNull<Map<String, String>> = appClient.post(
            "/x/v2/view/like"
        ) {
            setBody(FormDataContent(parameters {
                append("aid", aid.toString())
                append("like", (if (isLike) 0 else 1).toString())
            }))
        }.body()
        return BiliResponse.Success(
            code = response.code,
            message = response.message,
            ttl = response.ttl,
            data = response.data?.get("toast") ?: "异常 未响应data"
        )
    }

    /**
     * 投币
     */
    suspend fun coin(
        aid: Long,
        multiply: Int,
        selectLike: Boolean = false
    ): BiliResponse.Success<Boolean> {
        val response: BiliResponse.SuccessOrNull<JsonObject> = appClient.post(
            "/x/v2/view/coin/add"
        ) {
            setBody(FormDataContent(parameters {
                append("aid", aid.toString())
                append("multiply", multiply.toString())
                append("select_like", if (selectLike) "1" else "0")
            }))
        }.body()
        return BiliResponse.Success(
            code = response.code,
            message = response.message,
            ttl = response.ttl,
            data = response.data?.getValue("like")?.jsonPrimitive!!.boolean
        )
    }

    /**
     * 报告当前观看进度 每秒上报一次就可以,也会出现在历史观看记录,以及获取视频流中的last_play_time
     **/
    suspend fun reportViewingProgress(
        aid: Long,
        cid: Long,
        seconds: Long
    ) {
        apiClient.post(
            "/x/v2/history/report"
        ) {
            setBody(FormDataContent(parameters {
                append("aid", aid.toString())
                append("cid", cid.toString())
                append("progress", seconds.toString())
            }))
        }
    }
}