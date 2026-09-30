package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.biz.user.viewmodel.WatchHistoryViewModel
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

private enum class HistoryTopBarMode { Normal, Search, Manage }

@Composable
fun WatchHistoryScreen() {
    val navigator = LocalNavigation.currentOrThrow
    val viewModel = viewModel { WatchHistoryViewModel() }
    val historyItems = viewModel.history.collectAsLazyPagingItems()
    var mode by remember { mutableStateOf(HistoryTopBarMode.Normal) }
    var searchQuery by remember { mutableStateOf("") }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    val visibleItems = historyItems.itemSnapshotList.items.filter { it.matches(searchQuery) }

    CommonComposeUI(
        viewModel = viewModel,
        topBar = {
            WatchHistoryTopBar(
                mode = mode,
                searchQuery = searchQuery,
                selectedCount = viewModel.selectedCount,
                isDeleting = viewModel.isDeleting,
                onBack = navigator::pop,
                onEnterSearch = { mode = HistoryTopBarMode.Search },
                onQueryChange = { searchQuery = it },
                onCloseSearch = {
                    searchQuery = ""
                    mode = HistoryTopBarMode.Normal
                },
                onSelectAll = { viewModel.selectAll(visibleItems) },
                onRequestDelete = { showDeleteConfirmation = true },
                onCloseManage = {
                    viewModel.clearSelection()
                    mode = HistoryTopBarMode.Normal
                },
            )
        },
    ) {
        DialogHandler(viewModel)
        if (showDeleteConfirmation) {
            DeleteHistoryConfirmation(
                count = viewModel.selectedCount,
                onDismiss = { showDeleteConfirmation = false },
                onConfirm = {
                    showDeleteConfirmation = false
                    viewModel.deleteSelected {
                        historyItems.refresh()
                        viewModel.clearSelection()
                        mode = HistoryTopBarMode.Normal
                    }
                },
            )
        }
        WatchHistoryContent(
            historyItems = historyItems,
            visibleItems = visibleItems,
            isSearching = searchQuery.isNotBlank(),
            isManaging = mode == HistoryTopBarMode.Manage,
            viewModel = viewModel,
            onEnterManagement = { mode = HistoryTopBarMode.Manage },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WatchHistoryTopBar(
    mode: HistoryTopBarMode,
    searchQuery: String,
    selectedCount: Int,
    isDeleting: Boolean,
    onBack: () -> Unit,
    onEnterSearch: () -> Unit,
    onQueryChange: (String) -> Unit,
    onCloseSearch: () -> Unit,
    onSelectAll: () -> Unit,
    onRequestDelete: () -> Unit,
    onCloseManage: () -> Unit,
) {
    when (mode) {
        HistoryTopBarMode.Normal -> TopAppBar(
            title = { Text("历史记录") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "返回")
                }
            },
            actions = {
                IconButton(onClick = onEnterSearch) {
                    Icon(Icons.Outlined.Search, contentDescription = "搜索历史记录")
                }
            },
        )

        HistoryTopBarMode.Search -> TopAppBar(
            title = {
                TextField(
                    value = searchQuery,
                    onValueChange = onQueryChange,
                    singleLine = true,
                    placeholder = { Text("搜索历史记录") },
                )
            },
            navigationIcon = {
                IconButton(onClick = onCloseSearch) {
                    Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "退出搜索")
                }
            },
            actions = {
                IconButton(onClick = onCloseSearch) {
                    Icon(Icons.Outlined.Close, contentDescription = "关闭搜索")
                }
            },
        )

        HistoryTopBarMode.Manage -> TopAppBar(
            title = { Text("批量管理") },
            navigationIcon = { TextButton(onClick = onSelectAll) { Text("全选") } },
            actions = {
                IconButton(
                    enabled = selectedCount > 0 && !isDeleting,
                    onClick = onRequestDelete,
                ) {
                    Icon(
                        Icons.Outlined.DeleteOutline,
                        contentDescription = "删除已选 $selectedCount 条记录"
                    )
                }
                IconButton(enabled = !isDeleting, onClick = onCloseManage) {
                    Icon(Icons.Outlined.Close, contentDescription = "关闭批量管理")
                }
            },
        )
    }
}

@Composable
private fun DeleteHistoryConfirmation(count: Int, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("删除历史记录") },
        text = { Text("确定删除已选择的 $count 条观看记录吗？此操作无法撤销。") },
        confirmButton = { TextButton(onClick = onConfirm) { Text("删除") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun WatchHistoryContent(
    historyItems: LazyPagingItems<WatchHistoryPage.Item>,
    visibleItems: List<WatchHistoryPage.Item>,
    isSearching: Boolean,
    isManaging: Boolean,
    viewModel: WatchHistoryViewModel,
    onEnterManagement: () -> Unit,
) {
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        if (isSearching) {
            groupedHistoryItems(visibleItems, isManaging, viewModel, onEnterManagement)
            if (historyItems.itemSnapshotList.items.isNotEmpty() && visibleItems.isEmpty()) {
                item { SearchEmpty() }
            }
        } else {
            items(
                count = historyItems.itemCount,
                key = historyItems.itemKey { historyItemKey(it) },
            ) { index ->
                val item = historyItems[index] ?: return@items
                val previous = if (index > 0) historyItems.peek(index - 1) else null
                HistoryListEntry(
                    item = item,
                    showSection = previous?.let { historySection(it.viewAt) } != historySection(item.viewAt),
                    isManaging = isManaging,
                    isSelected = viewModel.isSelected(item),
                    onEnterManagement = onEnterManagement,
                    onToggleSelection = { viewModel.toggleSelection(item) },
                )
            }
        }

        when (val refresh = historyItems.loadState.refresh) {
            is LoadState.Loading -> item { LoadingIndicator() }
            is LoadState.Error -> item {
                LoadFailed(historyItems::retry, refresh.error.message ?: "历史记录加载失败")
            }

            is LoadState.NotLoading -> if (historyItems.itemCount == 0) item { EmptyHistory() }
        }
        when (val append = historyItems.loadState.append) {
            is LoadState.Loading -> if (!isSearching) item { LoadingIndicator() }
            is LoadState.Error -> if (!isSearching) item {
                LoadFailed(historyItems::retry, "加载更多失败，点击重试")
            }

            is LoadState.NotLoading -> if (!isSearching && historyItems.itemCount > 0 && append.endOfPaginationReached) {
                item { ListEnd() }
            }
        }
    }
}

private fun LazyListScope.groupedHistoryItems(
    items: List<WatchHistoryPage.Item>,
    isManaging: Boolean,
    viewModel: WatchHistoryViewModel,
    onEnterManagement: () -> Unit,
) {
    var lastSection: String? = null
    items.forEach { item ->
        val section = historySection(item.viewAt)
        item(key = historyItemKey(item)) {
            HistoryListEntry(
                item = item,
                showSection = section != lastSection,
                isManaging = isManaging,
                isSelected = viewModel.isSelected(item),
                onEnterManagement = onEnterManagement,
                onToggleSelection = { viewModel.toggleSelection(item) },
            )
        }
        lastSection = section
    }
}

@Composable
private fun HistoryListEntry(
    item: WatchHistoryPage.Item,
    showSection: Boolean,
    isManaging: Boolean,
    isSelected: Boolean,
    onEnterManagement: () -> Unit,
    onToggleSelection: () -> Unit,
) {
    Column {
        if (showSection) HistorySectionTitle(historySection(item.viewAt))
        WatchHistoryItem(item, isManaging, isSelected, onEnterManagement, onToggleSelection)
    }
}

@Composable
private fun HistorySectionTitle(title: String) {
    Text(
        text = title,
        modifier = Modifier.fillMaxWidth().padding(start = 16.dp, top = 14.dp, bottom = 4.dp),
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun WatchHistoryItem(
    item: WatchHistoryPage.Item,
    isManaging: Boolean,
    isSelected: Boolean,
    onEnterManagement: () -> Unit,
    onToggleSelection: () -> Unit,
) {
    val navigator = LocalNavigation.currentOrThrow
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .combinedClickable(
                onClick = {
                    if (isManaging) onToggleSelection() else when (item.history.business) {
                        "archive" -> navigator.push(SharedScreen.VideoPlayer(aid = item.history.oid))
                        "pgc" -> item.history.epid.takeIf { it != 0L }?.let {
                            navigator.push(SharedScreen.VideoPlayer(epid = it.toString()))
                        }
                    }
                },
                onLongClick = {
                    if (!isManaging) {
                        onToggleSelection()
                        onEnterManagement()
                    }
                },
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            if (isManaging) {
                Checkbox(
                    checked = isSelected,
                    onCheckedChange = { onToggleSelection() },
                    modifier = Modifier.align(Alignment.CenterVertically),
                )
            }
            Box(modifier = Modifier.width(148.dp).fillMaxHeight()) {
                AsyncImage(
                    model = item.cover,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier.fillMaxWidth().height(28.dp).align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.55f)
                            )
                        ),
                )
                Text(
                    text = historyProgress(item),
                    color = Color.White,
                    fontSize = 10.sp,
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                )
            }
            Column(modifier = Modifier.fillMaxHeight().weight(1f).padding(8.dp)) {
                Text(
                    item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.longTitle.isNotBlank()) {
                    Text(
                        item.longTitle,
                        fontSize = 11.sp,
                        color = TipColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = listOf(item.authorName, TimeUtils.formatTimeAgo(item.viewAt))
                        .filter { it.isNotBlank() }.joinToString(" · "),
                    fontSize = 11.sp,
                    color = TipColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun historyItemKey(item: WatchHistoryPage.Item) =
    "${item.history.business}_${item.kid}_${item.viewAt}"

private fun WatchHistoryPage.Item.matches(query: String): Boolean {
    val normalized = query.trim()
    return normalized.isEmpty() || title.contains(normalized, true) ||
            longTitle.contains(normalized, true) || authorName.contains(normalized, true)
}

@OptIn(ExperimentalTime::class)
private fun historySection(viewAt: Long): String {
    val timeZone = TimeZone.currentSystemDefault()
    val today = Clock.System.now().toLocalDateTime(timeZone).date
    val viewedDate = Instant.fromEpochMilliseconds(viewAt * 1000).toLocalDateTime(timeZone).date
    val daysAgo = (today.toEpochDays() - viewedDate.toEpochDays()).coerceAtLeast(0)
    return when (daysAgo) {
        0L -> "今天"
        1L -> "昨天"
        in 2L..6L -> "本周"
        in 7L..29L -> "一周前"
        in 30L..59L -> "一月前"
        else -> "更早"
    }
}

private fun historyProgress(item: WatchHistoryPage.Item): String = when {
    item.progress < 0 -> "已看完"
    item.duration > 0 -> "已看 ${TimeUtils.formatSecondToTime(item.progress.coerceAtLeast(0))}"
    else -> "已观看"
}

@Composable
private fun EmptyHistory() = HistoryHint("暂无观看记录")

@Composable
private fun SearchEmpty() = HistoryHint("没有匹配的观看记录")

@Composable
private fun HistoryHint(text: String) {
    Text(
        text,
        Modifier.fillMaxWidth().padding(48.dp),
        textAlign = TextAlign.Center,
        color = TipColor
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LoadFailed(onRetry: () -> Unit, message: String) {
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth().combinedClickable(onClick = onRetry).padding(24.dp),
        textAlign = TextAlign.Center,
        color = TipColor,
        fontSize = 12.sp,
    )
}

@Composable
private fun ListEnd() {
    Text(
        text = "没有更多记录了",
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        textAlign = TextAlign.Center,
        color = TipColor,
        fontSize = 12.sp,
    )
}
