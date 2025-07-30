package top.sacz.bili.route

import cafe.adriel.voyager.core.registry.ScreenRegistry
import top.sacz.bili.biz.home.HomeScreen
import top.sacz.bili.biz.login.ui.LoginScreen
import top.sacz.bili.biz.player.ui.VideoPlayerScreen
import top.sacz.bili.biz.user.ui.FollowListScreen
import top.sacz.bili.shared.navigation.SharedScreen

object RouteNavigationConfig {
    //路由表收拢所有路由
    fun routingScreenRegistration() {
        ScreenRegistry {
            register<SharedScreen.Home> {
                HomeScreen
            }
            register<SharedScreen.Login> {
                LoginScreen
            }
            register<SharedScreen.VideoPlayer> {
                VideoPlayerScreen(it.body)
            }
            register<SharedScreen.FollowList> {
                FollowListScreen
            }
        }
    }
}

