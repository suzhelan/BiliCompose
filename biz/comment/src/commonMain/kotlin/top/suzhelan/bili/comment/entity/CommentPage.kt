package top.suzhelan.bili.comment.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * 翻页获取评论对象
 */
@Serializable
data class CommentPage(
    @SerialName("config")
    val config: Config,//配置
    @SerialName("page")
    val page: Page,//页码信息
    @SerialName("replies")
    val replies: List<Comment>? =  null,//评论列表
    @SerialName("hots")
    val hots: List<Comment>? =  null,//热门评论
    @SerialName("upper")
    val upper: Upper,//置顶信息
    @SerialName("notice")
    val notice: Notice? = null,//公告
    @SerialName("control")
    val control: Control,//操作配置
) {
    @Serializable
    data class Control(
        @SerialName("answer_guide_android_url")
        val answerGuideAndroidUrl: String, // https://www.bilibili.com/h5/newbie/entry?navhide=1&re_src=6
        @SerialName("answer_guide_icon_url")
        val answerGuideIconUrl: String, // http://i0.hdslb.com/bfs/emote/96940d16602cacbbac796245b7bb99fa9b5c970c.png
        @SerialName("answer_guide_ios_url")
        val answerGuideIosUrl: String, // https://www.bilibili.com/h5/newbie/entry?navhide=1&re_src=12
        @SerialName("answer_guide_text")
        val answerGuideText: String, // 需要升级成为lv2会员后才可以评论，先去答题转正吧！
        @SerialName("bg_text")
        val bgText: String,
        @SerialName("child_input_text")
        val childInputText: String, // 轻轻敲醒沉睡的心灵，让我看看你的点评
        @SerialName("disable_jump_emote")
        val disableJumpEmote: Boolean, // false
        @SerialName("enable_charged")
        val enableCharged: Boolean, // false
        @SerialName("enable_cm_biz_helper")
        val enableCmBizHelper: Boolean, // false
        @SerialName("giveup_input_text")
        val giveupInputText: String, // 不发没关系，请继续友善哦~
        @SerialName("input_disable")
        val inputDisable: Boolean, // false
        @SerialName("root_input_text")
        val rootInputText: String, // 轻轻敲醒沉睡的心灵，让我看看你的点评
        @SerialName("screenshot_icon_state")
        val screenshotIconState: Int, // 1
        @SerialName("show_text")
        val showText: String,
        @SerialName("show_type")
        val showType: Int, // 1
        @SerialName("upload_picture_icon_state")
        val uploadPictureIconState: Int, // 1
        @SerialName("web_selection")
        val webSelection: Boolean // false
    )

    @Serializable
    data class Upper(
        @SerialName("mid")
        val mid: Long, // 378491392
        @SerialName("top")
        val top: Comment?,//置顶评论
        @SerialName("vote")
        val vote: JsonObject?,//投票评论
    )

    @Serializable
    data class Notice(
        @SerialName("content")
        val content: String,
        @SerialName("id")
        val id: Int,
        @SerialName("link")
        val link: String,
        @SerialName("title")
        val title: String,
    )

    @Serializable
    data class Config(
        @SerialName("read_only")
        val readOnly: Boolean, // false 是否只读评论区
        @SerialName("show_up_flag")
        val showUpFlag: Boolean, // true 是否显示up主觉得很赞
        @SerialName("show_del_log")
        val showDelLog: Boolean? = null, // false 是否显示删除日志
        @SerialName("showadmin")
        val showadmin: Int? = null, // 1 是否显示管理置顶
        @SerialName("showentry")
        val showentry: Int? = null, // 1 未知
        @SerialName("showfloor")
        val showfloor: Int? = null, // 0 是否显示楼层号
        @SerialName("showtopic")
        val showtopic: Int? = null // 1 是否显示话题
    )

    @Serializable
    data class Page(
        @SerialName("acount")
        val acount: Int, // 76792 总计评论条数
        @SerialName("count")
        val count: Int, // 60971 根评论条数
        @SerialName("num")
        val num: Int, // 1 当前页
        @SerialName("size")
        val size: Int // 5 每页显示条数
    )
}



