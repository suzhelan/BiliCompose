package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.longOrNull

enum class SubmissionOrder(val parameter: String, val label: String) {
    NEWEST("pubdate", "最新发布"), MOST_PLAYED("click", "最多播放"), MOST_FAVORITED(
        "stow",
        "最多收藏"
    )
}

@Serializable
data class SubmissionCategory(
    @SerialName("tid") val tid: Int,
    @SerialName("name") val name: String = "",
    @SerialName("count") val count: Int = 0,
)

@Serializable
data class SubmissionVideo(
    @SerialName("aid") val aid: Long = 0,
    @SerialName("bvid") val bvid: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("pic") val cover: String = "",
    @SerialName("author") val author: String = "",
    @SerialName("created") val created: Long = 0,
    @SerialName("length") val length: String = "",
    @SerialName("duration") val duration: Int = 0,
    @SerialName("playback_position") val playbackPosition: Int? = null,
    @SerialName("video_review") val danmaku: JsonPrimitive? = null,
    @SerialName("comment") val comments: JsonPrimitive? = null,
    @SerialName("cursor_attr") val cursorAttr: SubmissionCursor? = null,
    // 播放量在部分稿件上会返回 "--"，不能按整数强制反序列化。
    @SerialName("play") val play: JsonPrimitive? = null,
    @SerialName("hide_click") val hideClick: Boolean = false,
    @SerialName("is_union_video") val isUnionVideo: Int = 0,
    @SerialName("is_live_playback") val isLivePlayback: Int = 0,
    @SerialName("is_charging_arc") val isChargingArc: Boolean = false,
    @SerialName("is_lesson_video") val isLessonVideo: Int = 0,
    @SerialName("jump_url") val jumpUrl: String = "",
    @SerialName("state") val state: Int = 0,
    @SerialName("ugc_pay") val ugcPay: Int = 0,
) {
    val key: String get() = if (aid > 0) "av$aid" else bvid.ifBlank { "missing:$title:$created" }
    val canPlay: Boolean get() = state == 0 && (aid > 0 || bvid.isNotBlank())
    val playCount: Long? get() = if (hideClick) null else play?.longOrNull?.takeIf { it >= 0 }
    val playLabel: String? get() = if (hideClick) null else play?.contentOrNull
    val durationSeconds: Int get() = duration.takeIf { it > 0 } ?: parseSubmissionDuration(length)
}

@Serializable
data class SubmissionCursor(
    @SerialName("is_last_watched_arc") val isLastWatched: Boolean = false,
)

fun parseSubmissionDuration(length: String): Int {
    val parts = length.trim().split(':')
    if (parts.size !in 2..3) return 0
    var seconds = 0L
    parts.forEachIndexed { index, part ->
        val value = part.toLongOrNull() ?: return 0
        if (value !in 0..Int.MAX_VALUE.toLong() || (index > 0 && value >= 60)) return 0
        if (seconds > (Int.MAX_VALUE - value) / 60) return 0
        seconds = seconds * 60 + value
    }
    return seconds.toInt()
}

@Serializable
data class SubmissionData(
    @SerialName("list") val list: SubmissionList,
    @SerialName("page") val page: SubmissionPageInfo,
)

@Serializable
data class SubmissionList(
    @SerialName("vlist") val videos: List<SubmissionVideo>?,
    // 无投稿时有些响应使用 [] 或 null，而不是分区字典。
    @SerialName("tlist") val categories: JsonElement? = null,
)

@Serializable
data class SubmissionPageInfo(
    @SerialName("count") val count: Int,
    @SerialName("pn") val number: Int = 1,
    @SerialName("ps") val size: Int = 30,
)

enum class UserCollectionKind(val label: String) { SEASON("合集"), SERIES("系列") }

data class UserCollection(
    val id: Long,
    val kind: UserCollectionKind,
    val name: String,
    val cover: String,
    val count: Int,
    val description: String,
) {
    val key: String get() = "${kind.name}-$id"
}

@Serializable
data class CollectionEntry(
    @SerialName("meta") val meta: CollectionMeta,
    @SerialName("archives") val archives: List<CollectionArchive> = emptyList(),
)

@Serializable
data class CollectionMeta(
    @SerialName("season_id") val seasonId: Long = 0,
    @SerialName("series_id") val seriesId: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("total") val total: Int = 0,
    @SerialName("description") val description: String = "",
)

@Serializable
data class CollectionArchive(
    @SerialName("aid") val aid: Long = 0,
    @SerialName("bvid") val bvid: String = "",
    @SerialName("title") val title: String = "",
    @SerialName("pic") val cover: String = "",
    @SerialName("duration") val duration: Int = 0,
    @SerialName("pubdate") val pubdate: Long = 0,
    @SerialName("stat") val stat: CollectionArchiveStat = CollectionArchiveStat(),
    @SerialName("state") val state: Int = 0,
    @SerialName("ugc_pay") val ugcPay: Int = 0,
    @SerialName("playback_position") val playbackPosition: Int? = null,
    @SerialName("cursor_attr") val cursorAttr: SubmissionCursor? = null,
) {
    fun toVideo() = SubmissionVideo(
        aid = aid, bvid = bvid, title = title, cover = cover, created = pubdate,
        length = "${duration.coerceAtLeast(0) / 60}:${
            (duration.coerceAtLeast(0) % 60).toString().padStart(2, '0')
        }",
        play = stat.view, state = state, ugcPay = ugcPay, duration = duration,
        playbackPosition = playbackPosition, cursorAttr = cursorAttr,
        danmaku = stat.danmaku, comments = stat.reply,
    )
}

@Serializable
data class CollectionArchiveStat(
    @SerialName("view") val view: JsonPrimitive? = null,
    @SerialName("danmaku") val danmaku: JsonPrimitive? = null,
    @SerialName("reply") val reply: JsonPrimitive? = null,
)

data class ProfilePage<T>(
    val items: List<T>,
    val hasMore: Boolean,
    val total: Int,
    val categories: List<SubmissionCategory> = emptyList(),
    val notice: String? = null,
)

fun String.secureImageUrl(): String = when {
    startsWith("//") -> "https:$this"
    startsWith("http://") -> replaceFirst("http://", "https://")
    else -> this
}
