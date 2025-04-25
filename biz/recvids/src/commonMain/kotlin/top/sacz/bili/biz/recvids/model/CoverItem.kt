package top.sacz.bili.biz.recvids.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class Args(
    @SerialName("aid") val aid: Long = 0,
    @SerialName("rid") val rid: Int = 0,
    @SerialName("rname") val rname: String = "",
    @SerialName("tid") val tid: Int = 0,
    @SerialName("tname") val tname: String = "",
    @SerialName("up_id") val upId: Long = 0,//up主id
    @SerialName("up_name") val upName: String = ""//up主名称
)

@Immutable
@Serializable
data class DescButton(
    @SerialName("event") val event: String = "",
    @SerialName("text") val text: String = "", // UP主名称
    @SerialName("type") val type: Int = 0, // 类型，1表示UP主
    @SerialName("uri") val uri: String = "" // 跳转链接
)

@Immutable
@Serializable
data class GotoIcon(
    @SerialName("icon_height") val iconHeight: Int = 0,
    @SerialName("icon_night_url") val iconNightUrl: String = "",
    @SerialName("icon_url") val iconUrl: String = "",
    @SerialName("icon_width") val iconWidth: Int = 0
)


@Immutable
@Serializable
data class RCmdReasonStyle(
    @SerialName("bg_color")
    val bgColor: String,
    @SerialName("bg_color_night")
    val bgColorNight: String,
    @SerialName("bg_style")
    val bgStyle: Int,
    @SerialName("border_color")
    val borderColor: String,
    @SerialName("border_color_night")
    val borderColorNight: String,
    @SerialName("text")
    val text: String,
    @SerialName("text_color")
    val textColor: String,
    @SerialName("text_color_night")
    val textColorNight: String
)

@Immutable
@Serializable
data class PlayerArgs(
    @SerialName("aid") val aid: Long = 0, // 视频aid
    @SerialName("cid") val cid: Long = 0, // 视频cid
    @SerialName("duration") val duration: Int = 0, // 视频长度，单位为秒
    @SerialName("type") val type: String = ""
)

@Immutable
@Serializable
data class ThreePoint(
    @SerialName("dislike_reasons") val dislikeReasons: List<DislikeReason> = emptyList(),
    @SerialName("feedbacks") val feedbacks: List<Feedback> = emptyList(),
    @SerialName("watch_later") val watchLater: Int = 0
)

@Immutable
@Serializable
data class ThreePointV2(
    @SerialName("icon") val icon: String = "",
    @SerialName("reasons") val reasons: List<Reason> = emptyList(),
    @SerialName("subtitle") val subtitle: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("type") val type: String = ""
)

@Immutable
@Serializable
data class DislikeReason(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)

@Immutable
@Serializable
data class Feedback(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)

@Immutable
@Serializable
data class Reason(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)