package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Button
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.biliplayer.entity.PlayerArgsItem
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.vertical.ProgressPreview
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalBottomProgressArea
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalBottomReservedHeight
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalTopBar
import top.suzhelan.bili.biz.biliplayer.ui.vertical.VerticalVideoOverlay
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.player.ui.VerticalPlayerUI
import top.suzhelan.bili.player.ui.indicator.OnPreviewIndicator
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun NewVerticaScreen(intent: PlayerParams) {
    val viewModel = viewModel { VerticalVideoViewModel() }
    //使用PlayerViewModel
    AppScaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val context = BiliLocalContext.current
        val navigation = LocalNavigation.currentOrThrow
        val videoUrlList by viewModel.videoUrlList.collectAsStateWithLifecycle()
        LaunchedEffect(Unit) {
            //初始化数据
            viewModel.initData(intent)
        }
        //初始状态加载中
        if (videoUrlList.isEmpty()) {
            LoadingIndicator(text = "加载中...")
            return@AppScaffold
        }
        DialogHandler(viewModel)
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
            viewModel.updateActivePage(activePage)
        }
        LaunchedEffect(activePage) {
            activePage?.let { viewModel.onUpdate(it) }
        }

        VerticalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            beyondViewportPageCount = 1,
            key = { page -> page }
        ) { page ->
            val item = videoUrlList[page]
            // 错误展示只替换当前页内容，VerticalPager 始终保留并接收上下滑动。
            if (item !is BiliResponse.Success) {
                VideoLoadStateItem(
                    state = item,
                    onRetry = { viewModel.retryVideo(page) },
                    onBack = { navigation.pop() }
                )
                return@VerticalPager
            }
            val controller = remember(page, context) {
                viewModel.getController(page, context)
            }
            val isActivePage = activePage == page
            val currentPositionMillis by controller.currentPositionMillis.collectAsStateWithLifecycle()
            val totalDurationMillis by controller.totalDurationMillis.collectAsStateWithLifecycle()
            val playbackFinished by controller.playbackFinished.collectAsStateWithLifecycle()
            var hasRestartedAfterFinish by remember(controller) { mutableStateOf(false) }

            LaunchedEffect(item, controller, isActivePage) {
                viewModel.doPlayer(item.data, controller)
                viewModel.updatePagePlayback(controller, isActivePage)
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
                viewModel = viewModel,
                onBack = { navigation.pop() }
            )
        }
    }
}

@Composable
private fun VideoLoadStateItem(
    state: BiliResponse<PlayerArgsItem>,
    onRetry: () -> Unit,
    onBack: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        if (state is BiliResponse.Error) {
            Column(
                modifier = Modifier.align(Alignment.Center).padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (state.code == -404) "视频信息不存在或暂不可用"
                    else "视频加载失败：${state.msg}",
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                Button(onClick = onRetry) { Text("重试") }
                Text("上下滑动可切换其他视频", color = Color.White)
            }
        } else {
            CompositionLocalProvider(LocalContentColor provides Color.White) {
                LoadingIndicator(text = "获取视频信息中...")
            }
        }
        VerticalTopBar(
            onlineCountText = "",
            onBack = onBack,
            modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding()
        )
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


