package top.sacz.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserCard(
    // 用户稿件数
    // 示例：37
    @SerialName("archive_count")
    val archiveCount: Int = 0,

    // 文章数量（当前接口返回为0）
    // 示例：0
    @SerialName("article_count")
    val articleCount: Int = 0,

    // 用户卡片基础信息
    // 示例见Card类注释
    @SerialName("card")
    val card: Card = Card(),

    // 粉丝数
    // 示例：969999
    @SerialName("follower")
    val follower: Int = 0,

    // 是否关注此用户
    // 示例：true
    @SerialName("following")
    val following: Boolean = false,

    // 点赞数
    // 示例：3547978
    @SerialName("like_num")
    val likeNum: Int = 0
) {
    @Serializable
    data class Card(
        // 是否认证（已废弃）
        // 示例：false
        @SerialName("approve")
        val approve: Boolean = false,

        // 文章数量（已废弃）
        // 示例：0
        @SerialName("article")
        val article: Int = 0,

        // 关注数
        // 示例：234
        @SerialName("attention")
        val attention: Int = 0,

        // 生日信息（MM-DD格式）
        // 示例："09-19"
        @SerialName("birthday")
        val birthday: String = "",

        // 描述信息（暂未使用）
        // 示例：""
        @SerialName("description")
        val description: String = "",

        // 显示等级（暂未使用）
        // 示例："0"
        @SerialName("DisplayRank")
        val displayRank: String = "",

        // 头像图片URL
        // 示例："https://i2.hdslb.com/bfs/face/ef0457addb24141e15dfac6fbf45293ccf1e32ab.jpg"
        @SerialName("face")
        val face: String = "",

        // 是否为NFT头像
        // 示例：0
        @SerialName("face_nft")
        val faceNft: Int = 0,

        // NFT头像类型
        // 示例：0
        @SerialName("face_nft_type")
        val faceNftType: Int = 0,

        // 粉丝总数
        // 示例：969999
        @SerialName("fans")
        val fans: Int = 0,

        // 好友数量
        // 示例：234
        @SerialName("friend")
        val friend: Int = 0,

        // 是否为硬核会员
        // 示例：0
        @SerialName("is_senior_member")
        val isSeniorMember: Int = 0,

        // 等级信息
        // 示例见LevelInfo类注释
        @SerialName("level_info")
        val levelInfo: LevelInfo = LevelInfo(),

        // 用户ID
        // 示例："2"
        @SerialName("mid")
        val mid: String = "",

        // 用户昵称
        // 示例："碧诗"
        @SerialName("name")
        val name: String = "",

        // 勋章信息
        // 示例见Nameplate类注释
        @SerialName("nameplate")
        val nameplate: Nameplate = Nameplate(),

        // 认证信息
        // 示例见Official类注释
        @SerialName("Official")
        val official: Official = Official(),

        // 第二认证信息
        // 示例见OfficialVerify类注释
        @SerialName("official_verify")
        val officialVerify: OfficialVerify = OfficialVerify(),

        // 头像框信息
        // 示例见Pendant类注释
        @SerialName("pendant")
        val pendant: Pendant = Pendant(),

        // 所在地
        // 示例：""
        @SerialName("place")
        val place: String = "",

        // 用户权限等级
        // 示例："20000"
        @SerialName("rank")
        val rank: String = "",

        // 注册时间（暂未使用）
        // 示例：0
        @SerialName("regtime")
        val regtime: Int = 0,

        // 性别
        // 示例："男"
        @SerialName("sex")
        val sex: String = "",

        // 用户签名
        // 示例："kami.im 直男过气网红 # av362830 \"We Are Star Dust\""
        @SerialName("sign")
        val sign: String = "",

        // 空间状态
        // 示例：0
        @SerialName("spacesta")
        val spacesta: Int = 0,

        // 会员信息
        // 示例见Vip类注释
        @SerialName("vip")
        val vip: Vip = Vip()
    )

    @Serializable
    data class LevelInfo(
        // 当前经验值
        // 示例：27125
        @SerialName("current_exp")
        val currentExp: Int = 0,

        // 当前等级
        // 示例：6
        @SerialName("current_level")
        val currentLevel: Int = 0,

        // 当前等级起始经验值
        // 示例：10800
        @SerialName("current_min")
        val currentMin: Int = 0,

        // 下一等级所需经验值
        // 示例：28800
        @SerialName("next_exp")
        val nextExp: Int = 0
    )

    @Serializable
    data class Nameplate(
        // 获取条件
        // 示例："所有自制视频总播放数>=10万"
        @SerialName("condition")
        val condition: String = "",

        // 勋章大图URL
        // 示例："http://i2.hdslb.com/bfs/face/e93dd9edfa7b9e18bf46fd8d71862327a2350923.png"
        @SerialName("image")
        val image: String = "",

        // 勋章小图URL
        // 示例："http://i2.hdslb.com/bfs/face/275b468b043ec246737ab8580a2075bee0b1263b.png"
        @SerialName("image_small")
        val imageSmall: String = "",

        // 勋章等级
        // 示例："普通勋章"
        @SerialName("level")
        val level: String = "",

        // 勋章名称
        // 示例："见习偶像"
        @SerialName("name")
        val name: String = "",

        // 勋章ID
        // 示例：10
        @SerialName("nid")
        val nid: Int = 0
    )

    @Serializable
    data class Official(
        // 认证描述
        // 示例：""
        @SerialName("desc")
        val desc: String = "",

        // 认证类型
        // 示例：2
        @SerialName("role")
        val role: Int = 0,

        // 认证标题
        // 示例："bilibili创始人（站长）"
        @SerialName("title")
        val title: String = "",

        // 认证状态
        // 示例：0
        @SerialName("type")
        val type: Int = 0
    )

    @Serializable
    data class OfficialVerify(
        // 第二认证描述
        // 示例："bilibili创始人（站长）"
        @SerialName("desc")
        val desc: String = "",

        // 第二认证状态
        // 示例：0
        @SerialName("type")
        val type: Int = 0
    )

    @Serializable
    data class Pendant(
        // 过期时间（暂未使用）
        // 示例：0
        @SerialName("expire")
        val expire: Int = 0,

        // 头像框图片URL
        // 示例："http://i0.hdslb.com/bfs/garb/item/4f8f3f1f2d47f0dad84f66aa57acd4409ea46361.png"
        @SerialName("image")
        val image: String = "",

        // 增强版头像框图片URL
        // 示例："http://i0.hdslb.com/bfs/garb/item/fe0b83b53e2342b16646f6e7a9370d8a867decdb.webp"
        @SerialName("image_enhance")
        val imageEnhance: String = "",

        // 头像框逐帧动画URL
        // 示例："http://i0.hdslb.com/bfs/garb/item/127c507ec8448be30cf5f79500ecc6ef2fd32f2c.png"
        @SerialName("image_enhance_frame")
        val imageEnhanceFrame: String = "",

        // 新版头像框ID
        // 示例：2511
        @SerialName("n_pid")
        val nPid: Long = 0,

        // 头像框名称
        // 示例："初音未来13周年"
        @SerialName("name")
        val name: String = "",

        // 头像框ID
        // 示例：2511
        @SerialName("pid")
        val pid: Long = 0
    )

    @Serializable
    data class Vip(
        // 是否显示会员图标
        // 示例：1
        @SerialName("avatar_subscript")
        val avatarSubscript: Int = 0,

        // 会员角标URL
        // 示例：""
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String = "",

        // 会员过期时间（毫秒时间戳）
        // 示例：3896524800000
        @SerialName("due_date")
        val dueDate: Long = 0,

        // 会员标签信息
        // 示例见Label类注释
        @SerialName("label")
        val label: Label = Label(),

        // 会员昵称颜色
        // 示例："#FB7299"
        @SerialName("nickname_color")
        val nicknameColor: String = "",

        // 大角色类型
        // 示例：7
        @SerialName("role")
        val role: Int = 0,

        // 会员状态
        // 示例：1
        @SerialName("status")
        val status: Int = 0,

        // 主题类型
        // 示例：0
        @SerialName("theme_type")
        val themeType: Int = 0,

        // 电视大会员过期时间（秒级时间戳）
        // 示例：2003500800
        @SerialName("tv_due_date")
        val tvDueDate: Long = 0,

        // 电视大会员支付类型
        // 示例：1
        @SerialName("tv_vip_pay_type")
        val tvVipPayType: Int = 0,

        // 电视大会员状态
        // 示例：1
        @SerialName("tv_vip_status")
        val tvVipStatus: Int = 0,

        // 会员类型
        // 示例：2
        @SerialName("type")
        val type: Int = 0,

        // 支付类型
        // 示例：0
        @SerialName("vip_pay_type")
        val vipPayType: Int = 0,

        // 会员状态（旧版）
        // 示例：1
        @SerialName("vipStatus")
        val vipStatus: Int = 0,

        // 会员类型（旧版）
        // 示例：2
        @SerialName("vipType")
        val vipType: Int = 0
    )


    @Serializable
    data class Label(
        // 背景颜色
        // 示例："#FB7299"
        @SerialName("bg_color")
        val bgColor: String = "",

        // 背景样式
        // 示例：1
        @SerialName("bg_style")
        val bgStyle: Int = 0,

        // 边框颜色
        // 示例：""
        @SerialName("border_color")
        val borderColor: String = "",

        // 简体动态图标URI
        // 示例："https://i0.hdslb.com/bfs/activity-plat/static/20220608/e369244d0b14644f5e1a06431e22a4d5/wltavwHAkL.gif"
        @SerialName("img_label_uri_hans")
        val imgLabelUriHans: String = "",

        // 简体静态图标URI
        // 示例："https://i0.hdslb.com/bfs/vip/802418ff03911645648b63aa193ba67997b5a0bc.png"
        @SerialName("img_label_uri_hans_static")
        val imgLabelUriHansStatic: String = "",

        // 繁体动态图标URI
        // 示例：""
        @SerialName("img_label_uri_hant")
        val imgLabelUriHant: String = "",

        // 繁体静态图标URI
        // 示例："https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/8u7iRTPE7N.png"
        @SerialName("img_label_uri_hant_static")
        val imgLabelUriHantStatic: String = "",

        // 标签ID
        // 示例：0
        @SerialName("label_id")
        val labelId: Int = 0,

        // 标签主题
        // 示例："ten_annual_vip"
        @SerialName("label_theme")
        val labelTheme: String = "",

        // 路径（暂未使用）
        // 示例：""
        @SerialName("path")
        val path: String = "",

        // 标签文本
        // 示例："十年大会员"
        @SerialName("text")
        val text: String = "",

        // 文字颜色
        // 示例："#FFFFFF"
        @SerialName("text_color")
        val textColor: String = "",

        // 是否使用图片标签
        // 示例：true
        @SerialName("use_img_label")
        val useImgLabel: Boolean = false
    )
}