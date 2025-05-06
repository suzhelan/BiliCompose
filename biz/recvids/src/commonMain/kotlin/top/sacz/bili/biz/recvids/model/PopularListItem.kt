package top.sacz.bili.biz.recvids.model

import androidx.compose.runtime.Immutable
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * AI程序员驯服指南
 * 根据下方的json示例 为字段添加注释 注释需要包含说明和示例值,如果字段是obj那么则给obj本身类的对象添加注释
 * {...}
 *
 */

/**
 * 热门视频
 */
@Serializable
data class PopularListItem(
    @SerialName("list")
    val list: List<PopularItem> = listOf(),
    @SerialName("no_more")
    val noMore: Boolean = false // 表示下页没有了数据 如果为true则表示没有更多数据了，示例值：false
)

@Serializable
@Immutable
data class PopularItem(
    @SerialName("aid")
    val aid: Long = 0L, // 视频的AV号，示例值：114396604926350
    @SerialName("bvid")
    val bvid: String = "", // 视频的BV号，示例值："BV1EbLEzgEKe"
    @SerialName("cid")
    val cid: Long = 0, // 视频的CID，示例值：29598812533
    @SerialName("copyright")
    val copyright: Int = 0, // 版权信息，1表示原创，2表示转载，示例值：1
    @SerialName("cover43")
    val cover43: String = "", // 4:3封面图，示例值：""
    @SerialName("ctime")
    val ctime: Int = 0, // 视频创建时间，示例值：1745553839
    @SerialName("desc")
    val desc: String = "", // 视频描述，示例值："-"
    @SerialName("dimension")
    val dimension: Dimension = Dimension(), // 视频分辨率信息，示例值：{"width": 960, "height": 720, "rotate": 0}
    @SerialName("duration")
    val duration: Int = 0, // 视频时长，单位：秒，示例值：47
    @SerialName("dynamic")
    val `dynamic`: String = "", // 动态内容，示例值：""
    @SerialName("enable_vt")
    val enableVt: Int = 0, // 是否启用VT，示例值：0
    @SerialName("first_frame")
    val firstFrame: String = "", // 视频第一帧图片，示例值："http://i2.hdslb.com/bfs/storyff/n250425a22jepo9y5rjab2qsi9ug2euh_firsti.jpg"
    @SerialName("is_ogv")
    val isOgv: Boolean = false, // 是否为OGV视频，示例值：false
    @SerialName("mission_id")
    val missionId: Int = 0, // 任务ID，示例值：0
    @SerialName("owner")
    val owner: Owner = Owner(), // 视频UP主信息，示例值：{"mid": 3546661762631823, "name": "沉星纪录片", "face": "https://i0.hdslb.com/bfs/face/ca8ebe1d87000add24a084d3f73e044689e53cf2.jpg"}
    @SerialName("pic")
    val pic: String = "", // 视频封面图，示例值："http://i1.hdslb.com/bfs/archive/3ec65e32c48dc005ed3c428d490cb70ba81bb7e0.jpg"
    @SerialName("pub_location")
    val pubLocation: String = "", // 发布地点，示例值："河南"
    @SerialName("pubdate")
    val pubdate: Int = 0, // 视频发布时间，示例值：1745553838
    @SerialName("rcmd_reason")
    val rcmdReason: RcmdReason = RcmdReason(), // 推荐理由，示例值：{"content": "百万播放", "corner_mark": 1}
    @SerialName("rights")
    val rights: Rights = Rights(), // 视频权限信息，示例值：{"bp": 0, "elec": 0, "download": 0, "movie": 0, "pay": 0, "hd5": 0, "no_reprint": 0, "autoplay": 1, "ugc_pay": 0, "is_cooperation": 0, "ugc_pay_preview": 0, "no_background": 0, "arc_pay": 0, "pay_free_watch": 0}
    @SerialName("season_type")
    val seasonType: Int = 0, // 季节类型，示例值：0
    @SerialName("short_link_v2")
    val shortLinkV2: String = "", // 短链接，示例值："https://b23.tv/BV1EbLEzgEKe"
    @SerialName("stat")
    val stat: Stat = Stat(), // 视频统计信息，示例值：{"aid": 114396604926350, "view": 1259292, "danmaku": 21778, "reply": 6530, "favorite": 13437, "coin": 1772, "share": 40014, "now_rank": 0, "his_rank": 95, "like": 144078, "dislike": 0, "vt": 0, "vv": 1259292}
    @SerialName("state")
    val state: Int = 0, // 视频状态，示例值：0
    @SerialName("tid")
    val tid: Int = 0, // 分区ID，示例值：183
    @SerialName("title")
    val title: String = "", // 视频标题，示例值："大型纪录片《回到五一假期》#大型纪录片#五一假期 #大学生"
    @SerialName("tname")
    val tname: String = "", // 分区名称，示例值："影视剪辑"
    @SerialName("up_from_v2")
    val upFromV2: Int = 0, // UP主来源，示例值：35
    @SerialName("videos")
    val videos: Int = 0 // 视频数量，示例值：1
) {
    @Serializable
    data class Dimension(
        @SerialName("height")
        val height: Int = 0, // 视频高度，示例值：720
        @SerialName("rotate")
        val rotate: Int = 0, // 视频旋转角度，示例值：0
        @SerialName("width")
        val width: Int = 0 // 视频宽度，示例值：960
    )

    @Serializable
    data class Owner(
        @SerialName("face")
        val face: String = "", // UP主头像，示例值："https://i0.hdslb.com/bfs/face/ca8ebe1d87000add24a084d3f73e044689e53cf2.jpg"
        @SerialName("mid")
        val mid: Long = 0L, // UP主ID，示例值：3546661762631823
        @SerialName("name")
        val name: String = "" // UP主名称，示例值："沉星纪录片"
    )

    @Serializable
    data class RcmdReason(
        @SerialName("content")
        val content: String = "", // 推荐理由内容，示例值："百万播放"
        @SerialName("corner_mark")
        val cornerMark: Int = 0 // 推荐理由角标，示例值：1
    )

    @Serializable
    data class Rights(
        @SerialName("arc_pay")
        val arcPay: Int = 0, // 是否支持付费观看，示例值：0
        @SerialName("autoplay")
        val autoplay: Int = 0, // 是否自动播放，示例值：1
        @SerialName("bp")
        val bp: Int = 0, // 是否支持投币，示例值：0
        @SerialName("download")
        val download: Int = 0, // 是否支持下载，示例值：0
        @SerialName("elec")
        val elec: Int = 0, // 是否支持充电，示例值：0
        @SerialName("hd5")
        val hd5: Int = 0, // 是否支持高清5，示例值：0
        @SerialName("is_cooperation")
        val isCooperation: Int = 0, // 是否为合作视频，示例值：0
        @SerialName("movie")
        val movie: Int = 0, // 是否为电影，示例值：0
        @SerialName("no_background")
        val noBackground: Int = 0, // 是否无背景，示例值：0
        @SerialName("no_reprint")
        val noReprint: Int = 0, // 是否禁止转载，示例值：0
        @SerialName("pay")
        val pay: Int = 0, // 是否付费，示例值：0
        @SerialName("pay_free_watch")
        val payFreeWatch: Int = 0, // 是否支持免费观看，示例值：0
        @SerialName("ugc_pay")
        val ugcPay: Int = 0, // 是否支持UGC付费，示例值：0
        @SerialName("ugc_pay_preview")
        val ugcPayPreview: Int = 0 // 是否支持UGC付费预览，示例值：0
    )

    @Serializable
    data class Stat(
        @SerialName("aid")
        val aid: Long = 0L, // 视频AV号，示例值：114396604926350
        @SerialName("coin")
        val coin: Int = 0, // 投币数，示例值：1772
        @SerialName("danmaku")
        val danmaku: Int = 0, // 弹幕数，示例值：21778
        @SerialName("dislike")
        val dislike: Int = 0, // 不喜欢数，示例值：0
        @SerialName("favorite")
        val favorite: Int = 0, // 收藏数，示例值：13437
        @SerialName("his_rank")
        val hisRank: Int = 0, // 历史最高排名，示例值：95
        @SerialName("like")
        val like: Int = 0, // 点赞数，示例值：144078
        @SerialName("now_rank")
        val nowRank: Int = 0, // 当前排名，示例值：0
        @SerialName("reply")
        val reply: Int = 0, // 评论数，示例值：6530
        @SerialName("share")
        val share: Int = 0, // 分享数，示例值：40014
        @SerialName("view")
        val view: Int = 0, // 播放数，示例值：1259292
        @SerialName("vt")
        val vt: Int = 0, // VT值，示例值：0
        @SerialName("vv")
        val vv: Int = 0 // VV值，示例值：1259292
    )
}

