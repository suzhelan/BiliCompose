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
    val replies: List<NewComment>? = null,
    @SerialName("hots")
    val hots: List<NewComment>? = null,
    @SerialName("top_replies")
    val topReplies: List<NewComment>? = null,
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

}
