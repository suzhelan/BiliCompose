package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** `/x/web-interface/history/cursor` 的必要展示字段。 */
@Serializable
data class WatchHistoryPage(
    @SerialName("cursor") val cursor: Cursor = Cursor(),
    @SerialName("list") val list: List<Item> = emptyList(),
) {
    @Serializable
    data class Cursor(
        @SerialName("max") val max: Long = 0,
        @SerialName("business") val business: String = "",
        @SerialName("view_at") val viewAt: Long = 0,
    )

    @Serializable
    data class Item(
        @SerialName("title") val title: String = "",
        @SerialName("long_title") val longTitle: String = "",
        @SerialName("cover") val cover: String = "",
        @SerialName("author_name") val authorName: String = "",
        @SerialName("view_at") val viewAt: Long = 0,
        @SerialName("progress") val progress: Int = 0,
        @SerialName("duration") val duration: Int = 0,
        @SerialName("show_title") val showTitle: String = "",
        @SerialName("kid") val kid: Long = 0,
        @SerialName("history") val history: Detail = Detail(),
        @SerialName("pubdate") val pubdate: Long = 0,
    )

    @Serializable
    data class Detail(
        @SerialName("oid") val oid: Long = 0,
        @SerialName("epid") val epid: Long = 0,
        @SerialName("bvid") val bvid: String = "",
        @SerialName("business") val business: String = "",
    )
}
