package top.suzhelan.bili.biz.shorts.entity

import kotlinx.serialization.Serializable
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item

/**
 * 短视频项实体
 *
 * 封装短视频播放所需的核心数据模型
 * 从推荐流的SmallCoverV2Item转换而来
 *
 * @property aid 视频aid，唯一标识符
 * @property cid 视频cid，分P标识符
 * @property title 视频标题
 * @property cover 封面图URL
 * @property author 作者名称
 * @property authorId 作者UID
 * @property authorAvatar 作者头像URL，可能为空需异步加载
 * @property duration 视频时长（秒）
 * @property playCount 播放量显示文本
 * @property danmakuCount 弹幕数显示文本
 * @property likeCount 点赞数显示文本
 * @property commentCount 评论数显示文本
 * @property coinCount 投币数显示文本
 * @property favoriteCount 收藏数显示文本
 * @property shareCount 转发数显示文本
 * @property isVertical 是否为竖屏视频
 */
@Serializable
data class ShortVideoItem(
    val aid: Long,
    val cid: Long,
    val title: String,
    val cover: String,
    val author: String,
    val authorId: Long,
    val authorAvatar: String = "",
    val followerCount: Int = 0,
    val duration: Int,
    val playCount: String,
    val hasCount: Boolean = false,
    val likeCount: String = "点赞",
    val commentCount: String = "评论",
    val danmakuCount: String = "弹幕(无需展示)",
    val coinCount: String = "投币",
    val favoriteCount: String = "收藏",
    val shareCount: String = "分享",
    val isVertical: Boolean = true
) {
    companion object {
        /**
         * 从推荐流视频项转换为短视频项
         *
         * @param item 推荐流视频项
         * @return 短视频项
         */
        fun fromSmallCoverV2Item(item: SmallCoverV2Item): ShortVideoItem {
            // 判断是否为竖屏视频
            // goto字段为 "vertical_av" 表示竖屏视频
            // 也可以是 "av" 但需要额外判断
            val isVertical = item.goto == "vertical_av" ||
                    item.cardGoto == "vertical_av"

            return ShortVideoItem(
                aid = item.playerArgs.aid,
                cid = item.playerArgs.cid,
                title = item.title,
                cover = item.cover,
                author = item.args.upName,
                authorId = item.args.upId,
                authorAvatar = "",
                duration = item.playerArgs.duration,
                playCount = item.coverLeftText1,
                isVertical = isVertical
            )
        }
    }
}
