package top.suzhelan.bili.biz.user.util

import top.suzhelan.bili.biz.user.entity.FavoriteFolder
import top.suzhelan.bili.shared.navigation.SharedScreen

private val collectionVideoLink = Regex("^bilibili://video/([0-9]+)(?:[/?#].*)?$")

internal fun FavoriteFolder.toOpenScreen(): SharedScreen? {
    if (!canOpen) return null
    if (!isCollection) return SharedScreen.FavoriteDetail(id)

    // 只传 bvid，避免 aid 解析或两种标识不一致影响播放。
    bvid.trim().takeIf { it.isNotEmpty() }?.let {
        return SharedScreen.VideoPlayer(bvid = it)
    }
    val aid = collectionVideoLink.matchEntire(link.trim())?.groupValues?.get(1)
        ?.toLongOrNull()?.takeIf { it > 0 }
        ?: return null
    return SharedScreen.VideoPlayer(aid = aid)
}
