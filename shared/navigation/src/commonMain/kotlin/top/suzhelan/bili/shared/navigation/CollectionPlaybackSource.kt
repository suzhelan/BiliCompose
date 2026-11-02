package top.suzhelan.bili.shared.navigation

/** 当前播放页的合集入口信息，不跨播放页保存锁定状态。 */
data class CollectionPlaybackSource(
    val mid: Long,
    val id: Long,
    val kind: String,
    val title: String,
    val newestFirst: Boolean,
)
