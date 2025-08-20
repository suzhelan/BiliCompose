package top.sacz.bili.biz.player.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 视频基础信息数据类
 * 对应接口：https://api.bilibili.com/x/web-interface/view
 */
@Serializable
data class VideoInfo(
    /** 稿件avid（唯一标识） */
    @SerialName("aid")
    val aid: Long = 0,

    /** 争议信息对象（如争议提示） */
    @SerialName("argue_info")
    val argueInfo: ArgueInfo = ArgueInfo(),

    /** 稿件bvid（BV号标识） */
    @SerialName("bvid")
    val bvid: String = "",

    /** 当前分P cid（分页标识） */
    @SerialName("cid")
    val cid: Long = 0,

    /** 版权类型（1：原创，2：转载） */
    @SerialName("copyright")
    val copyright: Int = 0,

    /** 创建时间（秒级时间戳） */
    @SerialName("ctime")
    val ctime: Int = 0,

    /** 视频简介文本内容 */
    @SerialName("desc")
    val desc: String = "",

    /** 视频简介富文本内容 */
    @SerialName("desc_v2")
    val descV2: List<DescV2>? = listOf(),

    /** 视频分辨率信息 */
    @SerialName("dimension")
    val dimension: Dimension = Dimension(),

    /** 是否禁用显示投稿信息 */
    @SerialName("disable_show_up_info")
    val disableShowUpInfo: Boolean = false,

    /** 视频时长（秒） */
    @SerialName("duration")
    val duration: Int = 0,

    /** 动态内容（如话题标签） */
    @SerialName("dynamic")
    val `dynamic`: String = "",

    /** 是否启用VT（虚拟主播） */
    @SerialName("enable_vt")
    val enableVt: Int = 0,

    /** 荣誉回复信息 */
    @SerialName("honor_reply")
    val honorReply: HonorReply = HonorReply(),

    /** 是否为付费季 */
    @SerialName("is_chargeable_season")
    val isChargeableSeason: Boolean = false,

    /** 是否显示为季 */
    @SerialName("is_season_display")
    val isSeasonDisplay: Boolean = false,

    /** 是否为故事模式 */
    @SerialName("is_story")
    val isStory: Boolean = false,

    /** 是否为故事模式播放 */
    @SerialName("is_story_play")
    val isStoryPlay: Int = 0,

    /** 是否为UP主专属 */
    @SerialName("is_upower_exclusive")
    val isUpowerExclusive: Boolean = false,

    /** 是否为UP主专属带问答 */
    @SerialName("is_upower_exclusive_with_qa")
    val isUpowerExclusiveWithQa: Boolean = false,

    /** 是否为UP主专属播放 */
    @SerialName("is_upower_play")
    val isUpowerPlay: Boolean = false,

    /** 是否为UP主专属预览 */
    @SerialName("is_upower_preview")
    val isUpowerPreview: Boolean = false,

    /** 是否仅自己可见 */
    @SerialName("is_view_self")
    val isViewSelf: Boolean = false,

    /** 点赞图标 */
    @SerialName("like_icon")
    val likeIcon: String = "",

    /** 任务ID */
    @SerialName("mission_id")
    val missionId: Int = 0,

    /** 是否需要跳转BV号 */
    @SerialName("need_jump_bv")
    val needJumpBv: Boolean = false,

    /** 是否禁用缓存 */
    @SerialName("no_cache")
    val noCache: Boolean = false,

    /** 投稿者信息 */
    @SerialName("owner")
    val owner: Owner = Owner(),

    /** 分P列表 */
    @SerialName("pages")
    val pages: List<Page> = listOf(),

    /** 视频封面图片URL */
    @SerialName("pic")
    val pic: String = "",

    /** 发布时间（秒级时间戳） */
    @SerialName("pubdate")
    val pubdate: Int = 0,

    /** 权限控制标志位对象 */
    @SerialName("rights")
    val rights: Rights = Rights(),

    /** 联合投稿成员列表 */
    @SerialName("staff")
    val staff: List<Staff> = listOf(),

    /** 视频统计信息（播放量、弹幕量等） */
    @SerialName("stat")
    val stat: Stat = Stat(),

    /** 状态码 */
    @SerialName("state")
    val state: Int = 0,

    /** 字幕信息 */
    @SerialName("subtitle")
    val subtitle: Subtitle = Subtitle(),

    /** 青少年模式 */
    @SerialName("teenage_mode")
    val teenageMode: Int = 0,

    /** 分区ID */
    @SerialName("tid")
    val tid: Int = 0,

    /** 分区ID（新版） */
    @SerialName("tid_v2")
    val tidV2: Int = 0,

    /** 视频标题 */
    @SerialName("title")
    val title: String = "",

    /** 分区名称 */
    @SerialName("tname")
    val tname: String = "",

    /** 分区名称（新版） */
    @SerialName("tname_v2")
    val tnameV2: String = "",

    /** 用户装扮信息 */
    @SerialName("user_garb")
    val userGarb: UserGarb = UserGarb(),

    /** 分P数量 */
    @SerialName("videos")
    val videos: Int = 0,

    /** VT显示信息 */
    @SerialName("vt_display")
    val vtDisplay: String = ""
) {
    @Serializable
    data class ArgueInfo(
        @SerialName("argue_link")
        val argueLink: String = "", // 示例值：""
        @SerialName("argue_msg")
        val argueMsg: String = "", // 示例值：""
        @SerialName("argue_type")
        val argueType: Int = 0 // 示例值：0
    )

    @Serializable
    data class DescV2(
        @SerialName("biz_id")
        val bizId: Int = 0, // 示例值：0
        @SerialName("raw_text")
        val rawText: String = "", // 示例值："【CB想说的】看完拜年祭之后最爱的一个节目！..."
        @SerialName("type")
        val type: Int = 0 // 示例值：1
    )

    @Serializable
    data class Dimension(
        @SerialName("height")
        val height: Int = 0, // 示例值：1080
        @SerialName("rotate")
        val rotate: Int = 0, // 示例值：0
        @SerialName("width")
        val width: Int = 0 // 示例值：1920
    )

    @Serializable
    data class HonorReply(
        @SerialName("honor")
        val honor: List<Honor> = listOf()
    )

    @Serializable
    data class Owner(
        @SerialName("face")
        val face: String = "", // 示例值："https://i2.hdslb.com/bfs/face/c9af3b32cf74baec5a4b65af8ca18ae5ff571f77.jpg"
        @SerialName("mid")
        val mid: Long = 0, // 示例值：66606350
        @SerialName("name")
        val name: String = "" // 示例值："陈楒潼桶桶桶"
    )

    @Serializable
    data class Page(
        @SerialName("cid")
        val cid: Long = 0, // 示例值：146044693
        @SerialName("ctime")
        val ctime: Int = 0, // 示例值：1580212263
        @SerialName("dimension")
        val dimension: Dimension = Dimension(),
        @SerialName("duration")
        val duration: Int = 0, // 示例值：486
        @SerialName("from")
        val from: String = "", // 示例值："vupload"
        @SerialName("page")
        val page: Int = 0, // 示例值：1
        @SerialName("part")
        val part: String = "", // 示例值："建议改成：建议改成：诸 神 的 电 音 节（不是）"
        @SerialName("vid")
        val vid: String = "", // 示例值：""
        @SerialName("weblink")
        val weblink: String = "" // 示例值：""
    )

    /**
     * 权限标志位数据类
     * 包含视频的各种权限状态标志
     */
    @Serializable
    data class Rights(
        /** 是否允许自动播放（0：不允许，1：允许） */
        @SerialName("autoplay")
        val autoplay: Int = 0,

        /** 是否允许下载（0：不允许，1：允许） */
        @SerialName("download")
        val download: Int = 0,

        /** 是否支持高码率（0：不支持，1：支持） */
        @SerialName("hd5")
        val hd5: Int = 0,

        /** 是否显示禁止转载标志（0：不显示，1：显示） */
        @SerialName("no_reprint")
        val noReprint: Int = 0,

        /** 是否禁止分享（0：允许，1：禁止） */
        @SerialName("no_share")
        val noShare: Int = 0,

        /** 是否为付费内容（0：免费，1：付费） */
        @SerialName("pay")
        val pay: Int = 0,

        /** 是否为UP主付费内容（0：免费，1：付费） */
        @SerialName("ugc_pay")
        val ugcPay: Int = 0,

        /** 是否为UP主付费预览内容（0：免费，1：付费） */
        @SerialName("ugc_pay_preview")
        val ugcPayPreview: Int = 0 // 示例值：0
    )

    @Serializable
    data class Staff(
        @SerialName("face")
        val face: String = "", // 示例值："https://i2.hdslb.com/bfs/face/c9af3b32cf74baec5a4b65af8ca18ae5ff571f77.jpg"
        @SerialName("follower")
        val follower: Int = 0, // 示例值：616428
        @SerialName("label_style")
        val labelStyle: Int = 0, // 示例值：0
        @SerialName("mid")
        val mid: Long = 0, // 示例值：66606350
        @SerialName("name")
        val name: String = "", // 示例值："陈楒潼桶桶桶"
        @SerialName("official")
        val official: Official = Official(),
        @SerialName("title")
        val title: String = "", // 示例值："UP主"
        @SerialName("vip")
        val vip: Vip = Vip()
    )

    @Serializable
    data class Stat(
        @SerialName("aid")
        val aid: Long = 0, // aid：85440373
        @SerialName("coin")
        val coin: Int = 0, // 投币：72793
        @SerialName("danmaku")
        val danmaku: Int = 0, // 弹幕：12348
        @SerialName("dislike")
        val dislike: Int = 0, // 点踩数 恒为：0
        @SerialName("evaluation")
        val evaluation: String = "", // 视频评分
        @SerialName("favorite")
        val favorite: Int = 0, // 收藏数：58329
        @SerialName("his_rank")
        val hisRank: Int = 0, // 历史最高排名：55
        @SerialName("like")
        val like: Int = 0, // 点赞：161270
        @SerialName("now_rank")
        val nowRank: Int = 0, // 现在排名
        @SerialName("reply")
        val reply: Int = 0, // 评论数：2676
        @SerialName("share")
        val share: Int = 0, // 分享数：9620
        @SerialName("view")
        val view: Int = 0, // 播放数：2404179
        @SerialName("vt")
        val vt: Int = 0 // 恒为：0
    )

    @Serializable
    data class Subtitle(
        @SerialName("allow_submit")
        val allowSubmit: Boolean = false, // 示例值：false
        @SerialName("list")
        val list: List<Item0> = listOf()
    )

    @Serializable
    data class UserGarb(
        @SerialName("url_image_ani_cut")
        val urlImageAniCut: String = "" // 示例值："https://i0.hdslb.com/bfs/garb/item/e4c1c34e8b87fc05a893ed4a04ad322f75edbed9.bin"
    )

    @Serializable
    data class Honor(
        @SerialName("aid")
        val aid: Long = 0, // 示例值：85440373
        @SerialName("desc")
        val desc: String = "", // 示例值："第45期每周必看"
        @SerialName("type")
        val type: Int = 0, // 示例值：2
        @SerialName("weekly_recommend_num")
        val weeklyRecommendNum: Int = 0 // 示例值：45
    )

    @Serializable
    data class Official(
        @SerialName("desc")
        val desc: String = "", // 示例值：""
        @SerialName("role")
        val role: Int = 0, // 示例值：1
        @SerialName("title")
        val title: String = "", // 示例值："bilibili 知名音乐UP主"
        @SerialName("type")
        val type: Int = 0 // 示例值：0
    )

    @Serializable
    data class Vip(
        @SerialName("avatar_icon")
        val avatarIcon: AvatarIcon = AvatarIcon(),
        @SerialName("avatar_subscript")
        val avatarSubscript: Int = 0, // 示例值：1
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String = "", // 示例值：""
        @SerialName("due_date")
        val dueDate: Long = 0, // 示例值：1769443200000
        @SerialName("label")
        val label: Label = Label(),
        @SerialName("nickname_color")
        val nicknameColor: String = "", // 示例值："#FB7299"
        @SerialName("role")
        val role: Int = 0, // 示例值：3
        @SerialName("status")
        val status: Int = 0, // 示例值：1
        @SerialName("theme_type")
        val themeType: Int = 0, // 示例值：0
        @SerialName("tv_due_date")
        val tvDueDate: Int = 0, // 示例值：0
        @SerialName("tv_vip_pay_type")
        val tvVipPayType: Int = 0, // 示例值：0
        @SerialName("tv_vip_status")
        val tvVipStatus: Int = 0, // 示例值：0
        @SerialName("type")
        val type: Int = 0, // 示例值：2
        @SerialName("vip_pay_type")
        val vipPayType: Int = 0 // 示例值：1
    )

    @Serializable
    data class AvatarIcon(
        @SerialName("icon_resource")
        val iconResource: IconResource = IconResource(),
        @SerialName("icon_type")
        val iconType: Int = 0 // 示例值：1
    )

    @Serializable
    data class Label(
        @SerialName("bg_color")
        val bgColor: String = "", // 示例值："#FB7299"
        @SerialName("bg_style")
        val bgStyle: Int = 0, // 示例值：1
        @SerialName("border_color")
        val borderColor: String = "", // 示例值：""
        @SerialName("img_label_uri_hans")
        val imgLabelUriHans: String = "", // 示例值："https://i0.hdslb.com/bfs/activity-plat/static/20220608/e369244d0b14644f5e1a06431e22a4d5/0DFy9BHgwE.gif"
        @SerialName("img_label_uri_hans_static")
        val imgLabelUriHansStatic: String = "", // 示例值："https://i0.hdslb.com/bfs/vip/8d7e624d13d3e134251e4174a7318c19a8edbd71.png"
        @SerialName("img_label_uri_hant")
        val imgLabelUriHant: String = "", // 示例值：""
        @SerialName("img_label_uri_hant_static")
        val imgLabelUriHantStatic: String = "", // 示例值："https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/uckjAv3Npy.png"
        @SerialName("label_theme")
        val labelTheme: String = "", // 示例值："annual_vip"
        @SerialName("path")
        val path: String = "", // 示例值：""
        @SerialName("text")
        val text: String = "", // 示例值："年度大会员"
        @SerialName("text_color")
        val textColor: String = "", // 示例值："#FFFFFF"
        @SerialName("use_img_label")
        val useImgLabel: Boolean = false // 示例值：true
    )

    @Serializable
    class IconResource

    @Serializable
    data class Item0(
        @SerialName("ai_status")
        val aiStatus: Int = 0, // 示例值：2
        @SerialName("ai_type")
        val aiType: Int = 0, // 示例值：0
        @SerialName("author")
        val author: Author = Author(),
        @SerialName("id")
        val id: Long = 0, // 示例值：1061981378473779968
        @SerialName("id_str")
        val idStr: String = "", // 示例值："1061981378473779968"
        @SerialName("is_lock")
        val isLock: Boolean = false, // 示例值：false
        @SerialName("lan")
        val lan: String = "", // 示例值："ai-zh"
        @SerialName("lan_doc")
        val lanDoc: String = "", // 示例值："中文（自动生成）"
        @SerialName("subtitle_url")
        val subtitleUrl: String = "", // 示例值：""
        @SerialName("type")
        val type: Int = 0 // 示例值：1
    )

    @Serializable
    data class Author(
        @SerialName("birthday")
        val birthday: Int = 0, // 示例值：0
        @SerialName("face")
        val face: String = "", // 示例值：""
        @SerialName("in_reg_audit")
        val inRegAudit: Int = 0, // 示例值：0
        @SerialName("is_deleted")
        val isDeleted: Int = 0, // 示例值：0
        @SerialName("is_fake_account")
        val isFakeAccount: Int = 0, // 示例值：0
        @SerialName("is_senior_member")
        val isSeniorMember: Int = 0, // 示例值：0
        @SerialName("mid")
        val mid: Long = 0, // 示例值：0
        @SerialName("name")
        val name: String = "", // 示例值：""
        @SerialName("rank")
        val rank: Int = 0, // 示例值：0
        @SerialName("sex")
        val sex: String = "", // 示例值：""
        @SerialName("sign")
        val sign: String = "" // 示例值：""
    )
}