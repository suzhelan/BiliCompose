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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.serialization.json.Json.Default.decodeFromString
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
 *
 * 提供类似抖音的竖屏视频流播放体验
 * 采用MVVM架构，纯UI层不包含业务逻辑，所有状态由ViewModel管理
 *
 * @param initialAid 初始播放的视频aid
 * @param videoJson 初始视频的JSON序列化字符串（可选）
 * @param viewModel 视频ViewModel
 */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ShortVideoScreen(
    initialAid: Long,
    videoJson: String = "",
    viewModel: ShortVideoViewModel = viewModel { ShortVideoViewModel() }
) {
    val navigator = LocalNavigation.currentOrThrow

    // 从ViewModel获取所有状态
    val videoList by viewModel.videoList.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()
    val errorMessage by viewModel.errorMessage.collectAsStateWithLifecycle()
    val currentPlayingIndex by viewModel.currentPlayingIndex.collectAsStateWithLifecycle()
    val sessionId by viewModel.sessionId.collectAsStateWithLifecycle()

    // 解析初始视频
    val initialVideo = remember(videoJson) {
        if (videoJson.isNotEmpty()) {
            try {
                decodeFromString<SmallCoverV2Item>(videoJson)
            } catch (e: Exception) {
                LogUtils.e("ShortVideoScreen: 解析初始视频失败", e)
                null
            }
        } else null
    }

    // 初始化会话 - 只在进入时执行一次
    LaunchedEffect(initialAid, videoJson) {
        viewModel.initSession(initialAid, initialVideo)
    }

    // 错误提示处理
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            // 自动清除错误信息
            viewModel.clearError()
        }
    }

    // 对话框处理
    DialogHandler(viewModel)

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            videoList.isEmpty() && isLoading -> {
                // 加载状态
                LoadingContent()
            }

            videoList.isEmpty() && !isLoading -> {
                // 空状态
                EmptyContent(onRetry = viewModel::loadMoreVideos)
            }

            else -> {
                // 视频列表 - 使用sessionId作为key强制重新创建
                key(sessionId) {
                    ShortVideoVerticalPager(
                        videos = videoList,
                        initialPage = currentPlayingIndex,
                        onPageChanged = viewModel::updatePlayingIndex,
                        onLoadMore = viewModel::loadMoreVideos,
                        viewModel = viewModel
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
 * 加载状态内容
 */
@Composable
private fun LoadingContent() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

/**
 * 空状态内容
 *
 * @param onRetry 重试回调
 */
@Composable
private fun EmptyContent(onRetry: () -> Unit) {
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
            Button(onClick = onRetry) {
                Text("重新加载")
            }
        }
    }
}

/**
 * 竖向视频Pager组件
 *
 * 负责视频列表的垂直滚动和页面管理
 * 纯UI组件，不包含业务逻辑
 *
 * @param videos 视频列表
 * @param initialPage 初始页码
 * @param onPageChanged 页面变化回调
 * @param onLoadMore 加载更多回调
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ShortVideoVerticalPager(
    videos: List<ShortVideoItem>,
    initialPage: Int,
    onPageChanged: (Int) -> Unit,
    onLoadMore: () -> Unit,
    viewModel: ShortVideoViewModel
) {
    if (videos.isEmpty()) return

    val safeInitialPage = initialPage.coerceIn(0, videos.size - 1)

    val pagerState = rememberPagerState(
        initialPage = safeInitialPage,
        pageCount = { videos.size }
    )

    // 播放器控制器映射 - 统一管理所有播放器实例
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

    // 清理所有播放器
    DisposableEffect(Unit) {
        onDispose {
            LogUtils.d("ShortVideoScreen: 清理所有播放器，共 ${playerControllers.size} 个")
            playerControllers.values.forEach { it.close() }
            playerControllers.clear()
        }
    }

    // 清理不在视口范围内的播放器
    LaunchedEffect(pagerState.settledPage, videos.size) {
        if (playerControllers.size > 5) {
            val currentPage = pagerState.settledPage
            val keysToRemove = playerControllers.keys.filter { aid ->
                val index = videos.indexOfFirst { it.aid == aid }
                index != -1 && (index < currentPage - 2 || index > currentPage + 2)
            }

            keysToRemove.forEach { aid ->
                LogUtils.d("ShortVideoScreen: 清理远离视口的播放器 - aid=$aid")
                playerControllers[aid]?.close()
                playerControllers.remove(aid)
            }
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

        // 判断是否为当前页
        val isCurrentPage = if (pagerState.settledPage == pagerState.currentPage) {
            pagerState.settledPage == page
        } else {
            pagerState.currentPage == page
        }

        val playerController = rememberPlayerSyncController()

        // 将控制器添加到映射中进行统一管理
        DisposableEffect(video.aid) {
            if (!playerControllers.containsKey(video.aid)) {
                playerControllers[video.aid] = playerController
                LogUtils.d("ShortVideoScreen: 注册播放器实例 - aid=${video.aid}, title=${video.title}")
            }

            onDispose {
                // 不在这里关闭播放器，避免重复关闭
                // 播放器由上面的 LaunchedEffect 或最终的 DisposableEffect 统一清理
            }
        }

        ShortVideoPage(
            video = video,
            isCurrentPage = isCurrentPage,
            playerController = playerController,
            viewModel = viewModel
        )
    }
}

/**
 * 单个短视频页面
 *
 * 组合播放器、信息和操作按钮
 *
 * @param video 视频数据
 * @param isCurrentPage 是否为当前页
 * @param playerController 播放器控制器
 */
@Composable
private fun ShortVideoPage(
    video: ShortVideoItem,
    isCurrentPage: Boolean,
    playerController: PlayerSyncController,
    viewModel: ShortVideoViewModel
) {
    // 查询关注状态
    var followState by remember(video.authorId) { mutableStateOf(0) }

    LaunchedEffect(video.authorId) {
        val state = viewModel.queryFollowState(video.authorId)
        followState = state ?: 0
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // 播放器
        ShortVideoPlayer(
            video = video,
            isCurrentPage = isCurrentPage,
            modifier = Modifier.fillMaxSize(),
            playerController = playerController
        )

        // 底部信息
        ShortVideoBottomInfo(
            video = video,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 80.dp)
                .fillMaxWidth(0.7f)
        )

        // 侧边操作栏
        ShortVideoSideActions(
            video = video,
            followState = followState,
            onClickFollow = { authorId, currentState ->
                viewModel.toggleFollow(authorId, currentState)
                // 乐观更新UI
                followState = if (currentState == 0) 2 else 0
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 12.dp, bottom = 80.dp)
        )
    }
}
