package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.MoreHoriz
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.ThumbUpOffAlt
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.biz.user.viewmodel.MoreLikeViewModel
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun MoreLikeVideoScreen(
    mid: Long,
) {
    val navigator = LocalNavigation.currentOrThrow
    key(mid) {
        val viewModel = viewModel { MoreLikeViewModel() }
        AppScaffold(
            topBar = {
                TitleUI("最近点赞") {
                    navigator.pop()
                }
            },
        ) {
            MoreLikeVideoContent(mid, viewModel)
        }
    }
}

/**
 * 更多点赞的视频
 */
@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun MoreLikeVideoContent(
    mid: Long,
    viewModel: MoreLikeViewModel,
    listState: LazyListState = rememberLazyListState(),
    isActive: Boolean = true,
    refreshOnResume: Boolean = true,
    enablePullToRefresh: Boolean = true,
    showRefreshIndicator: Boolean = !enablePullToRefresh,
    onRefreshingChanged: (Boolean) -> Unit = {},
) {
    // 进入组合时记录缓存状态，首次加载完成后的重组不额外发起刷新。
    val hadCachedData = remember(viewModel, mid) { viewModel.hasLoadedData(mid) }
    var hasProcessedVisibility by remember(viewModel, mid) { mutableStateOf(false) }
    var hasResumed by remember(viewModel, mid) { mutableStateOf(false) }
    val currentlyActive by rememberUpdatedState(isActive)
    val lazyPagingItems = viewModel.getMoreLikeVideoFlow(mid).collectAsLazyPagingItems()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading
    val reportRefreshing by rememberUpdatedState(onRefreshingChanged)
    val scope = rememberCoroutineScope()
    LifecycleResumeEffect(viewModel, mid, refreshOnResume) {
        if (hasResumed && currentlyActive) {
            scope.launch { listState.scrollToItem(0) }
            if (refreshOnResume) viewModel.refresh(mid)
        }
        hasResumed = true
        onPauseOrDispose { }
    }
    LaunchedEffect(viewModel, mid, isActive) {
        if (isActive) {
            listState.scrollToItem(0)
            if (hasProcessedVisibility || hadCachedData) viewModel.refresh(mid)
        }
        hasProcessedVisibility = true
    }
    LaunchedEffect(isActive, isRefreshing) {
        reportRefreshing(isActive && isRefreshing)
    }
    DisposableEffect(viewModel) {
        onDispose { reportRefreshing(false) }
    }
    val isActionBusy by viewModel.isActionBusy.collectAsStateWithLifecycle()
    var selectedVideo by remember(mid) { mutableStateOf<LikeVideo?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(viewModel) {
        viewModel.messages.collectLatest { message ->
            selectedVideo = null
            snackbarHostState.showSnackbar(message)
        }
    }
    selectedVideo?.let { video ->
        LikeVideoBottomDialog(
            video = video,
            isActionBusy = isActionBusy,
            onDismissRequest = { selectedVideo = null },
            onCancelLike = {
                viewModel.cancelLike(mid, video)
            },
            onAddToWatchLater = {
                viewModel.addToWatchLater(video)
            },
        )
    }
    val content: @Composable BoxScope.() -> Unit = {
        LikeVideoList(lazyPagingItems, listState, isActionBusy, showRefreshIndicator) {
            selectedVideo = it
        }
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
    if (enablePullToRefresh) {
        PullToRefreshBox(
            isRefreshing = isRefreshing,
            onRefresh = { viewModel.refresh(mid) },
            modifier = Modifier.fillMaxSize(),
            content = content,
        )
    } else {
        // 资料页由整页的 PullToRefreshBox 处理，避免两个刷新容器抢同一手势。
        Box(Modifier.fillMaxSize(), content = content)
    }
}

@Composable
private fun LikeVideoList(
    lazyPagingItems: LazyPagingItems<LikeVideo>,
    listState: LazyListState,
    isActionBusy: Boolean,
    showRefreshIndicator: Boolean,
    onMoreClick: (LikeVideo) -> Unit,
) {
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
    ) {
        when (val refresh = lazyPagingItems.loadState.refresh) {
            is LoadState.Loading -> if (showRefreshIndicator || lazyPagingItems.itemCount == 0) {
                item {
                    if (lazyPagingItems.itemCount == 0) {
                        LoadingIndicator()
                    } else {
                        LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            is LoadState.Error -> item {
                Text(
                    text = "刷新失败：${refresh.error.message}，点击重试",
                    color = TipColor,
                    modifier = Modifier.fillMaxWidth().clickable { lazyPagingItems.retry() }
                        .padding(16.dp),
                )
            }

            else -> Unit
        }
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey {
                it.param
            }
        ) { index ->
            val likeVideo = lazyPagingItems[index] ?: return@items
            LikeVideoItem(likeVideo, moreEnabled = !isActionBusy) { onMoreClick(likeVideo) }
        }
        when (val append = lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingIndicator()
                }
            }

            is LoadState.NotLoading -> {
                if (append.endOfPaginationReached) {
                    item {
                        Text(
                            text = "没有更多了",
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            textAlign = TextAlign.Center,
                            color = TipColor
                        )
                    }
                }
            }

            is LoadState.Error -> {
                item {
                    Text(
                        text = "加载更多失败，点击重试",
                        color = TipColor,
                        modifier = Modifier.fillMaxWidth().clickable { lazyPagingItems.retry() }
                            .padding(16.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun LikeVideoItem(video: LikeVideo, moreEnabled: Boolean, onMoreClick: () -> Unit) {
    val navigator = LocalNavigation.currentOrThrow

    // 首先就是一个圆角卡片背景
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
            .clickable(enabled = video.state) {
                // 跳转到视频播放页面
                val aid = video.param.toLongOrNull()?.takeIf { it > 0 } ?: return@clickable
                navigator.push(SharedScreen.VideoPlayer(aid = aid))
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左部分 封面
            Box(modifier = Modifier.width(140.dp).fillMaxHeight()) {
                // 封面图像
                AsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    model = "${video.cover}@10q.webp",
                    contentDescription = video.title,
                )
                // 用来做底部渐黑的效果
                Box(
                    modifier = Modifier.fillMaxWidth().height(30.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.4f)
                            )
                        )
                )
                // 视频时长位于最右侧,底部
                Text(
                    text = TimeUtils.formatSecondToTime(video.duration),
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier.align(Alignment.BottomEnd).padding(
                        end = 4.dp,
                        bottom = 4.dp
                    )
                )
            }
            //右部分
            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
                    .padding(4.dp)
            ) {
                //标题
                Text(
                    text = video.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                //空隙填充
                Spacer(modifier = Modifier.weight(1f))
                //视频发布时间
                Text(
                    text = TimeUtils.formatTimeAgo(video.ctime.toLong()),
                    color = TipColor,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    //水平对齐
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 播放数
                    Icon(
                        imageVector = Icons.Outlined.SmartDisplay,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = TipColor
                    )
                    Text(
                        text = video.viewContent,
                        fontSize = 11.sp,
                        color = TipColor
                    )

                    // 间距
                    Spacer(Modifier.width(5.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = TipColor
                    )
                    // 弹幕数
                    Text(
                        text = "${video.danmaku}",
                        fontSize = 11.sp,
                        color = TipColor
                    )
                    Spacer(Modifier.weight(1f))
                    Icon(
                        imageVector = Icons.Outlined.MoreHoriz,
                        contentDescription = "更多操作",
                        modifier = Modifier
                            .clickable(enabled = moreEnabled, onClick = onMoreClick)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                            .size(20.dp),
                        tint = TipColor,
                    )
                }
            }
        }

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LikeVideoBottomDialog(
    video: LikeVideo,
    isActionBusy: Boolean,
    onDismissRequest: () -> Unit,
    onCancelLike: () -> Unit,
    onAddToWatchLater: () -> Unit,
) {
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    ) {
        Text(
            text = video.title,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
        )
        ListItem(
            headlineContent = { Text("取消点赞") },
            leadingContent = { Icon(Icons.Outlined.ThumbUpOffAlt, contentDescription = null) },
            modifier = Modifier.clickable(enabled = !isActionBusy, onClick = onCancelLike),
        )
        if (video.state) {
            ListItem(
                headlineContent = { Text("添加至稍后再看") },
                leadingContent = { Icon(Icons.Outlined.Schedule, contentDescription = null) },
                modifier = Modifier.clickable(enabled = !isActionBusy, onClick = onAddToWatchLater),
            )
        }
        if (isActionBusy) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                Spacer(Modifier.width(12.dp))
                Text("处理中", color = TipColor)
            }
        }
        Spacer(Modifier.height(16.dp))
    }
}
