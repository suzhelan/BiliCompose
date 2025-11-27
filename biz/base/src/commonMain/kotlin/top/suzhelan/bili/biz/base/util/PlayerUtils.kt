package top.suzhelan.bili.biz.base.util

import androidx.compose.ui.util.fastFirst
import top.suzhelan.bili.biz.base.api.PrivatePlayerApi
import top.suzhelan.bili.biz.base.entity.VideoPageItem


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
        page : Int = 1
    ): Long {
        val api = PrivatePlayerApi()
        //检查aid和bvid至少一项不为空
        if (aid == null && bvid == null) {
            throw RuntimeException("aid和bvid都为空")
        }
        val videoPageList = api.getVideoPageList(
            aid = aid,
            bvid = bvid
        ).data
        check(videoPageList.isNotEmpty())
        return videoPageList.fastFirst {
            it.page == page
        }.cid
    }


    /**
     * 获取视频分P列表
     */
    suspend fun getVideoPageList(
        aid: Long? = null,
        bvid: String? = null,
    ): List<VideoPageItem> {
        val api = PrivatePlayerApi()
        return api.getVideoPageList(
            aid = aid,
            bvid = bvid
        ).data
    }
}