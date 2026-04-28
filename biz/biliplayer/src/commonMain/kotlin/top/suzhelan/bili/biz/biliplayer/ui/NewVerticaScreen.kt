package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalVideoOverlay
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.player.ui.VerticalPlayerUI
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun NewVerticaScreen(intent: PlayerParams) {
    //使用PlayerViewModel
    CommonComposeUI(viewModel = VerticalVideoViewModel()) { vm ->
        val context = BiliLocalContext.current
        val navigation = LocalNavigation.currentOrThrow
        val videoUrlList by vm.videoUrlList.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            //初始化数据
            vm.initData(intent)
        }
        //初始状态加载中
        if (videoUrlList.isEmpty()) {
            LoadingIndicator(text = "加载中...")
            return@CommonComposeUI
        }
        val pagerState = rememberPagerState(
            initialPage = 0,
            pageCount = {
                videoUrlList.size
            }
        )

        val activePage by remember(pagerState, videoUrlList.size) {
            derivedStateOf {
                if (pagerState.isScrollInProgress) {
                    null
                } else {
                    pagerState.settledPage.takeIf { it in videoUrlList.indices }
                }
            }
        }

        LaunchedEffect(activePage, videoUrlList.size) {
            vm.updateActivePage(activePage)
        }

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            key = { page -> page }
        ) { page ->
            val item = videoUrlList[page]
            val controller = remember(page, context) {
                vm.getController(page, context)
            }
            val isActivePage = activePage == page

            LaunchedEffect(item, controller, isActivePage) {
                vm.doPlayer(item, controller)
                vm.updatePagePlayback(controller, isActivePage)
            }

            VideoContentItem(
                page = page,
                controller = controller,
                viewModel = vm,
                onBack = { navigation.pop() }
            )
        }
    }
}

@Composable
private fun PagerScope.VideoContentItem(
    page: Int,
    controller: PlayerSyncController,
    viewModel: VerticalVideoViewModel,
    onBack: () -> Unit,
) =
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalPlayerUI(
            controller = controller
        )
        VerticalVideoOverlay(
            page = page,
            videoPoolData = viewModel.videoPoolData,
            viewModel = viewModel,
            onBack = onBack
        )
    }
