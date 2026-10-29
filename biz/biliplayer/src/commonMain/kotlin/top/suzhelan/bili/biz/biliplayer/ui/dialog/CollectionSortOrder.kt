package top.suzhelan.bili.biz.biliplayer.ui.dialog

import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection

enum class CollectionSortOrder(val label: String) {
    Default("从早到晚"),
    Newest("最新发布"),
    MostPlayed("最多播放");

    /** 保留原始索引，排序后列表项的 key 和观看进度仍对应同一视频。 */
    fun sortEpisodes(episodes: List<VideoCollection.Episode>): List<IndexedValue<VideoCollection.Episode>> {
        val indexed = episodes.withIndex().toList()
        return when (this) {
            Default -> indexed.sortedWith(compareBy(nullsLast<Long>()) {
                it.value.arc?.pubdate?.takeIf { timestamp -> timestamp > 0 }
            })

            Newest -> indexed.sortedWith(compareByDescending {
                it.value.arc?.pubdate?.takeIf { timestamp -> timestamp > 0 }
            })

            MostPlayed -> indexed.sortedWith(compareByDescending {
                it.value.arc?.stat?.view?.takeIf { views -> views >= 0 }
            })
        }
    }
}
