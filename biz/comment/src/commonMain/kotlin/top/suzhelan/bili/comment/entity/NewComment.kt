package top.suzhelan.bili.comment.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 统一的评论条目对象。
 *
 * 这个实体来自原来的 CommentLazyPage.CommentLazy。独立成顶层类后，
 * 列表、回复详情和旧分页接口可以复用同一个展示模型。
 */
@Serializable
data class NewComment(
    @SerialName("action")
    val action: Int = 0,
    @SerialName("assist")
    val assist: Int = 0,
    @SerialName("attr")
    val attr: Int = 0,
    @SerialName("content")
    val content: Content = Content(),
    @SerialName("count")
    val count: Int = 0,
    @SerialName("ctime")
    val ctime: Int = 0,
    @SerialName("dialog")
    val dialog: Long = 0,
    @SerialName("floor")
    val floor: Int = 0,
    @SerialName("folder")
    val folder: Folder = Folder(),
    @SerialName("invisible")
    val invisible: Boolean = false,
    @SerialName("like")
    val like: Int = 0,
    @SerialName("member")
    val member: Member = Member(),
    @SerialName("mid")
    val mid: Long = 0,
    @SerialName("oid")
    val oid: Long = 0,
    @SerialName("parent")
    val parent: Long = 0,
    @SerialName("rcount")
    val rcount: Int = 0,
    @SerialName("replies")
    val replies: List<NewComment>? = null,
    @SerialName("reply_control")
    val replyControl: ReplyControl = ReplyControl(),
    @SerialName("root")
    val root: Long = 0,
    @SerialName("rpid")
    val rpid: Long = 0,
    @SerialName("state")
    val state: Int = 0,
    @SerialName("type")
    val type: Int = 0,
    @SerialName("up_action")
    val upAction: UpAction = UpAction(),
) {
    @Serializable
    data class Content(
        @SerialName("emote")
        val emote: Map<String, Emote> = emptyMap(),
        @SerialName("max_line")
        val maxLine: Int = 6,
        @SerialName("members")
        val members: List<Member> = emptyList(),
        @SerialName("message")
        val message: String = "",
        @SerialName("pictures")
        val pictures: List<Picture> = emptyList(),
    ) {
        @Serializable
        data class Picture(
            @SerialName("img_src")
            val imgSrc: String = "",
            @SerialName("img_size")
            val imgSize: Double = 0.0,
            @SerialName("img_width")
            val imgWidth: Int = 0,
            @SerialName("img_height")
            val imgHeight: Int = 0,
        )

        @Serializable
        data class Emote(
            @SerialName("meta")
            val meta: Meta = Meta(),
            @SerialName("text")
            val text: String = "",
            @SerialName("url")
            val url: String = "",
        ) {
            @Serializable
            data class Meta(
                @SerialName("size")
                val size: Int = 1,
            )
        }
    }

    @Serializable
    data class Folder(
        @SerialName("has_folded")
        val hasFolded: Boolean = false,
        @SerialName("is_folded")
        val isFolded: Boolean = false,
        @SerialName("rule")
        val rule: String = "",
    )

    @Serializable
    data class Member(
        @SerialName("avatar")
        val avatar: String = "",
        @SerialName("level_info")
        val levelInfo: LevelInfo = LevelInfo(),
        @SerialName("mid")
        val mid: Long = 0,
        @SerialName("pendant")
        val pendant: Pendant = Pendant(),
        @SerialName("uname")
        val uname: String = "",
        @SerialName("user_sailing")
        val userSailing: UserSailing? = null,
    ) {
        @Serializable
        data class LevelInfo(
            @SerialName("current_level")
            val currentLevel: Int = 0,
        )

        @Serializable
        data class Pendant(
            @SerialName("image")
            val image: String = "",
        )

        @Serializable
        data class UserSailing(
            @SerialName("cardbg")
            val cardBg: CardBg? = null,
        ) {
            @Serializable
            data class CardBg(
                @SerialName("image")
                val image: String = "",
            )
        }
    }

    @Serializable
    data class ReplyControl(
        @SerialName("location")
        val location: String = "早期评论无属地信息",
        @SerialName("max_line")
        val maxLine: Int = 6,
        @SerialName("time_desc")
        val timeDesc: String = "",
        @SerialName("translation_switch")
        val translationSwitch: Int = 0,
    )

    @Serializable
    data class UpAction(
        @SerialName("like")
        val like: Boolean = false,
        @SerialName("reply")
        val reply: Boolean = false,
    )
}
