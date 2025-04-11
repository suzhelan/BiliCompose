package top.sacz.bili.biz.recvids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import top.sacz.bili.biz.home.ui.EmptyCard
import top.sacz.bili.biz.home.ui.VideoCard
import top.sacz.bili.biz.recvids.viewmodel.FeedViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedVideoPage(viewModel: FeedViewModel = viewModel()) {

    val lazyPagingItems = viewModel.recommendedListFlow.collectAsLazyPagingItems()
    val isRefreshing = lazyPagingItems.loadState.refresh is LoadState.Loading

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            lazyPagingItems.refresh()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //骨架屏
            if (lazyPagingItems.itemCount == 0) {
                items(20) {
                    EmptyCard()
                }
            }
            items(lazyPagingItems.itemCount) { index ->
                val video = lazyPagingItems[index]
                VideoCard(video!!)
            }
        }
    }
}
