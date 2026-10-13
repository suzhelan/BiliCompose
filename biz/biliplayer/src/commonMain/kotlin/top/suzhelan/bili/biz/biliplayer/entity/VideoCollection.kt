package top.suzhelan.bili.biz.biliplayer.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** 视频详情接口返回的合集，保留分部和视频的原始顺序。 */
@Serializable
data class VideoCollection(
    val id: Long = 0,
    val title: String = "",
    @SerialName("ep_count")
    val episodeCount: Int = 0,
    val sections: List<Section> = emptyList(),
) {
    @Serializable
    data class Section(
        val id: Long = 0,
        val title: String = "",
        val episodes: List<Episode> = emptyList(),
    )

    @Serializable
    data class Episode(
        val id: Long = 0,
        val aid: Long = 0,
        val bvid: String = "",
        val cid: Long = 0,
        val title: String = "",
        val arc: Archive? = null,
    )

    @Serializable
    data class Archive(
        val pic: String = "",
        val duration: Int = 0,
        val pubdate: Long = 0,
        val stat: Statistics = Statistics(),
    )

    @Serializable
    data class Statistics(
        val view: Long = 0,
        val danmaku: Long = 0,
    )
}
