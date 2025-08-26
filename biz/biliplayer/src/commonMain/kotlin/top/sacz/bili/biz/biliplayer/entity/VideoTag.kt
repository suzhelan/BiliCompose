package top.sacz.bili.biz.biliplayer.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoTag(
    @SerialName("jump_url")
    val jumpUrl: String,
    @SerialName("music_id")
    val musicId: String,
    @SerialName("tag_id")
    val tagId: Int, // 12620189
    @SerialName("tag_name")
    val tagName: String, // 异度侵入
    @SerialName("tag_type")
    val tagType: String // old_channel
)