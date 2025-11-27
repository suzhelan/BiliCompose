package top.suzhelan.bili.biz.biliplayer.util

import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.biliplayer.entity.VideoPageItem

/**
 * 视频工具类
 */
object PlayerUtils {

    /**
     * 通过Aid或Bvid获取Cid
     */
    suspend fun getCidByAidOrBvid(
        aid: Long? = null,
        bvid: String? = null,
    ): Long {
        val api = VideoPlayerApi()
        val videoPageList = api.getVideoPageList(
            aid = aid,
            bvid = bvid
        ).data
        check(videoPageList.isNotEmpty())
        return videoPageList.first().cid
    }


    /**
     * 获取视频分P列表
     */
    suspend fun getVideoPageList(
        aid: Long? = null,
        bvid: String? = null,
    ): List<VideoPageItem> {
        val api = VideoPlayerApi()
        return api.getVideoPageList(
            aid = aid,
            bvid = bvid
        ).data
    }
}