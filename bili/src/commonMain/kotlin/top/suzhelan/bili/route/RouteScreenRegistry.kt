package top.suzhelan.bili.route

import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.VideoPlayerScreen
import top.suzhelan.bili.biz.home.HomeScreen
import top.suzhelan.bili.biz.login.ui.LoginScreen
import top.suzhelan.bili.biz.login.ui.ScanQRCodeScreen
import top.suzhelan.bili.biz.login.ui.WebViewScreen
import top.suzhelan.bili.biz.shorts.ui.ShortVideoScreen
import top.suzhelan.bili.biz.user.ui.FollowListScreen
import top.suzhelan.bili.biz.user.ui.UserProfileScreen
import top.suzhelan.bili.shared.navigation.SharedScreen


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
        VideoPlayerScreen(
            playerParams = PlayerParams(
                aid = param.aid,
                bvid = param.bvid,
                epid = param.epid,
                seasonId = param.seasonId,
                cid = param.cid
            )
        )
    }
    composable<SharedScreen.ShortVideo> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.ShortVideo>()
        ShortVideoScreen(param.aid, param.videoJson)
    }
    composable<SharedScreen.ScanQRCode> {
        ScanQRCodeScreen()
    }
    composable<SharedScreen.WebView> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.WebView>()
        WebViewScreen(param.url)
    }
    composable<SharedScreen.UserProfile> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.UserProfile>()
        UserProfileScreen(param.mid)
    }
}