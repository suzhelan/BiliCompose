package top.suzhelan.bili.biz.shorts.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.VerticalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.biz.shorts.ui.component.ShortVideoBottomInfo
import top.suzhelan.bili.biz.shorts.ui.component.ShortVideoPlayer
import top.suzhelan.bili.biz.shorts.ui.component.ShortVideoSideActions
import top.suzhelan.bili.biz.shorts.viewmodel.ShortVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.controller.rememberPlayerSyncController
import top.suzhelan.bili.shared.common.logger.LogUtils
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 短视频播放Screen
 * 纯 UI 层，所有状态由 ViewModel 管理
 *
 * @param initialAid 初始播放的视频aid
 * @param videoJson 视频的JSON序列化字符串
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShortVideoScreen(
    initialAid: Long,
    videoJson: String = "",
    viewModel: ShortVideoViewModel = viewModel { ShortVideoViewModel() }
) {
    val navigator = LocalNavigation.currentOrThrow

    // 从 ViewModel 获取所有状态
    val videoList by viewModel.videoList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val currentPlayingIndex by viewModel.currentPlayingIndex.collectAsStateWithLifecycle()
    val sessionId by viewModel.sessionId.collectAsStateWithLifecycle()

    // 解析初始视频
    val initialVideo = remember(videoJson) {
        if (videoJson.isNotEmpty()) {
            try {
                kotlinx.serialization.json.Json.decodeFromString<SmallCoverV2Item>(videoJson)
            } catch (e: Exception) {
                LogUtils.e("解析初始视频失败", e)
                null
            }
        } else null
    }

    // 仅在进入时初始化会话 - 由 ViewModel 管理所有状态
    LaunchedEffect(initialAid, videoJson) {
        viewModel.initSession(initialAid, initialVideo)
    }

    // 错误提示
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // 可以在这里显示 Toast 或 Snackbar
            viewModel.clearError()
        }
    }

    DialogHandler(viewModel)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            videoList.isEmpty() && isLoading -> {
                // 加载状态
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            videoList.isEmpty() && !isLoading -> {
                // 空状态
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "暂无竖屏视频",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Button(onClick = { viewModel.loadMoreVideos() }) {
                            Text("重新加载")
                        }
                    }
                }
            }

            else -> {
                // 视频列表 - 使用 sessionId 作为 key 强制重新创建
                key(sessionId) {
                    ShortVideoVerticalPager(
                        videos = videoList,
                        initialPage = currentPlayingIndex,
                        onPageChanged = viewModel::updatePlayingIndex,
                        onLoadMore = viewModel::loadMoreVideos
                    )
                }
            }
        }

        // 返回按钮
        IconButton(
            onClick = { navigator.pop() },
            modifier = Modifier
                .padding(16.dp)
                .align(Alignment.TopStart)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = "返回",
                tint = Color.White
            )
        }
    }
}

/**
 * 竖向视频Pager组件 - 纯 UI，无状态管理
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortVideoVerticalPager(
    videos: List<ShortVideoItem>,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
    onLoadMore: () -> Unit
) {
    if (videos.isEmpty()) return

    val safeInitialPage = initialPage.coerceIn(0, videos.size - 1)

    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { videos.size }
    )

    // 播放器控制器映射 - 只存储引用，不在这里创建
    val playerControllers = remember { mutableMapOf<Long, PlayerSyncController>() }

    // 监听页面变化
    LaunchedEffect(pagerState.settledPage) {
        if (pagerState.settledPage in videos.indices) {
            onPageChanged(pagerState.settledPage)

            // 接近末尾时加载更多
            if (pagerState.settledPage >= videos.size - 3) {
                onLoadMore()
            }
        }
    }

    // 清理不再需要的播放器
    DisposableEffect(videos) {
        onDispose {
            playerControllers.values.forEach { it.close() }
            playerControllers.clear()
        }
    }

    VerticalPager(
        state = pagerState,
        modifier = Modifier.fillMaxSize(),
        beyondViewportPageCount = 1,
        key = { index ->
            if (index in videos.indices) videos[index].aid
            else -1L * (index + 1)
        }
    ) { page ->
        if (page !in videos.indices) {
            Box(modifier = Modifier.fillMaxSize().background(Color.Black))
            return@VerticalPager
        }

        val video = videos[page]

        // 修复：使用 settledPage 来判断是否为当前页，并且考虑初始页面
        // 在 pagerState 未稳定之前，使用 initialPage 判断
        val isCurrentPage = if (pagerState.settledPage == pagerState.currentPage) {
            // 已稳定，使用 settledPage
            pagerState.settledPage == page
        } else {
            // 正在滚动或初始化中，使用 currentPage
            pagerState.currentPage == page
        }

        // 为每个页面创建独立的播放器控制器
        val playerController = rememberPlayerSyncController()

        // 将控制器添加到映射中以便清理
        DisposableEffect(video.aid) {
            playerControllers[video.aid] = playerController
            onDispose {
                playerControllers.remove(video.aid)
                playerController.close()
            }
        }

        Box(modifier = Modifier.fillMaxSize()) {
            ShortVideoPlayer(
                video = video,
                isCurrentPage = isCurrentPage,
                modifier = Modifier.fillMaxSize(),
                playerController = playerController
            )

            ShortVideoBottomInfo(
                video = video,
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(start = 16.dp, bottom = 80.dp)
                    .fillMaxWidth(0.7f)
            )

            ShortVideoSideActions(
                video = video,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(end = 12.dp, bottom = 80.dp)
            )
        }
    }
}
