package top.sacz.bili.biz.recvids.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import top.sacz.bili.biz.recvids.viewmodel.FeedViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedVideoPage(viewModel: FeedViewModel = viewModel()) {

    val lazyPagingItems = viewModel.recommendedListFlow.collectAsLazyPagingItems()
    val isRefreshing by derivedStateOf { lazyPagingItems.loadState.refresh is LoadState.Loading }

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
                items(10) {
                    EmptyCard()
                }
            }
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.param }
            ) { index ->
                val video = lazyPagingItems[index]
                if (video != null) {
                    VideoCard(video)
                } else {
                    EmptyCard()
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                BottomLoadingIndicator(lazyPagingItems.loadState.append, retry = {
                    lazyPagingItems.retry()
                })
            }
        }
    }
}

/**
 * 底部加载指示器
 */
@Composable
private fun BottomLoadingIndicator(loadState: LoadState, retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // 增加垂直间距
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(36.dp)
                )
            }

            is LoadState.Error -> {
                // 错误状态处理
                val error = loadState.error
                Button(
                    onClick = retry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer, // 错误容器色
                        contentColor = MaterialTheme.colorScheme.onErrorContainer // 对比色
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("加载失败，点击重试")
                }
            }

            else -> Unit
        }
    }
}
