package top.suzhelan.bili.comment.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.compose.collectAsLazyPagingItems
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.isError
import top.suzhelan.bili.api.isLoading
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.item.CommentCard
import top.suzhelan.bili.comment.viewmodel.CommentViewModel
import top.suzhelan.bili.shared.common.ui.PagerBottomIndicator

@Composable
fun CommentContent(oid: String, type: CommentSourceType) {
    val viewModel = viewModel { CommentViewModel() }

    val lazyPagingItems = viewModel.getCommentList(oid, type).collectAsLazyPagingItems()
    val sendResult by viewModel.sendCommentResult.collectAsState()

    var showInputDialog by remember { mutableStateOf(false) }

    LaunchedEffect(sendResult) {
        if (sendResult.isSuccess()) {
            showInputDialog = false
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 60.dp)
        ) {
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
            item {
                PagerBottomIndicator(lazyPagingItems)
            }
        }

        CommentInputBar(
            onSendClick = { showInputDialog = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
        )
    }

    if (showInputDialog) {
        SendCommentDialog(
            onDismiss = { showInputDialog = false },
            onSend = { message ->
                viewModel.sendComment(
                    oid = oid.toLongOrNull() ?: 0L,
                    type = type,
                    message = message
                )
            },
            sendResult = sendResult,
            onClearResult = { viewModel.clearSendResult() }
        )
    }
}

@Composable
private fun CommentInputBar(
    onSendClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier,
        tonalElevation = 2.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "发表评论",
                fontSize = 14.sp,
                color = Color.Gray,
                modifier = Modifier
                    .weight(1f)
                    .clickable(onClick = onSendClick)
                    .padding(vertical = 8.dp)
            )
            
            Button(
                onClick = onSendClick,
                contentPadding = PaddingValues(horizontal = 16.dp)
            ) {
                Text("发送")
            }
        }
    }
}

@Composable
private fun SendCommentDialog(
    onDismiss: () -> Unit,
    onSend: (String) -> Unit,
    sendResult: BiliResponse<*>,
    onClearResult: () -> Unit
) {
    var commentText by remember { mutableStateOf("") }
    val isLoading = sendResult.isLoading()

    LaunchedEffect(sendResult) {
        if (sendResult.isSuccess()) {
            commentText = ""
            onDismiss()
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("发表评论") },
        text = {
            Column {
                OutlinedTextField(
                    value = commentText,
                    onValueChange = { commentText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("请输入评论内容") },
                    minLines = 3,
                    maxLines = 5,
                    enabled = !isLoading
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                if (sendResult.isError()) {
                    val errorResult = sendResult as BiliResponse.Error
                    Text(
                        text = "发送失败: ${errorResult.msg}",
                        color = MaterialTheme.colorScheme.error,
                        fontSize = 12.sp
                    )
                }
                
                if (isLoading) {
                    LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (commentText.isNotBlank()) {
                        onSend(commentText)
                    }
                },
                enabled = commentText.isNotBlank() && !isLoading
            ) {
                Text("发送")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("取消")
            }
        }
    )
}