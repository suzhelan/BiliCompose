package top.suzhelan.bili.comment.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 指定评论的回复详情。
 *
 * 对应接口：GET /x/v2/reply/reply
 * - root 是被打开的主评论，适合作为详情页顶部的主视图。
 * - replies 是回复 root 的二级评论列表，适合按时间顺序排在底部。
 */
@Serializable
data class CommentReplyPage(
    @SerialName("page")
    val page: Page = Page(),
    @SerialName("replies")
    val replies: List<NewComment>? = null,
    @SerialName("root")
    val root: NewComment = NewComment(),
) {
    @Serializable
    data class Page(
        @SerialName("count")
        val count: Int = 0,
        @SerialName("num")
        val num: Int = 1,
        @SerialName("size")
        val size: Int = 20,
    )
}
