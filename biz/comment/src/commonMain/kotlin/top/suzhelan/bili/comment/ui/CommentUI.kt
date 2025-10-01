package top.suzhelan.bili.comment.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.item.CommentCard
import top.suzhelan.bili.comment.viewmodel.CommentViewModel
import top.suzhelan.bili.shared.common.ui.PagerBottomIndicator


@Composable
fun CommentContent(oid: String, type: CommentSourceType) {
    val viewModel = viewModel { CommentViewModel() }

    val lazyPagingItems = viewModel.getCommentList(oid, type).collectAsLazyPagingItems()

    LazyColumn(modifier = Modifier.fillMaxSize()) {
        items(lazyPagingItems.itemCount) { index ->
            CommentCard(lazyPagingItems[index]!!)
            if (index < lazyPagingItems.itemCount - 1) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }
        item {
            PagerBottomIndicator(lazyPagingItems)
        }
    }
}

