package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.biz.user.viewmodel.WatchHistoryViewModel
import top.suzhelan.bili.shared.common.ui.CenterTitleUI
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@Composable
fun WatchHistoryScreen() {
    val navigator = LocalNavigation.currentOrThrow
    CommonComposeUI(
        viewModel = viewModel { WatchHistoryViewModel() },
        topBar = {
            CenterTitleUI(title = "历史记录", onClickBack = navigator::pop)
        },
    ) { viewModel ->
        WatchHistoryContent(viewModel)
    }
}

@Composable
private fun WatchHistoryContent(viewModel: WatchHistoryViewModel) {
    val historyItems = viewModel.history.collectAsLazyPagingItems()
    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = historyItems.itemCount,
            key = historyItems.itemKey { "${it.history.business}_${it.history.oid}_${it.viewAt}" },
        ) { index ->
            val item = historyItems[index] ?: return@items
            WatchHistoryItem(item)
        }

        when (val refresh = historyItems.loadState.refresh) {
            is LoadState.Loading -> item { LoadingIndicator() }
            is LoadState.Error -> item {
                LoadFailed(
                    onRetry = historyItems::retry,
                    message = refresh.error.message ?: "历史记录加载失败",
                )
            }

            is LoadState.NotLoading -> if (historyItems.itemCount == 0) {
                item { EmptyHistory() }
            }
        }
        when (val append = historyItems.loadState.append) {
            is LoadState.Loading -> item { LoadingIndicator() }
            is LoadState.Error -> item {
                LoadFailed(onRetry = historyItems::retry, message = "加载更多失败，点击重试")
            }

            is LoadState.NotLoading -> if (historyItems.itemCount > 0 && append.endOfPaginationReached) {
                item {
                    Text(
                        text = "没有更多记录了",
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center,
                        color = TipColor,
                        fontSize = 12.sp,
                    )
                }
            }
        }
    }
}

@Composable
private fun WatchHistoryItem(item: WatchHistoryPage.Item) {
    val navigator = LocalNavigation.currentOrThrow
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(112.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable {
                when (item.history.business) {
                    "archive" -> navigator.push(SharedScreen.VideoPlayer(aid = item.history.oid))
                    "pgc" -> item.history.epid.takeIf { it != 0L }?.let {
                        navigator.push(SharedScreen.VideoPlayer(epid = it.toString()))
                    }
                }
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            Box(modifier = Modifier.width(148.dp).fillMaxHeight()) {
                AsyncImage(
                    model = item.cover,
                    contentDescription = item.title,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(28.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.55f),
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
            Column(
                modifier = Modifier.fillMaxHeight().weight(1f).padding(8.dp),
            ) {
                Text(
                    text = item.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                if (item.longTitle.isNotBlank()) {
                    Text(
                        text = item.longTitle,
                        fontSize = 11.sp,
                        color = TipColor,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = listOf(item.authorName, TimeUtils.formatTimeAgo(item.viewAt))
                        .filter { it.isNotBlank() }
                        .joinToString(" · "),
                    fontSize = 11.sp,
                    color = TipColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

private fun historyProgress(item: WatchHistoryPage.Item): String = when {
    item.progress < 0 -> "已看完"
    item.duration > 0 -> "已看 ${TimeUtils.formatSecondToTime(item.progress.coerceAtLeast(0))}"
    else -> "已观看"
}

@Composable
private fun EmptyHistory() {
    Text(
        text = "暂无观看记录",
        modifier = Modifier.fillMaxWidth().padding(48.dp),
        textAlign = TextAlign.Center,
        color = TipColor,
    )
}

@Composable
private fun LoadFailed(onRetry: () -> Unit, message: String) {
    Text(
        text = message,
        modifier = Modifier.fillMaxWidth().clickable(onClick = onRetry).padding(24.dp),
        textAlign = TextAlign.Center,
        color = TipColor,
        fontSize = 12.sp,
    )
}
