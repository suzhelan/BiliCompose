package top.suzhelan.bili.biz.recvids.ui.page

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import top.suzhelan.bili.biz.recvids.ui.card.EmptyCard
import top.suzhelan.bili.biz.recvids.ui.card.PopularCoverCard
import top.suzhelan.bili.biz.recvids.ui.card.PopularLoadingCard
import top.suzhelan.bili.biz.recvids.viewmodel.PopularViewModel
import top.suzhelan.bili.shared.common.ui.PagerBottomIndicator


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PopularVideoContent(
    modifier: Modifier = Modifier,
    viewModel: PopularViewModel = viewModel {
        PopularViewModel()
    }
) {
    val lazyPagingItems = viewModel.popularListFlow.collectAsLazyPagingItems()
    val isRefreshing by remember {
        derivedStateOf { lazyPagingItems.loadState.refresh is LoadState.Loading }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            lazyPagingItems.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyColumn {
            //骨架屏
            if (lazyPagingItems.itemCount == 0) {
                items(10) {
                    PopularLoadingCard()
                }
            }
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey {
                    it.bvid
                }
            ) { index ->
                val popularItem = lazyPagingItems[index]
                if (popularItem == null) {
                    EmptyCard()
                    return@items
                }
                PopularCoverCard(popularItem)
            }

            item {
                PagerBottomIndicator(lazyPagingItems)
            }
        }
    }
}