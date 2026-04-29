package top.suzhelan.bili.comment.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 懒加载获取评论对象
 */
@Serializable
data class CommentLazyPage(
    @SerialName("cursor")
    val cursor: Cursor,
    @SerialName("replies")
    val replies: List<CommentLazy>? = null,
    @SerialName("hots")
    val hots: List<CommentLazy>? = null,
    @SerialName("top_replies")
    val topReplies: List<CommentLazy>? = null,
) {
    @Serializable
    data class Cursor(
        @SerialName("all_count")
        val allCount: Int = 0,
        @SerialName("is_begin")
        val isBegin: Boolean = false,
        @SerialName("prev")
        val prev: Int = 0,
        @SerialName("next")
        val next: Int = 0,
        @SerialName("is_end")
        val isEnd: Boolean = false,
        @SerialName("mode")
        val mode: Int = 0,
        @SerialName("support_mode")
        val supportMode: List<Int> = emptyList(),
        @SerialName("name")
        val name: String = "",
        @SerialName("pagination_reply")
        val paginationReply: PaginationReply? = null,
        @SerialName("session_id")
        val sessionId: String = "",
    ) {
        @Serializable
        data class PaginationReply(
            @SerialName("next_offset")
            val nextOffset: String? = null,
            @SerialName("prev_offset")
            val prevOffset: String? = null,
        )
    }

    /**
     * 懒加载接口的评论条目对象。
     */
    @Serializable
    data class CommentLazy(
        @SerialName("action")
        val action: Int = 0,
        @SerialName("content")
        val content: Content = Content(),
        @SerialName("count")
        val count: Int = 0,
        @SerialName("ctime")
        val ctime: Int = 0,
        @SerialName("folder")
        val folder: Folder = Folder(),
        @SerialName("invisible")
        val invisible: Boolean = false,
        @SerialName("like")
        val like: Int = 0,
        @SerialName("member")
        val member: Member = Member(),
        @SerialName("rcount")
        val rcount: Int = 0,
        @SerialName("replies")
        val replies: List<CommentLazy>? = null,
        @SerialName("reply_control")
        val replyControl: ReplyControl = ReplyControl(),
        @SerialName("rpid")
        val rpid: Long = 0,
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


}
