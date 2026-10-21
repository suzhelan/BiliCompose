package top.suzhelan.bili.route

import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.NewVerticaScreen
import top.suzhelan.bili.biz.biliplayer.ui.VideoPlayerScreen
import top.suzhelan.bili.biz.home.HomeScreen
import top.suzhelan.bili.biz.login.ui.LoginScreen
import top.suzhelan.bili.biz.login.ui.ScanQRCodeScreen
import top.suzhelan.bili.biz.login.ui.WebViewScreen
import top.suzhelan.bili.biz.search.ui.SEARCH_EDIT_KEY
import top.suzhelan.bili.biz.search.ui.SearchInputScreen
import top.suzhelan.bili.biz.search.ui.SearchResultsScreen
import top.suzhelan.bili.biz.user.ui.FavoriteDetailScreen
import top.suzhelan.bili.biz.user.ui.FavoriteFoldersScreen
import top.suzhelan.bili.biz.user.ui.FollowListScreen
import top.suzhelan.bili.biz.user.ui.MoreLikeVideoScreen
import top.suzhelan.bili.biz.user.ui.NewUserProfileScreen
import top.suzhelan.bili.biz.user.ui.WatchHistoryScreen
import top.suzhelan.bili.biz.user.ui.WatchLaterScreen
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


//路由表收拢所有路由,这样不会显得太臃肿
fun NavGraphBuilder.routingScreenRegistration() {
    composable<SharedScreen.Search> { entry ->
        val editKeyword by entry.savedStateHandle.getStateFlow<String?>(SEARCH_EDIT_KEY, null)
            .collectAsStateWithLifecycle()
        SearchInputScreen(
            editKeyword = editKeyword,
            onEditConsumed = { entry.savedStateHandle[SEARCH_EDIT_KEY] = null })
    }
    composable<SharedScreen.SearchResults> { entry ->
        val keyword = entry.toRoute<SharedScreen.SearchResults>().keyword
        val navigation = LocalNavigation.currentOrThrow
        SearchResultsScreen(keyword = keyword, onEditKeyword = {
            navigation.navigator.previousBackStackEntry?.savedStateHandle?.set(
                SEARCH_EDIT_KEY,
                keyword
            )
            navigation.pop()
        })
    }
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
    composable<SharedScreen.NewVertical> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.NewVertical>()
        NewVerticaScreen(
            intent = PlayerParams(
                aid = param.aid,
                bvid = param.bvid,
                epid = param.epid,
                seasonId = param.seasonId,
                cid = param.cid
            )
        )
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
        val navController = LocalNavigation.currentOrThrow.navigator
        // 在进入时记录来源，避免后续跳转使当前页的返回图标发生变化。
        val showBackButton = remember(backStackEntry) {
            val previousDestination = navController.previousBackStackEntry?.destination
            previousDestination != null && !previousDestination.hasRoute<SharedScreen.Home>()
        }
        NewUserProfileScreen(mid = param.mid, showBackButton = showBackButton)
    }
    composable<SharedScreen.MoreLikeVideos> {  backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.MoreLikeVideos>()
        MoreLikeVideoScreen(
            param.mid
        )
    }
    composable<SharedScreen.WatchHistory> {
        WatchHistoryScreen()
    }
    composable<SharedScreen.WatchLater> {
        WatchLaterScreen()
    }
    composable<SharedScreen.FavoriteFolders> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.FavoriteFolders>()
        FavoriteFoldersScreen(param.mid)
    }
    composable<SharedScreen.FavoriteDetail> { backStackEntry ->
        val param = backStackEntry.toRoute<SharedScreen.FavoriteDetail>()
        FavoriteDetailScreen(param.mediaId)
    }
}
