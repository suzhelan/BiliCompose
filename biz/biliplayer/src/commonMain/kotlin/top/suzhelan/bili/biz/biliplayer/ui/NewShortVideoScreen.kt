package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.controller.rememberPlayerSyncController
import top.suzhelan.bili.player.ui.VerticalPlayerUI
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator


@Composable
fun NewVerticaScreen(intent: PlayerParams) {
    //使用PlayerViewModel
    CommonComposeUI(viewModel = VerticalVideoViewModel()) { vm ->
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
        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize()
        ) { page ->
            val item = videoUrlList[page]
            val controller = rememberPlayerSyncController()
            LaunchedEffect(Unit) {
                vm.controllerList.add(controller)
                vm.doPlayer(item, controller)
            }
            VideoContentItem(item = item, controller = controller)
        }
    }
}

@Composable
private fun PagerScope.VideoContentItem(item: PlayerArgsItem, controller: PlayerSyncController) =
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        VerticalPlayerUI(
            controller = controller
        )
    }