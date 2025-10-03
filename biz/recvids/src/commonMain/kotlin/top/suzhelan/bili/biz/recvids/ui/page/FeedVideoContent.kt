package top.suzhelan.bili.biz.recvids.ui.page

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.recvids.ui.card.EmptyCard
import top.suzhelan.bili.biz.recvids.ui.card.UnknownTypeCoverCard
import top.suzhelan.bili.biz.recvids.ui.card.VideoCard
import top.suzhelan.bili.biz.recvids.viewmodel.FeedViewModel
import top.suzhelan.bili.shared.common.ui.PagerBottomIndicator
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.dialog.DialogState

/**
 * 视频流布局实现
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedVideoContent(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel {
        FeedViewModel()
    }
) {


    val lazyPagingItems = viewModel.recommendedListFlow.collectAsLazyPagingItems()
    val isRefreshing by remember {
        derivedStateOf { lazyPagingItems.loadState.refresh is LoadState.Loading }
    }

    LaunchedEffect(lazyPagingItems.loadState.refresh) {
        val refreshState = lazyPagingItems.loadState.refresh
        //出现异常
        if (refreshState is LoadState.Error) {
            viewModel.updateDialog(
                DialogState.Message(
                title = "提示",
                text = "网络异常，请稍后再试,点击确定可重试,下面是异常信息，尽管你可能并看不懂：\n${refreshState.error}",
                confirmButtonText = "确定",
                onConfirmRequest = {
                    viewModel.dismissDialog()
                    lazyPagingItems.refresh()
                }
            ))
        }
    }

    DialogHandler(viewModel)
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            lazyPagingItems.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 10.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            //骨架屏
            if (lazyPagingItems.itemCount == 0) {
                items(10) {
                    EmptyCard()
                }
            }
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey {
                    when (it) {
                        is SmallCoverV2Item -> it.param
                        else -> it.idx
                    }
                }
            ) { index ->
                val video = lazyPagingItems[index]
                if (video == null) {
                    EmptyCard()
                    return@items
                }
                when (video) {
                    is SmallCoverV2Item -> {
                        VideoCard(video)
                    }

                    else -> {
                        UnknownTypeCoverCard(video)
                    }
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                PagerBottomIndicator(lazyPagingItems)
            }
        }
    }
}