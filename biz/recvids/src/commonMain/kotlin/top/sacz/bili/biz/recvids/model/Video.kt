package top.sacz.bili.biz.recvids.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoList(
    @SerialName("items")
    val items: List<Video> = emptyList(),
)

@Serializable
data class Video(
    @SerialName("args") val args: Args = Args(),
    @SerialName("can_play") val canPlay: Int = 0, // 是否可播放，1表示可播放
    @SerialName("card_goto") val cardGoto: String = "", // 卡片跳转类型，视频为"av"
    @SerialName("card_type") val cardType: String = "", // 卡片类型，视频为"small_cover_v2"
    @SerialName("cover") val cover: String = "", // 封面URL
    @SerialName("cover_left_1_content_description") val coverLeft1ContentDescription: String = "", // 播放量描述，如"8.9万观看"
    @SerialName("cover_left_2_content_description") val coverLeft2ContentDescription: String = "", // 弹幕数描述，如"250弹幕"
    @SerialName("cover_left_icon_1") val coverLeftIcon1: Int = 0,
    @SerialName("cover_left_icon2") val coverLeftIcon2: Int = 0,
    @SerialName("cover_left_text_1") val coverLeftText1: String = "", // 播放量，如"8.9万"
    @SerialName("cover_left_text_2") val coverLeftText2: String = "", // 弹幕数，如"250"
    @SerialName("cover_right_content_description") val coverRightContentDescription: String = "", // 视频长度描述，如"1分钟20秒"
    @SerialName("cover_right_text") val coverRightText: String = "", // 视频长度，如"1:20"
    @SerialName("desc_button") val descButton: DescButton = DescButton(), // UP主信息
    @SerialName("goto") val goto: String = "",
    @SerialName("goto_icon") val gotoIcon: GotoIcon = GotoIcon(),
    @SerialName("rcmd_reason_style") val rCmdReasonStyle: RCmdReasonStyle? = null,
    @SerialName("idx") val idx: Int = 0,
    @SerialName("official_icon") val officialIcon: Int = 0,
    @SerialName("param") val `param`: String = "", // 视频aid
    @SerialName("player_args") val playerArgs: PlayerArgs = PlayerArgs(), // 视频信息
    @SerialName("report_flow_data") val reportFlowData: String = "",
    @SerialName("talk_back") val talkBack: String = "",
    @SerialName("three_point") val threePoint: ThreePoint = ThreePoint(),
    @SerialName("three_point_v2") val threePointV2: List<ThreePointV2> = emptyList(),
    @SerialName("title") val title: String = "", // 视频标题
    @SerialName("uri") val uri: String = "" // 跳转链接
)

@Serializable
data class Args(
    @SerialName("aid") val aid: Long = 0,
    @SerialName("rid") val rid: Int = 0,
    @SerialName("rname") val rname: String = "",
    @SerialName("tid") val tid: Int = 0,
    @SerialName("tname") val tname: String = "",
    @SerialName("up_id") val upId: Long = 0,
    @SerialName("up_name") val upName: String = ""
)

@Serializable
data class DescButton(
    @SerialName("event") val event: String = "",
    @SerialName("text") val text: String = "", // UP主名称
    @SerialName("type") val type: Int = 0, // 类型，1表示UP主
    @SerialName("uri") val uri: String = "" // 跳转链接
)

@Serializable
data class GotoIcon(
    @SerialName("icon_height") val iconHeight: Int = 0,
    @SerialName("icon_night_url") val iconNightUrl: String = "",
    @SerialName("icon_url") val iconUrl: String = "",
    @SerialName("icon_width") val iconWidth: Int = 0
)

/**
 *       "rcmd_reason_style": {
 *           "text": "2万点赞",
 *           "text_color": "#FF6633",
 *           "bg_color": "#FFF1ED",
 *           "border_color": "#FFF1ED",
 *           "text_color_night": "#BF5330",
 *           "bg_color_night": "#3D2D29",
 *           "border_color_night": "#3D2D29",
 *           "bg_style": 1
 *        }
 */
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

@Serializable
data class PlayerArgs(
    @SerialName("aid") val aid: Long = 0, // 视频aid
    @SerialName("cid") val cid: Long = 0, // 视频cid
    @SerialName("duration") val duration: Int = 0, // 视频长度，单位为秒
    @SerialName("type") val type: String = ""
)

@Serializable
data class ThreePoint(
    @SerialName("dislike_reasons") val dislikeReasons: List<DislikeReason> = emptyList(),
    @SerialName("feedbacks") val feedbacks: List<Feedback> = emptyList(),
    @SerialName("watch_later") val watchLater: Int = 0
)

@Serializable
data class ThreePointV2(
    @SerialName("icon") val icon: String = "",
    @SerialName("reasons") val reasons: List<Reason> = emptyList(),
    @SerialName("subtitle") val subtitle: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("type") val type: String = ""
)

@Serializable
data class DislikeReason(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)

@Serializable
data class Feedback(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)

@Serializable
data class Reason(
    @SerialName("id") val id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("toast") val toast: String = ""
)