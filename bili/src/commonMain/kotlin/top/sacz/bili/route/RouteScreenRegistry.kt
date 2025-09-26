package top.sacz.bili.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import top.sacz.bili.biz.biliplayer.ui.VideoPlayerScreen
import top.sacz.bili.biz.home.HomeScreen
import top.sacz.bili.biz.login.ui.LoginScreen
import top.sacz.bili.biz.user.ui.FollowListScreen
import top.sacz.bili.shared.navigation.SharedScreen


//路由表收拢所有路由,这样不会显得太臃肿
fun NavGraphBuilder.routingScreenRegistration() {
    composable<SharedScreen.Home> { backStackEntry ->
        HomeScreen()
    }
    composable<SharedScreen.Login> {
        LoginScreen()
    }
    composable<SharedScreen.FollowList> {
        FollowListScreen()
    }
    composable<SharedScreen.VideoPlayer> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.VideoPlayer>()
        VideoPlayerScreen(param.body)
    }
}


