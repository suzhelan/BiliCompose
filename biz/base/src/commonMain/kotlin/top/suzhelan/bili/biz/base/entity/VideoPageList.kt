package top.suzhelan.bili.biz.base.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 视频分p项
 * 即通过avid/bvid获取cid
 */
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


