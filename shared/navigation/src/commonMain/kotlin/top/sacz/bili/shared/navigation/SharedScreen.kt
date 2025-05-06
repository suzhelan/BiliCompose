package top.sacz.bili.shared.navigation

import cafe.adriel.voyager.core.registry.ScreenProvider

sealed class SharedScreen(val path : String) : ScreenProvider {
    data object Home : SharedScreen("/home")
    data object Login : SharedScreen("/login")
    data class VideoPlayer(val url: String) : SharedScreen("/video")
}