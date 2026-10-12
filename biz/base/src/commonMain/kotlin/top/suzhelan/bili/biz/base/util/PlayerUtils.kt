package top.suzhelan.bili.biz.base.util

import top.suzhelan.bili.biz.base.api.PrivatePlayerApi


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
        page: Int = 1,
    ): Long {
        val api = PrivatePlayerApi()
        //检查aid和bvid至少一项不为空
        require(aid != null || !bvid.isNullOrBlank()) { "aid和bvid都为空" }
        require(page > 0) { "分P编号必须大于0" }
        val videoPageList = api.getVideoPageList(
            aid = aid,
            bvid = bvid
        ).data
        return videoPageList.firstOrNull { it.page == page }?.cid
            ?: throw IllegalStateException("未找到第${page}分P的视频信息")
    }


}
