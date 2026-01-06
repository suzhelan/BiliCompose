package top.suzhelan.bili.shared.navigation

import kotlinx.serialization.Serializable

/**
 * 当做是intent就行
 */
@Serializable
sealed class SharedScreen(val path: String) : BiliScreenProvider {
    @Serializable
    data object Home : SharedScreen("/home")

    @Serializable
    data object Login : SharedScreen("/login")

    @Serializable
    data object FollowList : SharedScreen("/followList")

    @Serializable
    data class VideoPlayer(
        val aid: Long? = null,
        val bvid: String? = null,
        val epid: String? = null,
        val seasonId: String? = null,
        val cid: Long? = null,
        val qn: Int = 80
    ) : SharedScreen("/video")

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
}

