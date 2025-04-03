package top.sacz.biz.home.model

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
    @SerialName("can_play") val canPlay: Int = 0,
    @SerialName("card_goto") val cardGoto: String = "",
    @SerialName("card_type") val cardType: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("cover_left1_content_description") val coverLeft1ContentDescription: String = "",
    @SerialName("cover_left2_content_description") val coverLeft2ContentDescription: String = "",
    @SerialName("cover_left_icon1") val coverLeftIcon1: Int = 0,
    @SerialName("cover_left_icon2") val coverLeftIcon2: Int = 0,
    @SerialName("cover_left_text1") val coverLeftText1: String = "",
    @SerialName("cover_left_text2") val coverLeftText2: String = "",
    @SerialName("cover_right_content_description") val coverRightContentDescription: String = "",
    @SerialName("cover_right_text") val coverRightText: String = "",
    @SerialName("desc_button") val descButton: DescButton = DescButton(),
    @SerialName("goto") val goto: String = "",
    @SerialName("goto_icon") val gotoIcon: GotoIcon = GotoIcon(),
    @SerialName("idx") val idx: Int = 0,
    @SerialName("official_icon") val officialIcon: Int = 0,
    @SerialName("param") val `param`: String = "",
    @SerialName("player_args") val playerArgs: PlayerArgs = PlayerArgs(),
    @SerialName("report_flow_data") val reportFlowData: String = "",
    @SerialName("talk_back") val talkBack: String = "",
    @SerialName("three_point") val threePoint: ThreePoint = ThreePoint(),
    @SerialName("three_point_v2") val threePointV2: List<ThreePointV2> = emptyList(),
    @SerialName("title") val title: String = "",
    @SerialName("uri") val uri: String = ""
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
    @SerialName("text") val text: String = "",
    @SerialName("type") val type: Int = 0,
    @SerialName("uri") val uri: String = ""
)

@Serializable
data class GotoIcon(
    @SerialName("icon_height") val iconHeight: Int = 0,
    @SerialName("icon_night_url") val iconNightUrl: String = "",
    @SerialName("icon_url") val iconUrl: String = "",
    @SerialName("icon_width") val iconWidth: Int = 0
)

@Serializable
data class PlayerArgs(
    // 修改aid和cid为Long类型以支持大数值
    @SerialName("aid") val aid: Long = 0,
    @SerialName("cid") val cid: Long = 0,
    @SerialName("duration") val duration: Int = 0,
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