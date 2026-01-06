package top.suzhelan.bili.biz.user.entity


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LikeVideoList(
    @SerialName("count")
    val count: Int,
    @SerialName("item")
    val item: List<LikeVideo>
)

@Serializable
data class LikeVideo(
    @SerialName("author")
    val author: String, // 甜蜜老张
    @SerialName("cover")
    val cover: String, // http://i1.hdslb.com/bfs/archive/58f031a52eab6d5faec5a39dfaab5728f65672d5.jpg
    @SerialName("cover_icon")
    val coverIcon: String,
    @SerialName("ctime")
    val ctime: Int, // 1706320200
    @SerialName("danmaku")
    val danmaku: Int, // 362
    @SerialName("duration")
    val duration: Int, // 216
    @SerialName("goto")
    val goto: String, // av
    @SerialName("icon_type")
    val iconType: Int, // 0
    @SerialName("is_cooperation")
    val isCooperation: Boolean, // false
    @SerialName("is_fold")
    val isFold: Boolean, // false
    @SerialName("is_live_playback")
    val isLivePlayback: Boolean, // false
    @SerialName("is_pgc")
    val isPgc: Boolean, // false
    @SerialName("is_popular")
    val isPopular: Boolean, // false
    @SerialName("is_pugv")
    val isPugv: Boolean, // false
    @SerialName("is_steins")
    val isSteins: Boolean, // false
    @SerialName("is_ugcpay")
    val isUgcpay: Boolean, // false
    @SerialName("length")
    val length: String,
    @SerialName("param")
    val `param`: String, // 624351941
    @SerialName("play")
    val play: Int, // 423652
    @SerialName("publish_time_text")
    val publishTimeText: String,
    @SerialName("state")
    val state: Boolean, // true
    @SerialName("subtitle")
    val subtitle: String,
    @SerialName("title")
    val title: String, // 兄弟难舍也难分
    @SerialName("tname")
    val tname: String,
    @SerialName("ugc_pay")
    val ugcPay: Int, // 0
    @SerialName("uri")
    val uri: String, // bilibili://video/624351941?player_width=1080&player_height=1920&player_rotate=0
    @SerialName("videos")
    val videos: Int, // 0
    @SerialName("view_content")
    val viewContent: String // 42.4万
)


