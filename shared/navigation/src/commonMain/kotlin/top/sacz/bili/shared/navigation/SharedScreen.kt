package top.sacz.bili.shared.navigation

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
}