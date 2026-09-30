package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `/x/web-interface/history/cursor` 的必要展示字段。 */
@Serializable
data class WatchHistoryPage(
    val cursor: Cursor = Cursor(),
    val list: List<Item> = emptyList(),
) {
    @Serializable
    data class Cursor(
        val max: Long = 0,
        val business: String = "",
        @SerialName("view_at") val viewAt: Long = 0,
    )

    @Serializable
    data class Item(
        val title: String = "",
        @SerialName("long_title") val longTitle: String = "",
        val cover: String = "",
        @SerialName("author_name") val authorName: String = "",
        @SerialName("view_at") val viewAt: Long = 0,
        val progress: Int = 0,
        val duration: Int = 0,
        @SerialName("show_title") val showTitle: String = "",
        val history: Detail = Detail(),
    )

    @Serializable
    data class Detail(
        val oid: Long = 0,
        val epid: Long = 0,
        val bvid: String = "",
        val business: String = "",
    )
}
