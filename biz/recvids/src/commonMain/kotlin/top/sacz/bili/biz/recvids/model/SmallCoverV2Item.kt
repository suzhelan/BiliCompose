package top.sacz.bili.biz.recvids.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

//拓展静态字段
inline val SmallCoverV2Item.Companion.targetCardType get() = "small_cover_v2"


@Immutable
@Serializable
data class SmallCoverV2Item(
    @SerialName("args") val args: Args = Args(),
    @SerialName("can_play") val canPlay: Int = 0, // 是否可播放，1表示可播放
    @SerialName("card_goto") override val cardGoto: String = "", // 卡片跳转类型，视频为"av"
    @SerialName("card_type") override val cardType: String = "", // 卡片类型，视频为"small_cover_v2"
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
    @SerialName("idx") override val idx: Int = 0,
    @SerialName("official_icon") val officialIcon: Int = 0,
    @SerialName("param") val `param`: String = "", // 视频aid
    @SerialName("player_args") val playerArgs: PlayerArgs = PlayerArgs(), // 视频信息
    @SerialName("report_flow_data") val reportFlowData: String = "",
    @SerialName("talk_back") val talkBack: String = "",
    @SerialName("three_point") val threePoint: ThreePoint = ThreePoint(),
    @SerialName("three_point_v2") val threePointV2: List<ThreePointV2> = emptyList(),
    @SerialName("title") val title: String = "", // 视频标题
    @SerialName("uri") val uri: String = "" // 跳转链接
) : BaseCoverItem()