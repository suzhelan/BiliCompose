package top.sacz.bili.biz.biliplayer.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 视频的相关视频列表,去除了一些无用的字段
 */
@Serializable
data class RecommendedVideoByVideo(
    @SerialName("config")
    val config: Config,
    @SerialName("items")
    val items: List<Item>
) {
    @Serializable
    data class Config(
        @SerialName("back_to_resume_progress")
        val backToResumeProgress: Int, // 10
        @SerialName("enable_back_to_resume")
        val enableBackToResume: Boolean, // true
        @SerialName("enable_jump_to_view")
        val enableJumpToView: Boolean, // true
        @SerialName("enable_rcmd_guide")
        val enableRcmdGuide: Boolean, // true
        @SerialName("enable_rerank")
        val enableRerank: Boolean, // true
        @SerialName("jump_to_view_icon")
        val jumpToViewIcon: String, // https://i0.hdslb.com/bfs/activity-plat/static/20221213/0977767b2e79d8ad0a36a731068a83d7/3wrrWm1sGX.png
        @SerialName("live_room_guide_delay")
        val liveRoomGuideDelay: Int, // 1000
        @SerialName("reply_no_danmu")
        val replyNoDanmu: Boolean, // true
        @SerialName("reply_zoom_exp")
        val replyZoomExp: Int, // 2
        @SerialName("show_button")
        val showButton: List<String>,
        @SerialName("show_live_full_button")
        val showLiveFullButton: Int, // 1
        @SerialName("slide_guidance_ab")
        val slideGuidanceAb: Int, // 1
        @SerialName("speed_play_exp")
        val speedPlayExp: Boolean, // true
        @SerialName("tab_rotation_time")
        val tabRotationTime: Int // 6
    )

    @Serializable
    data class Item(
        @SerialName("bvid")
        val bvid: String, // BV1patxzvEm3
        @SerialName("card_goto")
        val cardGoto: String, // vertical_av
        @SerialName("copyright")
        val copyright: Int, // 1
        @SerialName("cover")
        val cover: String, // http://i1.hdslb.com/bfs/archive/1d40e476bcc618428407122c5547d7dcc08bf152.jpg
        @SerialName("desc")
        val desc: String = "-", // -
        @SerialName("dimension")
        val dimension: Dimension,
        @SerialName("dl_subtitle")
        val dlSubtitle: String, // 第一次发视频，尽然是因为事故
        @SerialName("dl_title")
        val dlTitle: String, // 视频已缓存完成
        @SerialName("duration")
        val duration: Int, // 44
        @SerialName("ff_cover")
        val ffCover: String, // http://i2.hdslb.com/bfs/storyff/n250809sa2ya5fsr9zm63n2pc0fvgbar_firsti.jpg
        @SerialName("goto")
        val goto: String, // vertical_av
        @SerialName("idx")
        val idx: Int, // 1
        @SerialName("owner")
        val owner: Owner,
        @SerialName("param")
        val `param`: String, // 114996004654324
        @SerialName("part")
        val part: String, // studio_video_1754699535185.mp4
        @SerialName("player_args")
        val playerArgs: PlayerArgs,
        @SerialName("pubdate")
        val pubdate: Int, // 1754700085
        @SerialName("report_flow_data")
        val reportFlowData: String, // {"flow_card_type":"vertical_av"}
        @SerialName("rights")
        val rights: Rights,
        @SerialName("short_link")
        val shortLink: String, // https://b23.tv/BV1patxzvEm3
        @SerialName("show_report")
        val showReport: ShowReport,
        @SerialName("stat")
        val stat: Stat,
        @SerialName("sub_title")
        val subTitle: String, // 169.9万播放
        @SerialName("thumb_up_animation")
        val thumbUpAnimation: String, // story_like_combo_tv
        @SerialName("title")
        val title: String, // 第一次发视频，尽然是因为事故
        @SerialName("top_search_bar")
        val topSearchBar: TopSearchBar,
        @SerialName("uri")
        val uri: String, // bilibili://story/114996004654324?cid=31577146929&player_height=1920&player_preload=%7B%22cid%22%3A31577146929%2C%22expire_time%22%3A1756093981%2C%22file_info%22%3A%7B%2216%22%3A%5B%7B%22timelength%22%3A43833%2C%22filesize%22%3A600390%7D%5D%7D%2C%22support_quality%22%3Anull%2C%22support_formats%22%3Anull%2C%22support_description%22%3Anull%2C%22quality%22%3A16%2C%22url%22%3A%22http%3A%2F%2F182.89.195.11%3A4480%2Fupgcxcode%2F29%2F69%2F31577146929%2F31577146929-1-16.mp4%3Fe%3Dig8euxZM2rNcNbRVhwdVhwdlhWdVhwdVhoNvNC8BqJIzNbfqXBvEuENvNC8aNEVEtEvE9IMvXBvE2ENvNCImNEVEIj0Y2J_aug859r1qXg8gNEVE5XREto8z5JZC2X2gkX5L5F1eTX1jkXlsTXHeux_f2o859IB_%5Cu0026platform%3D%5Cu0026oi%3D2095235124%5Cu0026deadline%3D1756097581%5Cu0026os%3Dmcdn%5Cu0026og%3Dcos%5Cu0026trid%3D00002ee5f08cf1ec47768df9a28a392eb5cO%5Cu0026mid%3D0%5Cu0026uipk%3D5%5Cu0026gen%3Dplayurlv3%5Cu0026nbs%3D1%5Cu0026upsig%3Df80055d3b2b74baf6d4ed34ba237fcc9%5Cu0026uparams%3De%2Cplatform%2Coi%2Cdeadline%2Cos%2Cog%2Ctrid%2Cmid%2Cuipk%2Cgen%2Cnbs%5Cu0026mcdnid%3D50037561%5Cu0026bvc%3Dvod%5Cu0026nettype%3D0%5Cu0026bw%3D111700%5Cu0026lrs%3D46%5Cu0026dl%3D0%5Cu0026f%3DO_0_0%5Cu0026agrr%3D0%5Cu0026buvid%3D%5Cu0026build%3D0%5Cu0026orderid%3D0%2C3%22%2C%22video_codecid%22%3A7%2C%22video_project%22%3Atrue%2C%22fnver%22%3A0%2C%22fnval%22%3A0%7D&player_rotate=0&player_width=1080&report_flow_data=%7B%22flow_card_type%22%3A%22vertical_av%22%7D
        @SerialName("view_content")
        val viewContent: String, // 169.9万
        @SerialName("vip")
        val vip: Vip
    )


    @Serializable
    data class Dimension(
        @SerialName("height")
        val height: Int, // 1920
        @SerialName("rotate")
        val rotate: Int, // 0
        @SerialName("width")
        val width: Int // 1080
    )


    @Serializable
    data class Owner(
        @SerialName("avatar")
        val avatar: Avatar,
        @SerialName("face")
        val face: String, // https://i0.hdslb.com/bfs/face/706ef370ccb11a46716f593fd8daf005d3c967f6.jpg
        @SerialName("fans")
        val fans: Int, // 4298
        @SerialName("like_num")
        val likeNum: Int, // 508147
        @SerialName("mid")
        val mid: Long, // 3461582560430399
        @SerialName("name")
        val name: String, // 大鹏無
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,
        @SerialName("relation")
        val relation: Relation,
        @SerialName("sub_avatar")
        val subAvatar: SubAvatar
    )

    @Serializable
    data class PlayerArgs(
        @SerialName("aid")
        val aid: Long, // 114996004654324
        @SerialName("cid")
        val cid: Long, // 31577146929
        @SerialName("type")
        val type: String // av
    )


    @Serializable
    data class Rights(
        @SerialName("no_background")
        val noBackground: Int, // 0
        @SerialName("no_reprint")
        val noReprint: Int // 0
    )


    @Serializable
    data class ShowReport(
        @SerialName("pugv_str")
        val pugvStr: String,
        @SerialName("request_from")
        val requestFrom: String // 0
    )

    @Serializable
    data class Stat(
        @SerialName("aid")
        val aid: Long, // 114996004654324
        @SerialName("coin")
        val coin: Int, // 359601
        @SerialName("danmaku")
        val danmaku: Int, // 29576
        @SerialName("favorite")
        val favorite: Int, // 22994
        @SerialName("follow")
        val follow: Int, // 0
        @SerialName("like")
        val like: Int, // 502246
        @SerialName("reply")
        val reply: Int, // 2796
        @SerialName("share")
        val share: Int, // 697
        @SerialName("view")
        val view: Int // 1699728
    )


    @Serializable
    data class TopSearchBar(
        @SerialName("uri")
        val uri: String // bilibili://search?direct_return=false&from_avid=114996004654324&from_trackid=&event_id=main.ugc-video-detail-vertical.0.0.pv
    )


    @Serializable
    data class Vip(
        @SerialName("avatar_subscript")
        val avatarSubscript: Int, // 0
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String,
        @SerialName("due_date")
        val dueDate: Long, // 1768492800000
        @SerialName("label")
        val label: Label,
        @SerialName("nickname_color")
        val nicknameColor: String,
        @SerialName("ott_info")
        val ottInfo: OttInfo,
        @SerialName("role")
        val role: Int, // 0
        @SerialName("status")
        val status: Int, // 0
        @SerialName("super_vip")
        val superVip: SuperVip,
        @SerialName("theme_type")
        val themeType: Int, // 0
        @SerialName("tv_due_date")
        val tvDueDate: Int, // 0
        @SerialName("tv_vip_pay_type")
        val tvVipPayType: Int, // 0
        @SerialName("tv_vip_status")
        val tvVipStatus: Int, // 0
        @SerialName("type")
        val type: Int, // 0
        @SerialName("vip_pay_type")
        val vipPayType: Int // 0
    )


    @Serializable
    data class Avatar(
        @SerialName("container_size")
        val containerSize: ContainerSize,
        @SerialName("fallback_layers")
        val fallbackLayers: FallbackLayers,
        @SerialName("mid")
        val mid: String // 1992648798
    )

    @Serializable
    data class OfficialVerify(
        @SerialName("type")
        val type: Int // -1
    )

    @Serializable
    data class Relation(
        @SerialName("status")
        val status: Int // 1
    )

    @Serializable
    data class SubAvatar(
        @SerialName("container_size")
        val containerSize: ContainerSize,
        @SerialName("mid")
        val mid: String // 1992648798
    )

    @Serializable
    data class ContainerSize(
        @SerialName("height")
        val height: Int, // 1
        @SerialName("width")
        val width: Int // 1
    )

    @Serializable
    data class FallbackLayers(
        @SerialName("is_critical_group")
        val isCriticalGroup: Boolean, // true
        @SerialName("layers")
        val layers: List<Layer>
    )


    @Serializable
    data class Layer(
        @SerialName("general_spec")
        val generalSpec: GeneralSpec,
        @SerialName("visible")
        val visible: Boolean // true
    )

    @Serializable
    data class GeneralSpec(
        @SerialName("pos_spec")
        val posSpec: PosSpec,
        @SerialName("render_spec")
        val renderSpec: RenderSpec,
        @SerialName("size_spec")
        val sizeSpec: SizeSpec
    )


    @Serializable
    data class PosSpec(
        @SerialName("axis_x")
        val axisX: Double, // 0.5
        @SerialName("axis_y")
        val axisY: Double, // 0.5
        @SerialName("coordinate_pos")
        val coordinatePos: Int // 2
    )

    @Serializable
    data class RenderSpec(
        @SerialName("opacity")
        val opacity: Int // 1
    )

    @Serializable
    data class SizeSpec(
        @SerialName("height")
        val height: Double, // 1.05
        @SerialName("width")
        val width: Double // 1.05
    )


    @Serializable
    data class Label(
        @SerialName("bg_color")
        val bgColor: String,
        @SerialName("bg_style")
        val bgStyle: Int, // 0
        @SerialName("border_color")
        val borderColor: String,
        @SerialName("img_label_uri_hans")
        val imgLabelUriHans: String,
        @SerialName("img_label_uri_hans_static")
        val imgLabelUriHansStatic: String, // https://i0.hdslb.com/bfs/vip/d7b702ef65a976b20ed854cbd04cb9e27341bb79.png
        @SerialName("img_label_uri_hant")
        val imgLabelUriHant: String,
        @SerialName("img_label_uri_hant_static")
        val imgLabelUriHantStatic: String, // https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/KJunwh19T5.png
        @SerialName("label_goto")
        val labelGoto: LabelGoto?,
        @SerialName("label_id")
        val labelId: Int, // 0
        @SerialName("label_theme")
        val labelTheme: String,
        @SerialName("path")
        val path: String,
        @SerialName("text")
        val text: String,
        @SerialName("text_color")
        val textColor: String,
        @SerialName("use_img_label")
        val useImgLabel: Boolean // true
    )

    @Serializable
    data class OttInfo(
        @SerialName("overdue_time")
        val overdueTime: Int, // 0
        @SerialName("pay_channel_id")
        val payChannelId: String,
        @SerialName("pay_type")
        val payType: Int, // 0
        @SerialName("status")
        val status: Int, // 0
        @SerialName("vip_type")
        val vipType: Int // 0
    )

    @Serializable
    data class SuperVip(
        @SerialName("is_super_vip")
        val isSuperVip: Boolean // false
    )


    @Serializable
    data class LabelGoto(
        @SerialName("mobile")
        val mobile: String, // https://big.bilibili.com/mobile/index?navhide=1&from_spmid=vipicon
        @SerialName("pc_web")
        val pcWeb: String // https://account.bilibili.com/big?from_spmid=vipicon
    )

}


