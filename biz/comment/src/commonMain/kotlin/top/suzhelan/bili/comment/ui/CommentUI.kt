package top.suzhelan.bili.comment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.item.CommentCard
import top.suzhelan.bili.comment.viewmodel.CommentViewModel
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.theme.TipColor


@Composable
fun CommentContent(oid: String, type: CommentSourceType) {
    val viewModel = viewModel { CommentViewModel() }
    val lazyPagingItems = viewModel.getCommentList(oid, type).collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(
            count = lazyPagingItems.itemCount,
            key = { index ->
                lazyPagingItems[index]?.rpid ?: index
            }
        ) { index ->
            val item = lazyPagingItems[index]
            if (item != null) {
                CommentCard(item)
            }
        }
        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingIndicator()
                }
            }

            is LoadState.NotLoading -> {
                item {
                    Text(
                        text = "没有更多了",
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        textAlign = TextAlign.Center,
                        color = TipColor
                    )
                }
            }

            is LoadState.Error -> {
                item {
                    CommentLoadError(
                        message = (lazyPagingItems.loadState.append as LoadState.Error).error.message
                            ?: "未知错误",
                        onRetry = {
                            lazyPagingItems.retry()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun CommentLoadError(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = message,
            textAlign = TextAlign.Center
        )
        Button(onClick = onRetry) {
            Text("重试")
        }
    }
}
