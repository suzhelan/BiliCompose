package top.suzhelan.bili.biz.biliplayer.entity

import top.suzhelan.bili.biz.biliplayer.ui.dialog.CollectionSortOrder

data class CollectionPlayback(
    val collection: VideoCollection? = null,
    val locked: Boolean = false,
    val sortOrder: CollectionSortOrder = CollectionSortOrder.Default,
    val loading: Boolean = false,
    val error: String? = null,
) {
    val episodes: List<VideoCollection.Episode>
        get() = collection?.sections.orEmpty().flatMap { section ->
            sortOrder.sortEpisodes(section.episodes).map { it.value }
        }

    fun nextEpisode(aid: Long): VideoCollection.Episode? {
        val ordered = episodes
        val index = ordered.indexOfFirst { it.aid == aid }
        return if (index >= 0) ordered.getOrNull(index + 1) else null
    }
}

data class NextPlayback(
    val params: PlayerParams,
    val title: String,
    val cover: String,
    val seconds: Int = 3,
)

/** 先走完当前稿件的分 P，再查找排序后的下一条稿件；末尾不循环。 */
fun CollectionPlayback.nextPlayback(video: VideoInfo, current: PlayerParams): NextPlayback? {
    if (!locked) return null
    val partIndex = video.pages.indexOfFirst { it.cid == current.cid }
    val nextPart = if (partIndex >= 0) video.pages.getOrNull(partIndex + 1) else null
    if (nextPart != null) {
        return NextPlayback(
            params = current.copy(cid = nextPart.cid),
            title = "${video.title} · ${nextPart.part}",
            cover = video.pic,
        )
    }
    val next = nextEpisode(video.aid) ?: return null
    return NextPlayback(
        params = PlayerParams(
            aid = next.aid.takeIf { it > 0 },
            bvid = next.bvid.takeIf { it.isNotBlank() },
            qn = current.qn,
        ),
        title = next.title,
        cover = next.arc?.pic.orEmpty(),
    )
}
