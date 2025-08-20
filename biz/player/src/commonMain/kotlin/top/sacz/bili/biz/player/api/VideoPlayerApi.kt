package top.sacz.bili.biz.player.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient

import top.sacz.bili.biz.player.entity.OnlineCount
import top.sacz.bili.biz.player.entity.PlayerArgsItem
import top.sacz.bili.biz.player.entity.VideoInfo
import top.sacz.bili.biz.player.entity.VideoTag

/**
 * 获取视频信息
 */
class VideoPlayerApi {

    private val client = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true
    )

    /**
     * 获取视频分P列表
     */
    suspend fun getVideoPageList(
        avid: String? = null,
        bvid: String? = null,
    ): BiliResponse.Success<List<Any>> {
        return client.get(
            "/x/player/pagelist"
        ) {
            url {
                if (avid != null) {
                    parameter("aid", avid)
                }
                if (bvid != null) {
                    parameter("bvid", bvid)
                }
            }
        }.body()
    }

    /**
     * 获取视频简介
     */
    suspend fun getVideoDescr(
        avid: String? = null,
        bvid: String? = null,
    ): BiliResponse.Success<String> {
        return client.get(
            "/x/web-interface/archive/desc"
        ) {
            url {
                if (avid != null) {
                    parameter("aid", avid)
                }
                if (bvid != null) {
                    parameter("bvid", bvid)
                }
            }
        }.body()
    }

    /**
     * 获取视频详细信息
     */
    suspend fun getVideoDetails(
        avid: String? = null,
        bvid: String? = null,
    ): BiliResponse.Success<VideoInfo> {
        return client.get(
            "/x/web-interface/view"
        ) {
            url {
                if (avid != null) {
                    parameter("aid", avid)
                }
                if (bvid != null) {
                    parameter("bvid", bvid)
                }
            }
        }.body()
    }

    /**
     * 获取视频流信息
     * 必须包含 cid+avid/bvid
     */
    suspend fun getPlayerInfo(
        avid: String? = null,
        bvid: String? = null,
        epid: String? = null,
        seasonId: String? = null,
        cid: String,
        qn: Int = 80
    ): BiliResponse.Success<PlayerArgsItem> {
        val data = mutableMapOf<String, String>().apply {
            if (avid != null) this["avid"] = avid
            if (bvid != null) this["bvid"] = bvid
            if (epid != null) this["ep_id"] = epid
            if (seasonId != null) this["season_id"] = seasonId
            this["cid"] = cid
            this["qn"] = qn.toString()
            // 获取所有格式的视频
            this["fnval"] = "4048"//不知道啥
            this["fourk"] = "1"//允许4K
            this["voice_balance"] = "1"
            this["gaia_source"] = "pre-load"
            this["web_location"] = "1550101"
            this["try_look"] = "1"//未登录时可拉取1080p画质
        }
        return client.get(
            "/x/player/wbi/playurl"
        ) {
            url {
                data.forEach { (key, value) ->
                    parameter(key, value)
                }
            }
        }.body()
    }

    /**
     * 获取视频在线观看数量文本
     */
    suspend fun getVideoOnlineCountText(
        aid: Long,
        cid: Long
    ): BiliResponse.Success<OnlineCount> {
        val appClient = getKtorClient(AppConfig.APP_BASE_URL)
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
}