package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WatchLaterList(
    val count: Int = 0,
    val list: List<WatchLaterVideo>? = null,
)

@Serializable
data class WatchLaterVideo(
    val aid: Long,
    val bvid: String = "",
    val cid: Long = 0,
    val title: String = "",
    val pic: String = "",
    val duration: Int = 0,
    val progress: Int = 0,
    val state: Int = 0,
    @SerialName("add_at") val addAt: Long = 0,
    val owner: Owner = Owner(),
) {
    val isUnavailable: Boolean get() = state < 0

    @Serializable
    data class Owner(
        val mid: Long = 0,
        val name: String = "",
    )
}
