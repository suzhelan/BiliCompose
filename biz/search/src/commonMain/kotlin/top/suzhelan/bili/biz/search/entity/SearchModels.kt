package top.suzhelan.bili.biz.search.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SearchPage<T>(
    @SerialName("page") val page: Int,
    @SerialName("numPages") val pages: Int,
    @SerialName("numResults") val count: Int,
    @SerialName("pagesize") val pageSize: Int = 20,
    @SerialName("result") val result: List<T>? = null,
)

@Serializable
data class SearchVideo(
    @SerialName("aid") val aid: Long,
    @SerialName("bvid") val bvid: String = "",
    @SerialName("title") val title: String,
    @SerialName("pic") val cover: String = "",
    @SerialName("author") val author: String = "",
    @SerialName("mid") val mid: Long = 0,
    @SerialName("play") val plays: Long = 0,
    @SerialName("video_review") val danmaku: Long = 0,
    @SerialName("pubdate") val publishedAt: Long = 0,
    @SerialName("duration") val duration: String = "",
)

@Serializable
data class SearchUser(
    @SerialName("mid") val mid: Long,
    @SerialName("uname") val name: String,
    @SerialName("upic") val avatar: String = "",
    @SerialName("usign") val signature: String = "",
    @SerialName("fans") val fans: Long = 0,
    @SerialName("videos") val videos: Long = 0,
)

@Serializable
data class SuggestionResult(@SerialName("tag") val tags: List<SearchSuggestion> = emptyList())

@Serializable
data class SearchSuggestion(
    @SerialName("value") val value: String,
    @SerialName("name") val name: String = "",
)

@Serializable
data class TrendingData(@SerialName("trending") val trending: TrendingList)

@Serializable
data class TrendingList(@SerialName("list") val list: List<TrendingWord>)

@Serializable
data class TrendingWord(
    @SerialName("keyword") val keyword: String,
    @SerialName("show_name") val displayName: String = "",
)

enum class SearchCategory { Video, User }

enum class VideoOrder(val parameter: String) {
    Relevance("totalrank"), Plays("click"), Newest("pubdate"), Danmaku("dm"), Favorites("stow")
}

enum class VideoDuration(val parameter: Int) {
    All(0), UnderTen(1), TenToThirty(2), ThirtyToSixty(3), OverSixty(4)
}

enum class UserOrder(val parameter: String) { Relevance("0"), Fans("fans") }

data class VideoSearchSpec(
    val keyword: String,
    val order: VideoOrder = VideoOrder.Relevance,
    val duration: VideoDuration = VideoDuration.All,
)

data class UserSearchSpec(val keyword: String, val order: UserOrder = UserOrder.Relevance)
