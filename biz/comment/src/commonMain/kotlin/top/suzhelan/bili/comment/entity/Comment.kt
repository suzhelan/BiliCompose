package top.suzhelan.bili.comment.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 超复杂评论区条目对象
 */
@Serializable
data class Comment(
    @SerialName("action")
    val action: Int, // 0
    @SerialName("assist")
    val assist: Int, // 0
    @SerialName("attr")
    val attr: Int, // 0
    @SerialName("content")
    val content: Content,
    @SerialName("count")
    val count: Int, // 0
    @SerialName("ctime")
    val ctime: Int, // 1759050606
    @SerialName("dialog")
    val dialog: Long, // 0
    @SerialName("dialog_str")
    val dialogStr: String, // 0
    @SerialName("dynamic_id_str")
    val dynamicIdStr: String, // 0
    @SerialName("fansgrade")
    val fansgrade: Int, // 0
    @SerialName("folder")
    val folder: Folder,
    @SerialName("invisible")
    val invisible: Boolean, // false
    @SerialName("like")
    val like: Int, // 0
    @SerialName("member")
    val member: Member,
    @SerialName("mid")
    val mid: Long, // 378491392
    @SerialName("mid_str")
    val midStr: String, // 378491392
    @SerialName("note_cvid_str")
    val noteCvidStr: String, // 0
    @SerialName("oid")
    val oid: Long, // 115280630192742
    @SerialName("oid_str")
    val oidStr: String, // 115280630192742
    @SerialName("parent")
    val parent: Long, // 0
    @SerialName("parent_str")
    val parentStr: String, // 0
    @SerialName("rcount")
    val rcount: Int, // 0
    @SerialName("replies")
    val replies: List<Comment>?, // 评论回复条目预览	仅嵌套一层 否则为 null
    @SerialName("reply_control")
    val replyControl: ReplyControl,
    @SerialName("root")
    val root: Long, // 0
    @SerialName("root_str")
    val rootStr: String, // 0
    @SerialName("rpid")
    val rpid: Long, // 275929471521
    @SerialName("rpid_str")
    val rpidStr: String, // 275929471521
    @SerialName("state")
    val state: Int, // 0
    @SerialName("track_info")
    val trackInfo: String,
    @SerialName("type")
    val type: Int, // 1
    @SerialName("up_action")
    val upAction: UpAction
) {
    @Serializable
    data class Content(
        @SerialName("emote")
        val emote: Map<String, Emote> = emptyMap(),
        @SerialName("max_line")
        val maxLine: Int, // 6
        @SerialName("members")
        val members: List<Member> = emptyList(),
        @SerialName("message")
        val message: String, // 宝宝好可爱[喜欢],
        @SerialName("pictures")
        val pictures: List<Picture> = emptyList(),
    ) {
        @Serializable
        data class Picture(
            @SerialName("img_src")
            val imgSrc: String,
            @SerialName("img_size")
            val imgSize: Double,
            @SerialName("img_width")
            val imgWidth: Int, // 0
            @SerialName("img_height")
            val imgHeight: Int, // 0
        )

        @Serializable
        data class Emote(
            @SerialName("attr")
            val attr: Int, // 0
            @SerialName("id")
            val id: Int, // 3
            @SerialName("jump_title")
            val jumpTitle: String, // 喜欢
            @SerialName("meta")
            val meta: Meta,
            @SerialName("mtime")
            val mtime: Int, // 1668688325
            @SerialName("package_id")
            val packageId: Int, // 1
            @SerialName("state")
            val state: Int, // 0
            @SerialName("text")
            val text: String, // [喜欢]
            @SerialName("type")
            val type: Int, // 1
            @SerialName("url")
            val url: String // https://i0.hdslb.com/bfs/emote/8a10a4d73a89f665feff3d46ca56e83dc68f9eb8.png
        ) {
            @Serializable
            data class Meta(
                @SerialName("size")
                val size: Int, // 1
                @SerialName("suggest")
                val suggest: List<String> = emptyList(),
            )
        }
    }

    @Serializable
    data class Folder(
        @SerialName("has_folded")
        val hasFolded: Boolean, // false
        @SerialName("is_folded")
        val isFolded: Boolean, // false
        @SerialName("rule")
        val rule: String
    )

    @Serializable
    data class Member(
        @SerialName("avatar")
        val avatar: String, // https://i1.hdslb.com/bfs/face/b8bf52bb209be7d6a531ea318af0bf860bc443c6.jpg
        @SerialName("face_nft_new")
        val faceNftNew: Int, // 0
        @SerialName("contract_desc")
        val contractDesc: String? = null,//合作用户说明
        @SerialName("is_contractor")
        val isContractor: Boolean? = null, // false 是否合作用户
        @SerialName("is_senior_member")
        val isSeniorMember: Int, // 1
        @SerialName("level_info")
        val levelInfo: LevelInfo,
        @SerialName("mid")
        val mid: Long, // 378491392
        @SerialName("nameplate")
        val nameplate: Nameplate,//勋章
        @SerialName("official_verify")
        val officialVerify: OfficialVerify,//认证信息
        @SerialName("pendant")
        val pendant: Pendant,//头像框
        @SerialName("rank")
        val rank: String, // 10000
        @SerialName("sex")
        val sex: String, // 保密
        @SerialName("sign")
        val sign: String,
        @SerialName("uname")
        val uname: String, // ゚゚生盐诺亚
        @SerialName("vip")
        val vip: Vip,
        @SerialName("user_sailing_v2")
        val userSailing: UserSailing?= null

    ) {

        @Serializable
        data class UserSailing(
            @SerialName("card_bg")
            val cardBg: CardBg? = null,
        ) {
            @Serializable
            data class CardBg(
                @SerialName("fan")
                val fan: Fan,
                @SerialName("id")
                val id: Long, // 72238
                @SerialName("image")
                val image: String, // http://i0.hdslb.com/bfs/archive/5a70d93faf507689b3980da92cc4941c20343f57.png
                @SerialName("jump_url")
                val jumpUrl: String, // https://www.bilibili.com/h5/mall/digital-card/home?act_id=105606&from=reply&f_source=garb&-Abrowser=live&hybrid_set_header=2&navhide=1&anchor_task=1
                @SerialName("name")
                val name: String, // ε�������ղؼ�ѫ��
                @SerialName("type")
                val type: String // collect_card
            ) {
                @Serializable
                data class Fan(
                    @SerialName("color")
                    val color: String, // #BFC8D2
                    @SerialName("color_format")
                    val colorFormat: ColorFormat,
                    @SerialName("is_fan")
                    val isFan: Int, // 1
                    @SerialName("num_desc")
                    val numDesc: String, // 039416
                    @SerialName("num_prefix")
                    val numPrefix: String, // CD.
                    @SerialName("number")
                    val number: Int // 39416
                ) {
                    @Serializable
                    data class ColorFormat(
                        @SerialName("colors")
                        val colors: List<String>,
                        @SerialName("end_point")
                        val endPoint: String, // 100,100
                        @SerialName("gradients")
                        val gradients: List<Int>,
                        @SerialName("start_point")
                        val startPoint: String // 0,0
                    )
                }
            }
        }


        @Serializable
        data class LevelInfo(
            @SerialName("current_exp")
            val currentExp: Int, // 0
            @SerialName("current_level")
            val currentLevel: Int, // 6
            @SerialName("current_min")
            val currentMin: Int, // 0
            @SerialName("next_exp")
            val nextExp: Int // 0
        )

        @Serializable
        data class Nameplate(
            @SerialName("condition")
            val condition: String, // 所有自制视频总播放数>=100万，数据次日更新
            @SerialName("image")
            val image: String, // https://i0.hdslb.com/bfs/face/27a952195555e64508310e366b3e38bd4cd143fc.png
            @SerialName("image_small")
            val imageSmall: String, // https://i1.hdslb.com/bfs/face/0497be49e08357bf05bca56e33a0637a273a7610.png
            @SerialName("level")
            val level: String, // 稀有勋章
            @SerialName("name")
            val name: String, // 知名偶像
            @SerialName("nid")
            val nid: Int // 8
        )

        @Serializable
        data class OfficialVerify(
            @SerialName("desc")
            val desc: String,
            @SerialName("type")
            val type: Int // -1
        )

        @Serializable
        data class Pendant(
            @SerialName("expire")
            val expire: Int, // 0
            @SerialName("image")
            val image: String,//头像框链接
            @SerialName("image_enhance")
            val imageEnhance: String,//同上
            @SerialName("image_enhance_frame")
            val imageEnhanceFrame: String,//常态为空
            @SerialName("n_pid")
            val nPid: Long, // 0
            @SerialName("name")
            val name: String,
            @SerialName("pid")
            val pid: Long // 0
        )

        @Serializable
        data class Vip(
            @SerialName("accessStatus")
            val accessStatus: Int, // 0
            @SerialName("avatar_subscript")
            val avatarSubscript: Int, // 1
            @SerialName("dueRemark")
            val dueRemark: String,
            @SerialName("label")
            val label: Label,
            @SerialName("nickname_color")
            val nicknameColor: String, // #FB7299
            @SerialName("themeType")
            val themeType: Int, // 0
            @SerialName("vipDueDate")
            val vipDueDate: Long, // 1791043200000
            @SerialName("vipStatus")
            val vipStatus: Int, // 1
            @SerialName("vipStatusWarn")
            val vipStatusWarn: String,
            @SerialName("vipType")
            val vipType: Int // 2
        ) {
            @Serializable
            data class Label(
                @SerialName("bg_color")
                val bgColor: String, // #FB7299
                @SerialName("bg_style")
                val bgStyle: Int, // 1
                @SerialName("border_color")
                val borderColor: String,
                @SerialName("img_label_uri_hans")
                val imgLabelUriHans: String,
                @SerialName("img_label_uri_hans_static")
                val imgLabelUriHansStatic: String, // https://i0.hdslb.com/bfs/bangumi/kt/e74c74edc802bc4badba15f03e69d7cff75f2679.png
                @SerialName("img_label_uri_hant")
                val imgLabelUriHant: String,
                @SerialName("img_label_uri_hant_static")
                val imgLabelUriHantStatic: String, // https://i0.hdslb.com/bfs/activity-plat/static/20220614/e369244d0b14644f5e1a06431e22a4d5/VEW8fCC0hg.png
                @SerialName("label_goto")
                val labelGoto: LabelGoto?,
                @SerialName("label_id")
                val labelId: Int, // 47
                @SerialName("label_theme")
                val labelTheme: String, // annual_vip
                @SerialName("path")
                val path: String, // http://i0.hdslb.com/bfs/vip/label_annual.png
                @SerialName("text")
                val text: String, // 年度大会员
                @SerialName("text_color")
                val textColor: String, // #FFFFFF
                @SerialName("use_img_label")
                val useImgLabel: Boolean // true
            ) {
                @Serializable
                data class LabelGoto(
                    @SerialName("mobile")
                    val mobile: String, // https://big.bilibili.com/mobile/index?appId=125&appSubId=minebutton&banner_id=34281&popup_id=34279
                    @SerialName("pc_web")
                    val pcWeb: String // https://account.bilibili.com/big?from_spmid=vipicon
                )
            }
        }
    }

    @Serializable
    data class ReplyControl(
        @SerialName("location")
        val location: String, // IP属地：广东
        @SerialName("max_line")
        val maxLine: Int, // 6
        @SerialName("time_desc")
        val timeDesc: String, // 23秒前发布
        @SerialName("translation_switch")
        val translationSwitch: Int // 1
    )

    @Serializable
    data class UpAction(
        @SerialName("like")
        val like: Boolean, // false
        @SerialName("reply")
        val reply: Boolean // false
    )
}


/**
 * 评论条目对象
 */
