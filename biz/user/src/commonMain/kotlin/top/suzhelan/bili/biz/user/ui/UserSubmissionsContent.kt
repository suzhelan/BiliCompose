package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.user.entity.SubmissionCategory
import top.suzhelan.bili.biz.user.entity.SubmissionOrder
import top.suzhelan.bili.biz.user.entity.UserCollection
import top.suzhelan.bili.biz.user.entity.UserCollectionKind
import top.suzhelan.bili.biz.user.entity.secureImageUrl
import top.suzhelan.bili.biz.user.viewmodel.ProfileListState
import top.suzhelan.bili.biz.user.viewmodel.SubmissionWatchHistoryLoader
import top.suzhelan.bili.biz.user.viewmodel.SubmissionWatchHistoryState
import top.suzhelan.bili.biz.user.viewmodel.UserCollectionVideosViewModel
import top.suzhelan.bili.biz.user.viewmodel.UserSubmissionsViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@Composable
fun UserSubmissionsContent(
    mid: Long,
    viewModel: UserSubmissionsViewModel,
    listState: LazyListState,
    isActive: Boolean,
    showCollections: Boolean,
    onShowCollections: (Boolean) -> Unit,
    showInitialIndicator: Boolean,
) {
    val videos by viewModel.videos.state.collectAsStateWithLifecycle()
    val collections by viewModel.collections.state.collectAsStateWithLifecycle()
    val order by viewModel.order.collectAsStateWithLifecycle()
    val tid by viewModel.tid.collectAsStateWithLifecycle()
    val watchHistory = submissionWatchHistory(viewModel.watchHistory)
    val lastWatchedKey = watchHistory.lastWatchedKey(videos.items)
    val navigation = LocalNavigation.currentOrThrow
    val scope = rememberCoroutineScope()
    // 切换筛选时仍保留完整分区选项，不使用前一筛选的列表冒充新结果。
    var categories by remember(mid) { mutableStateOf(emptyList<SubmissionCategory>()) }
    LaunchedEffect(videos.categories, videos.hasLoaded, videos.isRefreshing) {
        if (videos.hasLoaded && !videos.isRefreshing && videos.error == null && videos.categories.isNotEmpty()) categories =
            videos.categories
    }
    LaunchedEffect(mid, isActive, showCollections) {
        if (isActive) {
            if (showCollections) viewModel.loadCollections(mid) else viewModel.loadVideos(mid)
        }
    }
    val header: LazyListScope.() -> Unit = {
        item(key = "submission-controls") {
            Column(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    FilterChip(selected = !showCollections, onClick = {
                        onShowCollections(false)
                        scope.launch { listState.scrollToItem(0) }
                    }, label = { Text("视频") })
                    FilterChip(selected = showCollections, onClick = {
                        onShowCollections(true)
                        scope.launch { listState.scrollToItem(0) }
                    }, label = { Text("合集与系列") })
                }
                if (!showCollections) {
                    Row(
                        Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Row(Modifier.weight(1f), verticalAlignment = Alignment.CenterVertically) {
                            var expanded by remember { mutableStateOf(false) }
                            Box(Modifier.weight(1f, fill = false)) {
                                TextButton(onClick = { expanded = true }) {
                                    Text(
                                        if (tid == 0) "全部分区 ▾" else "${categories.find { it.tid == tid }?.name ?: "当前分区"} ▾",
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis,
                                    )
                                }
                                DropdownMenu(
                                    expanded,
                                    { expanded = false },
                                    Modifier.heightIn(max = 360.dp)
                                ) {
                                    DropdownMenuItem(text = { Text("全部分区") }, onClick = {
                                        expanded = false
                                        viewModel.loadVideos(mid, tid = 0)
                                        scope.launch { listState.scrollToItem(0) }
                                    })
                                    categories.forEach { category ->
                                        DropdownMenuItem(
                                            text = { Text("${category.name}（${category.count}）") },
                                            onClick = {
                                                expanded = false
                                                viewModel.loadVideos(mid, tid = category.tid)
                                                scope.launch { listState.scrollToItem(0) }
                                            })
                                    }
                                }
                            }
                            if (videos.hasLoaded) Text(
                                "${videos.total} 个视频",
                                color = TipColor,
                                fontSize = 12.sp,
                                maxLines = 1,
                            )
                        }
                        Box {
                            var sortExpanded by remember { mutableStateOf(false) }
                            TextButton(onClick = { sortExpanded = true }) {
                                Text("${order.label} ▾", maxLines = 1)
                            }
                            DropdownMenu(sortExpanded, { sortExpanded = false }) {
                                SubmissionOrder.entries.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option.label) },
                                        trailingIcon = {
                                            if (order == option) Icon(
                                                Icons.Rounded.Check,
                                                contentDescription = "已选中"
                                            )
                                        },
                                        onClick = {
                                            sortExpanded = false
                                            viewModel.loadVideos(mid, order = option)
                                            scope.launch { listState.scrollToItem(0) }
                                        },
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    if (showCollections) {
        ProfilePagedList(
            state = collections,
            listState = listState,
            isActive = isActive,
            onRetry = { viewModel.loadCollections(mid, force = true) },
            onLoadMore = viewModel.collections::loadMore,
            emptyText = "暂无公开合集或系列",
            key = { it.key },
            header = header,
            showInitialIndicator = showInitialIndicator,
        ) { collection ->
            CollectionCard(collection) {
                navigation.push(
                    SharedScreen.UserCollection(
                        mid,
                        collection.id,
                        collection.kind.name,
                        collection.name
                    )
                )
            }
        }
    } else {
        ProfilePagedList(
            state = videos,
            listState = listState,
            isActive = isActive,
            onRetry = { viewModel.loadVideos(mid, force = true) },
            onLoadMore = viewModel.videos::loadMore,
            emptyText = if (tid == 0) "暂无公开视频投稿" else "该分区暂无公开视频投稿",
            key = { it.key },
            header = header,
            showInitialIndicator = showInitialIndicator,
        ) { video ->
            SubmissionVideoCard(
                video,
                watchHistory.find(video),
                watchHistory.accountId != null,
                video.key == lastWatchedKey
            )
        }
    }
}

@Composable
fun UserCollectionScreen(mid: Long, id: Long, kind: String, title: String) {
    val navigation = LocalNavigation.currentOrThrow
    val collectionKind = UserCollectionKind.entries.find { it.name == kind } ?: return
    key(mid, id, kind) {
        val vm = viewModel { UserCollectionVideosViewModel() }
        val state by vm.videos.state.collectAsStateWithLifecycle()
        val watchHistory = submissionWatchHistory(vm.watchHistory)
        val lastWatchedKey = watchHistory.lastWatchedKey(state.items)
        var reverse by rememberSaveable { mutableStateOf(false) }
        var pullRefreshing by remember { mutableStateOf(false) }
        val listState = rememberLazyListState()
        val scope = rememberCoroutineScope()
        LifecycleResumeEffect(mid, id, kind, reverse) {
            vm.load(mid, id, collectionKind, reverse, force = true)
            onPauseOrDispose { }
        }
        LaunchedEffect(pullRefreshing, state.isRefreshing) {
            if (!vm.videos.state.value.isRefreshing) pullRefreshing = false
        }
        AppScaffold(topBar = { TitleUI(title.ifBlank { collectionKind.label }) { navigation.pop() } }) {
            PullToRefreshBox(
                isRefreshing = pullRefreshing,
                onRefresh = {
                    pullRefreshing = true
                    vm.load(mid, id, collectionKind, reverse, force = true)
                },
                modifier = Modifier.fillMaxSize(),
            ) {
                ProfilePagedList(
                    state, listState, true,
                    onRetry = { vm.load(mid, id, collectionKind, reverse, force = true) },
                    onLoadMore = vm.videos::loadMore,
                    emptyText = "暂无公开视频",
                    key = { it.key },
                    showInitialIndicator = !pullRefreshing,
                    header = {
                        item {
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    if (state.hasLoaded) "${state.total} 个视频" else collectionKind.label,
                                    Modifier.weight(1f), color = TipColor,
                                )
                                TextButton(onClick = {
                                    reverse = !reverse
                                    scope.launch { listState.scrollToItem(0) }
                                }) { Text(if (reverse) "反向排序" else "默认排序") }
                            }
                        }
                    },
                ) { video ->
                    SubmissionVideoCard(
                        video,
                        watchHistory.find(video),
                        watchHistory.accountId != null,
                        video.key == lastWatchedKey
                    )
                }
            }
        }
    }
}

@Composable
private fun <T> ProfilePagedList(
    state: ProfileListState<T>,
    listState: LazyListState,
    isActive: Boolean,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    emptyText: String,
    key: (T) -> String,
    header: LazyListScope.() -> Unit,
    showInitialIndicator: Boolean = true,
    itemContent: @Composable (T) -> Unit,
) {
    val nearEnd by remember(listState) {
        derivedStateOf {
            val info = listState.layoutInfo
            info.totalItemsCount > 0 && (info.visibleItemsInfo.lastOrNull()?.index
                ?: -1) >= info.totalItemsCount - 3
        }
    }
    LaunchedEffect(
        isActive,
        nearEnd,
        state.page,
        state.hasMore,
        state.isRefreshing,
        state.isLoadingMore,
        state.error
    ) {
        if (isActive && nearEnd && state.hasMore && !state.isRefreshing && !state.isLoadingMore && state.error == null) onLoadMore()
    }
    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        header()
        if (state.notice != null) {
            item {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(state.notice, Modifier.weight(1f), color = TipColor, fontSize = 12.sp)
                    TextButton(onClick = onRetry, enabled = !state.isRefreshing) { Text("重试") }
                }
            }
        }
        if (showInitialIndicator && state.items.isEmpty() && (state.isRefreshing || (!state.hasLoaded && state.error == null))) {
            item {
                Box(
                    Modifier.fillMaxWidth().padding(32.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
        }
        if (state.error != null) {
            item {
                Column(
                    Modifier.fillMaxWidth().padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        state.error,
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center
                    )
                    TextButton(onClick = if (state.appendFailed) onLoadMore else onRetry) { Text("重试") }
                }
            }
        }
        items(state.items, key = key) { itemContent(it) }
        when {
            state.isLoadingMore -> item {
                Box(
                    Modifier.fillMaxWidth().padding(16.dp),
                    contentAlignment = Alignment.Center
                ) { CircularProgressIndicator(Modifier.size(24.dp)) }
            }

            state.hasLoaded && state.error == null && state.items.isEmpty() && !state.isRefreshing -> item {
                ProfileListMessage(
                    emptyText
                )
            }

            state.hasLoaded && state.items.isNotEmpty() && !state.hasMore -> item {
                ProfileListMessage(
                    "没有更多了"
                )
            }

            state.hasMore && state.error == null && !state.isRefreshing -> item {
                Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    TextButton(
                        onClick = onLoadMore
                    ) { Text("加载更多") }
                }
            }
        }
    }
}

@Composable
private fun ProfileListMessage(message: String) {
    Text(
        message,
        Modifier.fillMaxWidth().padding(20.dp),
        textAlign = TextAlign.Center,
        color = TipColor
    )
}

@Composable
private fun submissionWatchHistory(loader: SubmissionWatchHistoryLoader): SubmissionWatchHistoryState {
    val isLoggedIn by LoginMapper.isLoginState
    val accountId = if (isLoggedIn) LoginMapper.getMid() else null
    val state by loader.state.collectAsStateWithLifecycle()
    LifecycleResumeEffect(loader, accountId) {
        loader.refresh(accountId)
        onPauseOrDispose { }
    }
    return if (accountId == state.accountId) state else SubmissionWatchHistoryState(accountId)
}

@Composable
private fun CollectionCard(collection: UserCollection, onClick: () -> Unit) {
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 5.dp)
            .clickable(onClick = onClick)
    ) {
        BoxWithConstraints(Modifier.fillMaxWidth()) {
            val coverWidth = (maxWidth * 0.3f).coerceAtMost(180.dp)
            Row(
                Modifier.fillMaxWidth().padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    collection.cover.secureImageUrl(),
                    null,
                    Modifier.width(coverWidth).aspectRatio(16f / 10f)
                        .clip(RoundedCornerShape(6.dp)),
                    contentScale = ContentScale.Crop
                )
                Column(
                    Modifier.weight(1f).padding(start = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        collection.name,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        "${collection.kind.label} · ${collection.count} 个视频",
                        color = TipColor,
                        fontSize = 12.sp
                    )
                    if (collection.description.isNotBlank()) Text(
                        collection.description,
                        color = TipColor,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
