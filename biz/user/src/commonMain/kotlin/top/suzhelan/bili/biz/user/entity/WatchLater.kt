package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchLaterList(
    @SerialName("count") val count: Int = 0,
    @SerialName("list") val list: List<WatchLaterVideo>? = null,
)

@Serializable
data class WatchLaterVideo(
    @SerialName("aid") val aid: Long,
    @SerialName("bvid") val bvid: String = "",
    @SerialName("cid") val cid: Long = 0,
    @SerialName("title") val title: String = "",
    @SerialName("pic") val pic: String = "",
    @SerialName("duration") val duration: Int = 0,
    @SerialName("progress") val progress: Int = 0,
    @SerialName("state") val state: Int = 0,
    @SerialName("add_at") val addAt: Long = 0,
    @SerialName("owner") val owner: Owner = Owner(),
    @SerialName("view_at") val viewAt: Long = 0,
    @SerialName("pubdate") val pubdate: Long = 0,
) {
    val isUnavailable: Boolean get() = state < 0

    @Serializable
    data class Owner(
        @SerialName("mid") val mid: Long = 0,
        @SerialName("name") val name: String = "",
    )
}
