package top.sacz.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class Relation(
    @SerialName("attribute")
    val attribute: Int, // 关系 0：未关注 1：悄悄关注（已弃用） 2：已关注 6：已互粉 128：已拉黑
    @SerialName("mid")
    val mid: Long, // mid
    @SerialName("mtime")
    val mtime: Int, // 1601654227
    @SerialName("special")
    val special: Int, // 0
    @SerialName("tag")
    val tag: List<Int>? // null
)


/**
 * 查询分组列表
 */
@Serializable
data class RelationTags(
    @SerialName("count")
    val count: Int, // 3
    @SerialName("name")
    val name: String, // 默认分组
    @SerialName("tagid")
    val tagid: Int, // 0
    @SerialName("tip")
    val tip: String
)

/**
 * 通过分组列表查询的对象
 */
@Serializable
data class RelationUser(
    @SerialName("attribute")
    var attribute: Int, // 0
    @SerialName("face")
    val face: String, // https://i2.hdslb.com/bfs/face/18e3b77e40cc76ac35b904a994b499a23e449b08.webp
    @SerialName("face_nft")
    val faceNft: Int, // 0
    @SerialName("follow_time")
    val followTime: String,
    @SerialName("live")
    val live: Live,
    @SerialName("mid")
    val mid: Long, // 401913755
    @SerialName("nft_icon")
    val nftIcon: String, // https://i0.hdslb.com/bfs/activity-plat/static/20220506/334553dd7c506a92b88eaf4d59ac8b4d/j8AeXAkEul.gif
    @SerialName("official_verify")
    val officialVerify: OfficialVerify,
    @SerialName("rec_reason")
    val recReason: String,
    @SerialName("sign")
    val sign: String, // 是猫猫！
    @SerialName("special")
    val special: Int, // 0
    @SerialName("track_id")
    val trackId: String,
    @SerialName("uname")
    val uname: String, // 神楽乃
    @SerialName("vip")
    val vip: Vip
) {
    /**
     * 认证信息
     */
    @Serializable
    data class OfficialVerify(
        @SerialName("desc")
        val desc: String,//如果有认证的话显示为认证信息
        @SerialName("type")
        val type: Int // -1：无 0：UP 主认证 1：机构认证
    )

    @Serializable
    data class Vip(
        @SerialName("accessStatus")
        val accessStatus: Int, // 0
        @SerialName("avatar_subscript")
        val avatarSubscript: Int, // 1
        @SerialName("avatar_subscript_url")
        val avatarSubscriptUrl: String,
        @SerialName("dueRemark")
        val dueRemark: String,
        @SerialName("label")
        val label: Label,
        @SerialName("nickname_color")
        val nicknameColor: String, // #FB7299
        @SerialName("themeType")
        val themeType: Int, // 0
        @SerialName("vipDueDate")
        val vipDueDate: Long, // 1784563200000
        @SerialName("vipStatus")
        val vipStatus: Int, // 1
        @SerialName("vipStatusWarn")
        val vipStatusWarn: String,
        @SerialName("vipType")
        val vipType: Int // 2
    )

    @Serializable
    data class Label(
        @SerialName("bg_color")
        val bgColor: String, // #FB7299
        @SerialName("bg_style")
        val bgStyle: Int, // 1
        @SerialName("border_color")
        val borderColor: String,
        @SerialName("label_theme")
        val labelTheme: String, // annual_vip
        @SerialName("path")
        val path: String, // http://i0.hdslb.com/bfs/vip/label_annual.png
        @SerialName("text")
        val text: String, // 年度大会员
        @SerialName("text_color")
        val textColor: String // #FFFFFF
    )

    @Serializable
    data class Live(
        @SerialName("jump_url")
        val jumpUrl: String,
        @SerialName("live_status")
        val liveStatus: Int // 0
    )
}





