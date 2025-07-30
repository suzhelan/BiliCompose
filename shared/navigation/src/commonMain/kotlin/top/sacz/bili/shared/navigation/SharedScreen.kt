package top.sacz.bili.shared.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen(val path : String) : ScreenProvider {
    data object Home : SharedScreen("/home")
    data object Login : SharedScreen("/login")
    data object FollowList : SharedScreen("/followList")
    data class VideoPlayer(val body: String) : SharedScreen("/video")
}