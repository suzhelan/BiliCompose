package top.suzhelan.bili.shared.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class SharedScreen(val path: String) : BiliScreenProvider {
    @Serializable
    data object Home : SharedScreen("/home")

    @Serializable
    data object Login : SharedScreen("/login")

    @Serializable
    data object FollowList : SharedScreen("/followList")

    @Serializable
    data class VideoPlayer(val body: String) : SharedScreen("/video")

    @Serializable
    data class ShortVideo(val aid: Long, val videoJson: String = "") : SharedScreen("/shorts")

    @Serializable
    data object ScanQRCode : SharedScreen("/scanQRCode")

    @Serializable
    data class WebView(val url: String) : SharedScreen("/webView")
}