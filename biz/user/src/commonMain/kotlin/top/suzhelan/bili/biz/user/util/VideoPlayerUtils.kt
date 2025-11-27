package top.suzhelan.bili.biz.user.util

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient

object VideoPlayerUtils {
    private val client = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true
    )

    @Serializable
    data class VideoPageItem(
        @SerialName("cid")
        val cid: Long, // 35039678
        @SerialName("dimension")
        val dimension: Dimension,
        @SerialName("duration")
        val duration: Int, // 210
        @SerialName("from")
        val from: String, // vupload
        @SerialName("page")
        val page: Int, // 3
        @SerialName("part")
        val part: String, // 02. 火柴人与动画师 II
        @SerialName("vid")
        val vid: String,
        @SerialName("weblink")
        val weblink: String
    ) {
        @Serializable
        data class Dimension(
            @SerialName("height")
            val height: Int, // 1080
            @SerialName("rotate")
            val rotate: Int, // 0
            @SerialName("width")
            val width: Int // 1484
        )
    }

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