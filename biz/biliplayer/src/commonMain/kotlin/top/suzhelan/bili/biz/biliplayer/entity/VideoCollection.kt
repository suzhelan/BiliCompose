package top.suzhelan.bili.biz.biliplayer.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 视频详情接口返回的合集，保留分部和视频的原始顺序。 */
@Serializable
data class VideoCollection(
    @SerialName("id")
    val id: Long = 0,
    @SerialName("title")
    val title: String = "",
    @SerialName("ep_count")
    val episodeCount: Int = 0,
    @SerialName("sections")
    val sections: List<Section> = emptyList(),
) {
    @Serializable
    data class Section(
        @SerialName("id")
        val id: Long = 0,
        @SerialName("title")
        val title: String = "",
        @SerialName("episodes")
        val episodes: List<Episode> = emptyList(),
    )

    @Serializable
    data class Episode(
        @SerialName("id")
        val id: Long = 0,
        @SerialName("aid")
        val aid: Long = 0,
        @SerialName("bvid")
        val bvid: String = "",
        @SerialName("cid")
        val cid: Long = 0,
        @SerialName("title")
        val title: String = "",
        @SerialName("arc")
        val arc: Archive? = null,
    )

    @Serializable
    data class Archive(
        @SerialName("pic")
        val pic: String = "",
        @SerialName("duration")
        val duration: Int = 0,
        @SerialName("pubdate")
        val pubdate: Long = 0,
        @SerialName("stat")
        val stat: Statistics = Statistics(),
    )

    @Serializable
    data class Statistics(
        @SerialName("view")
        val view: Long = 0,
        @SerialName("danmaku")
        val danmaku: Long = 0,
    )
}
