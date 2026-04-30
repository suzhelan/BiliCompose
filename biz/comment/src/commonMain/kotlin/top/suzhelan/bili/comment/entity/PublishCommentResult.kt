package top.suzhelan.bili.comment.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 发布评论接口返回的数据。
 *
 * 接口成功时会直接返回新评论的完整对象，UI 使用 [reply] 做局部插入，
 * 不需要刷新 Paging 列表，避免评论区全量重组。
 */
@Serializable
data class PublishCommentResult(
    @SerialName("success_action")
    val successAction: Int = 0,
    @SerialName("success_toast")
    val successToast: String = "",
    @SerialName("need_captcha")
    val needCaptcha: Boolean = false,
    @SerialName("url")
    val url: String = "",
    @SerialName("rpid")
    val rpid: Long = 0,
    @SerialName("rpid_str")
    val rpidStr: String = "",
    @SerialName("dialog")
    val dialog: Long = 0,
    @SerialName("dialog_str")
    val dialogStr: String = "",
    @SerialName("root")
    val root: Long = 0,
    @SerialName("root_str")
    val rootStr: String = "",
    @SerialName("parent")
    val parent: Long = 0,
    @SerialName("parent_str")
    val parentStr: String = "",
    @SerialName("reply")
    val reply: NewComment? = null,
)
