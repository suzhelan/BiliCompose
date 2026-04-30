package top.suzhelan.bili.comment.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.dialog.CommentReplyDetailDialog
import top.suzhelan.bili.comment.ui.item.CommentCard
import top.suzhelan.bili.comment.viewmodel.CommentPublishMessageScope
import top.suzhelan.bili.comment.viewmodel.CommentViewModel
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun CommentContent(oid: String, type: CommentSourceType) {
    val viewModel = viewModel { CommentViewModel() }
    val navigation = LocalNavigation.currentOrThrow
    val lazyPagingItems = viewModel.getCommentList(oid, type).collectAsLazyPagingItems()
    val listState = rememberLazyListState()
    val publishedRootCommentsFlow = remember(oid, type) {
        viewModel.getPublishedRootComments(oid, type)
    }
    val publishedRootComments by publishedRootCommentsFlow.collectAsStateWithLifecycle(emptyList())
    val publishState by viewModel.publishState.collectAsStateWithLifecycle()
    val showLoginRequiredDialog by viewModel.showLoginRequiredDialog.collectAsStateWithLifecycle()
    var rootCommentText by remember(oid, type) { mutableStateOf("") }
    val latestPublishedRootId = publishedRootComments.firstOrNull()?.rpid

    LaunchedEffect(latestPublishedRootId) {
        if (latestPublishedRootId != null) {
            listState.animateScrollToItem(0)
        }
    }

    CommentReplyDetailDialog(oid, type, viewModel)
    if (showLoginRequiredDialog) {
        LoginRequiredDialog(
            onDismissRequest = {
                viewModel.dismissLoginRequiredDialog()
            },
            onConfirmRequest = {
                viewModel.dismissLoginRequiredDialog()
                navigation.push(SharedScreen.Login)
            }
        )
    }

    Column(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxWidth().weight(1f)
        ) {
            items(
                items = publishedRootComments,
                key = { comment -> "published-root-${comment.rpid}" }
            ) { comment ->
                CommentCard(
                    comment = comment,
                    onClick = { clickedComment ->
                        viewModel.openReplyDetail(oid, type, clickedComment)
                    },
                    onReplyClick = { clickedComment ->
                        viewModel.openReplyDetail(oid, type, clickedComment)
                    }
                )
            }

            items(
                count = lazyPagingItems.itemCount,
                key = { index ->
                    lazyPagingItems[index]?.rpid?.let { rpid -> "paging-$rpid" }
                        ?: "paging-index-$index"
                }
            ) { index ->
                val item = lazyPagingItems[index]
                if (item != null) {
                    CommentCard(
                        comment = item,
                        onClick = { comment ->
                            viewModel.openReplyDetail(oid, type, comment)
                        },
                        onReplyClick = { comment ->
                            viewModel.openReplyDetail(oid, type, comment)
                        }
                    )
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

        CommentInputBar(
            value = rootCommentText,
            onValueChange = { value ->
                rootCommentText = value
                viewModel.clearPublishMessage()
            },
            placeholder = "发一条友善的评论",
            isSending = publishState.isPublishingRoot,
            errorMessage = publishState.message.takeIf {
                publishState.messageScope == CommentPublishMessageScope.Root
            },
            onSend = {
                viewModel.publishRootComment(oid, type, rootCommentText) {
                    rootCommentText = ""
                }
            }
        )
    }
}

@Composable
private fun LoginRequiredDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = {
            Text("需要登录")
        },
        text = {
            Text("登录后才能发表评论")
        },
        confirmButton = {
            TextButton(onClick = onConfirmRequest) {
                Text("去登录")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismissRequest) {
                Text("取消")
            }
        }
    )
}

/**
 * 回复详情
 **/
@Composable
private fun CommentReplyDetailDialog(
    oid: String,
    type: CommentSourceType,
    viewModel: CommentViewModel,
) {
    val selectedComment by viewModel.selectedComment.collectAsStateWithLifecycle()
    val replyDetail by viewModel.replyDetail.collectAsStateWithLifecycle()
    val publishedRepliesByRoot by viewModel.publishedRepliesByRoot.collectAsStateWithLifecycle()
    val publishState by viewModel.publishState.collectAsStateWithLifecycle()

    selectedComment?.let { comment ->
        CommentReplyDetailDialog(
            selectedComment = comment,
            replyDetail = replyDetail,
            publishedReplies = publishedRepliesByRoot[comment.rpid].orEmpty(),
            isPublishingReply = publishState.publishingReplyRoot == comment.rpid,
            publishError = publishState.message.takeIf {
                publishState.messageScope == CommentPublishMessageScope.Reply &&
                        publishState.messageRoot == comment.rpid
            },
            onRetry = {
                viewModel.openReplyDetail(oid, type, comment)
            },
            onDismissRequest = {
                viewModel.closeReplyDetail()
            },
            onInputChanged = {
                viewModel.clearPublishMessage()
            },
            onPublishReply = { root, parent, message, onSuccess ->
                viewModel.publishReplyComment(
                    oid = oid,
                    type = type,
                    root = root,
                    parent = parent,
                    message = message,
                    onSuccess = onSuccess
                )
            }
        )
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
