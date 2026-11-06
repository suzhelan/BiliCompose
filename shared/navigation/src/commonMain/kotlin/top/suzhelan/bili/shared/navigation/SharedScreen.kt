package top.suzhelan.bili.shared.navigation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * 当做是intent就行
 */
@Serializable
sealed class SharedScreen(val path: String) : BiliScreenProvider {
    @Serializable
    data object Home : SharedScreen("/home")

    @Serializable
    data object Search : SharedScreen("/search")

    @Serializable
    data class SearchResults(@SerialName("keyword") val keyword: String) :
        SharedScreen("/search/results")

    @Serializable
    data object Login : SharedScreen("/login")

    @Serializable
    data class FollowList(@SerialName("mid") val mid: Long? = null) : SharedScreen("/followList")

    @Serializable
    data class FansList(@SerialName("mid") val mid: Long? = null) : SharedScreen("/fansList")

    @Serializable
    data class VideoPlayer(
        @SerialName("aid") val aid: Long? = null,
        @SerialName("bvid") val bvid: String? = null,
        @SerialName("epid") val epid: String? = null,
        @SerialName("seasonId") val seasonId: String? = null,
        @SerialName("cid") val cid: Long? = null,
        @SerialName("qn") val qn: Int = 80,
        @SerialName("collectionMid") val collectionMid: Long? = null,
        @SerialName("collectionId") val collectionId: Long? = null,
        @SerialName("collectionKind") val collectionKind: String? = null,
        @SerialName("collectionTitle") val collectionTitle: String = "",
        @SerialName("collectionNewestFirst") val collectionNewestFirst: Boolean = false,
    ) : SharedScreen("/video")

    @Serializable
    data class NewVertical(
        val aid: Long? = null,
        val bvid: String? = null,
        val epid: String? = null,
        val seasonId: String? = null,
        val cid: Long? = null,
        val qn: Int = 80,
    ) : SharedScreen("/vertical")

    @Serializable
    data class ShortVideo(val aid: Long, val videoJson: String = "") : SharedScreen("/shorts")

    @Serializable
    data object ScanQRCode : SharedScreen("/scanQRCode")

    @Serializable
    data class WebView(val url: String) : SharedScreen("/webView")

    @Serializable
    data class UserProfile(val mid: Long) : SharedScreen("/userProfile")

    @Serializable
    data class MoreLikeVideos(val mid: Long) : SharedScreen("/moreLikeVideos")

    @Serializable
    data object WatchHistory : SharedScreen("/watchHistory")

    @Serializable
    data object WatchLater : SharedScreen("/watchLater")

    @Serializable
    data class FavoriteFolders(val mid: Long) : SharedScreen("/favoriteFolders")

    @Serializable
    data class FavoriteDetail(val mediaId: Long) : SharedScreen("/favoriteDetail")

    @Serializable
    data class UserCollection(
        @SerialName("mid") val mid: Long,
        @SerialName("id") val id: Long,
        @SerialName("kind") val kind: String,
        @SerialName("title") val title: String,
    ) : SharedScreen("/userCollection")
}

