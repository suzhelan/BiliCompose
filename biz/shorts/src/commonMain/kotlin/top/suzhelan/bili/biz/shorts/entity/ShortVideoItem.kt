package top.suzhelan.bili.biz.shorts.entity

import kotlinx.serialization.Serializable
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item

/**
 * 短视频项实体
 * 封装SmallCoverV2Item用于短视频场景
 */
@Serializable
data class ShortVideoItem(
    val aid: Long,
    val cid: Long,
    val title: String,
    val cover: String,
    val author: String,
    val authorId: Long,
    val duration: Int,
    val playCount: String,
    val danmakuCount: String,
    val isVertical: Boolean = true
) {
    companion object {
        fun fromSmallCoverV2Item(item: SmallCoverV2Item): ShortVideoItem {
            return ShortVideoItem(
                aid = item.playerArgs.aid,
                cid = item.playerArgs.cid,
                title = item.title,
                cover = item.cover,
                author = item.args.upName,
                authorId = item.args.upId,
                duration = item.playerArgs.duration,
                playCount = item.coverLeftText1,
                danmakuCount = item.coverLeftText2,
                isVertical = item.rCmdReasonStyle?.text?.contains("竖屏") == true
            )
        }
    }
}

