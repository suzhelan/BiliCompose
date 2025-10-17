package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 用户空间信息实体类
 *
 * 对应API: https://api.bilibili.com/x/space/wbi/acc/info
 */
@Serializable
data class UserSpaceInfo(
    /** 用户mid */
    @SerialName("mid") val mid: Long = 0, // 示例: 2

    /** 昵称 */
    @SerialName("name") val name: String = "", // 示例: "碧诗"

    /** 性别 男/女/保密 */
    @SerialName("sex") val sex: String = "", // 示例: "男"

    /** 头像链接 */
    @SerialName("face") val face: String = "", // 示例: "https://i2.hdslb.com/bfs/face/ef0457addb24141e15dfac6fbf45293ccf1e32ab.jpg"

    /** 是否为 NFT 头像 0：不是 NFT 头像, 1：是 NFT 头像 */
    @SerialName("face_nft") val faceNft: Int = 0, // 示例: 0

    /** NFT 头像类型？ */
    @SerialName("face_nft_type") val faceNftType: Int = 0, // 示例: 0

    /** 签名 */
    @SerialName("sign") val sign: String = "", // 示例: "https://kami.im 直男过气网红 #  We Are Star Dust"

    /** 用户权限等级 */
    @SerialName("rank") val rank: Int = 0, // 示例: 20000

    /** 当前等级 0-6 级 */
    @SerialName("level") val level: Int = 0, // 示例: 6

    /** 注册时间 此接口返回恒为0 */
    @SerialName("jointime") val jointime: Int = 0, // 示例: 0

    /** 节操值 此接口返回恒为0 */
    @SerialName("moral") val moral: Int = 0, // 示例: 0

    /** 封禁状态 0：正常, 1：被封 */
    @SerialName("silence") val silence: Int = 0, // 示例: 0

    /** 硬币数 需要登录（Cookie）只能查看自己的，默认为0 */
    @SerialName("coins") val coins: Double = 0.0, // 示例: 0.0

    /** 是否具有粉丝勋章 false：无, true：有 */
    @SerialName("fans_badge") val fansBadge: Boolean = false, // 示例: true

    /** 粉丝勋章信息 */
    @SerialName("fans_medal") val fansMedal: FansMedal? = null,

    /** 认证信息 */
    @SerialName("official") val official: Official,

    /** 会员信息 */
    @SerialName("vip") val vip: Vip,

    /** 头像框信息 */
    @SerialName("pendant") val pendant: Pendant,

    /** 勋章信息 */
    @SerialName("nameplate") val nameplate: Nameplate,

    /** （？） */
    @SerialName("user_honour_info") val userHonourInfo: UserHonourInfo,

    /** 是否关注此用户 true：已关注, false：未关注 */
    @SerialName("is_followed") val isFollowed: Boolean = false, // 示例: false

    /** 主页头图链接 */
    @SerialName("top_photo") val topPhoto: String = "", // 示例: "http://i0.hdslb.com/bfs/space/cb1c3ef50e22b6096fde67febe863494caefebad.png"


    /** 系统通知 无内容则为空对象 */
    @SerialName("sys_notice") val sysNotice: SysNotice? = null,

    /** 直播间信息 */
    @SerialName("live_room") val liveRoom: LiveRoom? = null,

    /** 生日 MM-DD 如设置隐私为空 */
    @SerialName("birthday") val birthday: String = "", // 示例: "09-19"

    /** 学校 */
    @SerialName("school") val school: School,

    /** 专业资质信息 */
    @SerialName("profession") val profession: Profession,

    /** 个人标签 有效时：array, 无效时：null */
    @SerialName("tags") val tags: List<String>? = null,

    /** （？） */
    @SerialName("series") val series: Series? = null,

    /** 是否为硬核会员 0：否, 1：是 */
    @SerialName("is_senior_member") val isSeniorMember: Int = 0, // 示例: 0

    /** （？） */
    @SerialName("mcn_info") val mcnInfo: JsonObject? = null,

    /** （？） */
    @SerialName("gaia_res_type") val gaiaResType: Int = 0, // 示例: 0

    /** （？） */
    @SerialName("gaia_data") val gaiaData: JsonObject? = null,

    /** （？） */
    @SerialName("is_risk") val isRisk: Boolean = false, // 示例: false

    /** 充电信息 */
    @SerialName("elec") val elec: Elec,

    /** 是否显示老粉计划 */
    @SerialName("contract") val contract: Contract? = null,

    /** （？） */
    @SerialName("certificate_show") val certificateShow: Boolean = false, // 示例: false

    /** 昵称渲染信息 有效时：obj, 无效时：null */
    @SerialName("name_render") val nameRender: JsonObject? = null
) {
    /**
     * 粉丝勋章信息
     */
    @Serializable
    data class FansMedal(
        /** 是否显示粉丝勋章 */
        @SerialName("show") val show: Boolean = false, // 示例: true

        /** 是否佩戴了粉丝勋章 */
        @SerialName("wear") val wear: Boolean = false, // 示例: true

        /** 粉丝勋章信息 */
        @SerialName("medal") val medal: Medal? = null
    ) {
        /**
         * 粉丝勋章详细信息
         */
        @Serializable
        data class Medal(
            /** 此用户mid */
            @SerialName("uid") val uid: Long = 0, // 示例: 2

            /** 粉丝勋章所属UP的mid */
            @SerialName("target_id") val targetId: Long = 0, // 示例: 548076

            /** 粉丝勋章id */
            @SerialName("medal_id") val medalId: Int = 0, // 示例: 32525

            /** 粉丝勋章等级 */
            @SerialName("level") val level: Int = 0, // 示例: 28

            /** 粉丝勋章名称 */
            @SerialName("medal_name") val medalName: String = "", // 示例: "桜樱怪"

            /** 颜色 */
            @SerialName("medal_color") val medalColor: Int = 0, // 示例: 398668

            /** 当前亲密度 */
            @SerialName("intimacy") val intimacy: Int = 0, // 示例: 25364

            /** 下一等级所需亲密度 */
            @SerialName("next_intimacy") val nextIntimacy: Int = 0, // 示例: 160000

            /** 每日亲密度获取上限 */
            @SerialName("day_limit") val dayLimit: Int = 0, // 示例: 250000

            /** 今日已获得亲密度 */
            @SerialName("today_feed") val todayFeed: Int = 0, // 示例: 2382

            /** 粉丝勋章颜色 十进制数，可转为十六进制颜色代码 */
            @SerialName("medal_color_start") val medalColorStart: Int = 0, // 示例: 398668

            /** 粉丝勋章颜色 十进制数，可转为十六进制颜色代码 */
            @SerialName("medal_color_end") val medalColorEnd: Int = 0, // 示例: 6850801

            /** 粉丝勋章边框颜色 十进制数，可转为十六进制颜色代码 */
            @SerialName("medal_color_border") val medalColorBorder: Int = 0, // 示例: 6809855

            /**  */
            @SerialName("is_lighted") val isLighted: Int = 0, // 示例: 1

            /**  */
            @SerialName("light_status") val lightStatus: Int = 0, // 示例: 1

            /** 当前是否佩戴 0：未佩戴, 1：已佩戴 */
            @SerialName("wearing_status") val wearingStatus: Int = 0, // 示例: 1

            /**  */
            @SerialName("score") val score: Int = 0 // 示例: 50185364
        )
    }

    /**
     * 认证信息
     */
    @Serializable
    data class Official(
        /** 认证类型 见用户认证类型一览 */
        @SerialName("role") val role: Int = 0, // 示例: 2

        /** 认证信息 无为空 */
        @SerialName("title") val title: String = "", // 示例: "bilibili创始人（站长）"

        /** 认证备注 无为空 */
        @SerialName("desc") val desc: String = "", // 示例: ""

        /** 是否认证 -1：无, 0：个人认证, 1：机构认证 */
        @SerialName("type") val type: Int = 0 // 示例: 0
    )

    /**
     * 会员信息
     */
    @Serializable
    data class Vip(
        /** 会员类型 0：无, 1：月大会员, 2：年度及以上大会员 */
        @SerialName("type") val type: Int = 0, // 示例: 2

        /** 会员状态 0：无, 1：有 */
        @SerialName("status") val status: Int = 0, // 示例: 1

        /** 会员过期时间 毫秒时间戳 */
        @SerialName("due_date") val dueDate: Long = 0, // 示例: 3979555200000

        /** 支付类型 0：未开启自动续费, 1：已开启自动续费 */
        @SerialName("vip_pay_type") val vipPayType: Int = 0, // 示例: 0

        /** 0 作用尚不明确 */
        @SerialName("theme_type") val themeType: Int = 0, // 示例: 0

        /** 会员标签 */
        @SerialName("label") val label: Label,

        /** 是否显示会员图标 0：不显示, 1：显示 */
        @SerialName("avatar_subscript") val avatarSubscript: Int = 0, // 示例: 1

        /** 会员昵称颜色 颜色码，一般为#FB7299，曾用于愚人节改变大会员配色 */
        @SerialName("nickname_color") val nicknameColor: String = "", // 示例: "#FB7299"

        /** 大角色类型 1：月度大会员, 3：年度大会员, 7：十年大会员, 15：百年大会员 */
        @SerialName("role") val role: Int = 0, // 示例: 7

        /** 大会员角标地址 */
        @SerialName("avatar_subscript_url") val avatarSubscriptUrl: String = "", // 示例: ""

        /** 电视大会员状态 0：未开通 */
        @SerialName("tv_vip_status") val tvVipStatus: Int = 0, // 示例: 1

        /** 电视大会员支付类型 */
        @SerialName("tv_vip_pay_type") val tvVipPayType: Int = 0, // 示例: 1

        /** 电视大会员过期时间 秒级时间戳 */
        @SerialName("tv_due_date") val tvDueDate: Int = 0, // 示例: 2003500800
    ) {
        /**
         * 会员标签
         */
        @Serializable
        data class Label(
            /** 空 作用尚不明确 */
            @SerialName("path") val path: String = "", // 示例: ""

            /** 会员类型文案 大会员 年度大会员 十年大会员 百年大会员 最强绿鲤鱼 */
            @SerialName("text") val text: String = "", // 示例: "十年大会员"

            /** 会员标签 vip：大会员, annual_vip：年度大会员, ten_annual_vip：十年大会员, hundred_annual_vip：百年大会员, fools_day_hundred_annual_vip：最强绿鲤鱼 */
            @SerialName("label_theme") val labelTheme: String = "", // 示例: "ten_annual_vip"

            /** 会员标签 */
            @SerialName("text_color") val textColor: String = "", // 示例: "#FFFFFF"

            /** 1 */
            @SerialName("bg_style") val bgStyle: Int = 0, // 示例: 1

            /** 会员标签背景颜色 颜色码，一般为#FB7299，曾用于愚人节改变大会员配色 */
            @SerialName("bg_color") val bgColor: String = "", // 示例: "#FB7299"

            /** 会员标签边框颜色 未使用 */
            @SerialName("border_color") val borderColor: String = "", // 示例: ""

            /**  */
            @SerialName("use_img_label") val useImgLabel: Boolean = false, // 示例: true

            /** 空串 */
            @SerialName("img_label_uri_hans") val imgLabelUriHans: String = "", // 示例: "https://i0.hdslb.com/bfs/activity-plat/static/20220608/e369244d0b14644f5e1a06431e22a4d5/wltavwHAkL.gif"

            /** 空串 */
            @SerialName("img_label_uri_hant") val imgLabelUriHant: String = "", // 示例: ""

            /** 大会员牌子图片 简体版 */
            @SerialName("img_label_uri_hans_static") val imgLabelUriHansStatic: String = "", // 示例: "https://i0.hdslb.com/bfs/vip/802418ff03911645648b63aa193ba67997b5a0bc.png"

            /** 大会员牌子图片 繁体版 */
            @SerialName("img_label_uri_hant_static") val imgLabelUriHantStatic: String = "" // 示例: "https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/8u7iRTPE7N.png"
        )

    }

    /**
     * 头像框信息
     *
     * 普通头像框的image与image_enhance内容相同
     * 动态头像框的image为png静态图片，image_enhance为webp动态图片，image_enhance_frame为png逐帧序列
     */
    @Serializable
    data class Pendant(
        /** 头像框id */
        @SerialName("pid") val pid: Long = 0, // 示例: 32257

        /** 头像框名称 */
        @SerialName("name") val name: String = "", // 示例: "EveOneCat2"

        /** 头像框图片url */
        @SerialName("image") val image: String = "", // 示例: "https://i2.hdslb.com/bfs/garb/item/488870931b1bba66da36d22848f0720480d3d79a.png"

        /** 过期时间 此接口返回恒为0 */
        @SerialName("expire") val expire: Int = 0, // 示例: 0

        /** 头像框图片url */
        @SerialName("image_enhance") val imageEnhance: String = "", // 示例: "https://i2.hdslb.com/bfs/garb/item/5974f17f9d96a88bafba2f6d18d647a486e88312.webp"

        /** 头像框图片逐帧序列url */
        @SerialName("image_enhance_frame") val imageEnhanceFrame: String = "", // 示例: "https://i2.hdslb.com/bfs/garb/item/4316a3910bb0bd6f2f1c267a3e9187f0b9fe5bd0.png"

        /** 新版头像框id */
        @SerialName("n_pid") val nPid: Long = 0 // 示例: 32257
    )

    /**
     * 勋章信息
     */
    @Serializable
    data class Nameplate(
        /** 勋章id */
        @SerialName("nid") val nid: Int = 0, // 示例: 10

        /** 勋章名称 */
        @SerialName("name") val name: String = "", // 示例: "见习偶像"

        /** 勋章图标 */
        @SerialName("image") val image: String = "", // 示例: "https://i2.hdslb.com/bfs/face/e93dd9edfa7b9e18bf46fd8d71862327a2350923.png"

        /** 勋章图标（小） */
        @SerialName("image_small") val imageSmall: String = "", // 示例: "https://i2.hdslb.com/bfs/face/275b468b043ec246737ab8580a2075bee0b1263b.png"

        /** 勋章等级 */
        @SerialName("level") val level: String = "", // 示例: "普通勋章"

        /** 获取条件 */
        @SerialName("condition") val condition: String = "" // 示例: "所有自制视频总播放数>=10万"
    )

    /**
     * 用户荣誉信息
     */
    @Serializable
    data class UserHonourInfo(
        /** 0 */
        @SerialName("mid") val mid: Int = 0, // 示例: 0

        /** null */
        @SerialName("colour") val colour: String? = null, // 示例: null

        /** null */
        @SerialName("tags") val tags: List<String>? = null // 示例: []
    )


    /**
     * 系统通知
     *
     * 主要用于展示如用户争议、纪念账号等等的小黄条
     */
    @Serializable
    data class SysNotice(
        /** id */
        @SerialName("id") val id: Int = 0, // 示例: 5

        /** 显示文案 */
        @SerialName("content") val content: String = "", // 示例: "该用户存在争议行为，已冻结其帐号功能的使用"

        /** 跳转地址 */
        @SerialName("url") val url: String = "", // 示例: ""

        /** 提示类型 1,2 */
        @SerialName("notice_type") val noticeType: Int = 0, // 示例: 1

        /** 前缀图标 */
        @SerialName("icon") val icon: String = "", // 示例: ""

        /** 文字颜色 */
        @SerialName("text_color") val textColor: String = "", // 示例: ""

        /** 背景颜色 */
        @SerialName("bg_color") val bgColor: String = "" // 示例: ""
    )

    /**
     * 直播间信息
     */
    @Serializable
    data class LiveRoom(
        /** 直播间状态 0：无房间, 1：有房间 */
        @SerialName("roomStatus") val roomStatus: Int = 0, // 示例: 1

        /** 直播状态 0：未开播, 1：直播中 */
        @SerialName("liveStatus") val liveStatus: Int = 0, // 示例: 0

        /** 直播间网页 url */
        @SerialName("url") val url: String = "", // 示例: "https://live.bilibili.com/1024?broadcast_type=0&is_room_feed=0"

        /** 直播间标题 */
        @SerialName("title") val title: String = "", // 示例: "试图恰鸡"

        /** 直播间封面 url */
        @SerialName("cover") val cover: String = "", // 示例: "http://i0.hdslb.com/bfs/live/new_room_cover/96ee5bfd0279a0f18b190340334f43f473038288.jpg"

        /**  */
        @SerialName("watched_show") val watchedShow: WatchedShow,

        /** 直播间 id */
        @SerialName("roomid") val roomId: Long = 0, // 示例: 1024

        /** 轮播状态 0：未轮播, 1：轮播 */
        @SerialName("roundStatus") val roundStatus: Int = 0, // 示例: 0

        /** 0 */
        @SerialName("broadcast_type") val broadcastType: Int = 0 // 示例: 0
    ) {
        /**
         * （？）
         */
        @Serializable
        data class WatchedShow(
            /** ? */
            @SerialName("switch") val switch: Boolean = false, // 示例: true

            /** total watched users */
            @SerialName("num") val num: Int = 0, // 示例: 3

            /**  */
            @SerialName("text_small") val textSmall: String = "", // 示例: "3"

            /**  */
            @SerialName("text_large") val textLarge: String = "", // 示例: "3人看过"

            /** watched icon url */
            @SerialName("icon") val icon: String = "", // 示例: "https://i0.hdslb.com/bfs/live/a725a9e61242ef44d764ac911691a7ce07f36c1d.png"

            /** ? */
            @SerialName("icon_location") val iconLocation: String = "", // 示例: ""

            /** watched icon url */
            @SerialName("icon_web") val iconWeb: String = "" // 示例: "https://i0.hdslb.com/bfs/live/8d9d0f33ef8bf6f308742752d13dd0df731df19c.png"
        )
    }

    /**
     * 学校信息
     */
    @Serializable
    data class School(
        /** 就读大学名称 没有则为空 */
        @SerialName("name") val name: String = "" // 示例: ""
    )

    /**
     * 专业资质信息
     */
    @Serializable
    data class Profession(
        /** 资质名称 */
        @SerialName("name") val name: String = "", // 示例: ""

        /** 职位 */
        @SerialName("department") val department: String = "", // 示例: ""

        /** 所属机构 */
        @SerialName("title") val title: String = "", // 示例: ""

        /** 是否显示 0：不显示, 1：显示 */
        @SerialName("is_show") val isShow: Int = 0 // 示例: 0
    )

    /**
     * （？）
     */
    @Serializable
    data class Series(
        /** (?) */
        @SerialName("user_upgrade_status") val userUpgradeStatus: Int = 0, // 示例: 3

        /** (?) */
        @SerialName("show_upgrade_window") val showUpgradeWindow: Boolean = false // 示例: false
    )

    /**
     * 充电信息
     */
    @Serializable
    data class Elec(
        /** 显示的充电信息 */
        @SerialName("show_info") val showInfo: ShowInfo
    ) {
        /**
         * 显示的充电信息
         */
        @Serializable
        data class ShowInfo(
            /** 是否显示充电按钮 */
            @SerialName("show") val show: Boolean = false, // 示例: true

            /** 充电功能开启状态 -1：未开通充电功能, 1：已开通自定义充电, 2：已开通包月、自定义充电, 3：已开通包月高档、自定义充电 */
            @SerialName("state") val state: Int = 0, // 示例: 1

            /** 充电按钮显示文字 空字符串或 充电 或 充电中 */
            @SerialName("title") val title: String = "", // 示例: ""

            /** 充电图标 */
            @SerialName("icon") val icon: String = "", // 示例: ""

            /** 跳转url */
            @SerialName("jump_url") val jumpUrl: String = "" // 示例: "?oid=2"
        )
    }

    /**
     * 老粉计划信息
     */
    @Serializable
    data class Contract(
        /** true/false 在页面中未使用此字段 */
        @SerialName("is_display") val isDisplay: Boolean = false, // 示例: false

        /** 是否在显示老粉计划 true：显示, false：不显示 */
        @SerialName("is_follow_display") val isFollowDisplay: Boolean = false // 示例: false
    )
}