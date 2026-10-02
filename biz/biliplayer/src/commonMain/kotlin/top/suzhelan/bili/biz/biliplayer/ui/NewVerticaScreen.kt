package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.vertical.ProgressPreview
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalBottomProgressArea
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalBottomReservedHeight
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalVideoOverlay
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.player.ui.VerticalPlayerUI
import top.suzhelan.bili.player.ui.indicator.OnPreviewIndicator
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun NewVerticaScreen(intent: PlayerParams) {
    val viewModel = viewModel { VerticalVideoViewModel() }
    //使用PlayerViewModel
    CommonComposeUI(
        viewModel = viewModel,
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { vm ->
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
        DialogHandler(vm)
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
            val currentPositionMillis by controller.currentPositionMillis.collectAsStateWithLifecycle()
            val totalDurationMillis by controller.totalDurationMillis.collectAsStateWithLifecycle()
            val playbackFinished by controller.playbackFinished.collectAsStateWithLifecycle()
            var hasRestartedAfterFinish by remember(controller) { mutableStateOf(false) }

            LaunchedEffect(item, controller, isActivePage) {
                vm.doPlayer(item, controller)
                vm.updatePagePlayback(controller, isActivePage)
            }

            LaunchedEffect(
                controller,
                isActivePage,
                playbackFinished,
                currentPositionMillis,
                totalDurationMillis,
            ) {
                if (!isActivePage) {
                    return@LaunchedEffect
                }
                val reachedEnd =
                    totalDurationMillis > 0 && currentPositionMillis >= totalDurationMillis
                if ((playbackFinished || reachedEnd) && !hasRestartedAfterFinish) {
                    hasRestartedAfterFinish = true
                    controller.seekTo(0)
                    controller.resume()
                } else if (!playbackFinished && !reachedEnd) {
                    hasRestartedAfterFinish = false
                }
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
        var previewIndicator by remember { mutableStateOf<ProgressPreview?>(null) }

        VerticalPlayerUI(
            controller = controller,
            showProgressIndicator = false
        )
        VerticalVideoOverlay(
            page = page,
            videoPoolData = viewModel.videoPoolData,
            viewModel = viewModel,
            onBack = onBack,
            bottomReservedHeight = VerticalBottomReservedHeight
        )
        VerticalBottomProgressArea(
            controller = controller,
            modifier = Modifier.align(Alignment.BottomCenter),
            onPreview = { positionMillis, totalDurationMillis ->
                previewIndicator = ProgressPreview(positionMillis, totalDurationMillis)
            },
            onPreviewFinished = {
                previewIndicator = null
            }
        )
        previewIndicator?.let { preview ->
            OnPreviewIndicator(
                modifier = Modifier.align(Alignment.Center),
                progress = preview.positionMillis,
                total = preview.totalDurationMillis
            )
        }
    }


