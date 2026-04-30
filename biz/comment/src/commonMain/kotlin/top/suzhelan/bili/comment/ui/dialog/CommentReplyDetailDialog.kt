package top.suzhelan.bili.comment.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.comment.entity.CommentReplyPage
import top.suzhelan.bili.comment.entity.NewComment
import top.suzhelan.bili.comment.ui.CommentInputBar
import top.suzhelan.bili.comment.ui.item.CommentCard
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.theme.TipColor

/**
 * 评论回复详情 BottomDialog。
 *
 * 高度控制在 0.48f，满足“低于 0.5f”的要求。内容结构固定为：
 * 1. 主评论：使用接口返回的 root，接口还没返回时使用列表项 selectedComment 兜底。
 * 2. 回复列表：展示回复这条主评论的二级评论，排在主评论下方。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommentReplyDetailDialog(
    selectedComment: NewComment,
    replyDetail: BiliResponse<CommentReplyPage>,
    publishedReplies: List<NewComment>,
    isPublishingReply: Boolean,
    publishError: String?,
    onRetry: () -> Unit,
    onDismissRequest: () -> Unit,
    onInputChanged: () -> Unit,
    onPublishReply: (
        root: NewComment,
        parent: NewComment,
        message: String,
        onSuccess: () -> Unit,
    ) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val replyListState = rememberLazyListState()
    var replyMessage by remember(selectedComment.rpid) { mutableStateOf("") }
    var replyTarget by remember(selectedComment.rpid) { mutableStateOf(selectedComment) }
    val uiData = replyDetail.toReplyDetailUiData(selectedComment)
    val replies = mergeReplies(
        baseReplies = uiData.replies,
        publishedReplies = publishedReplies
    )
    val totalCount = uiData.totalCount + countNewReplies(uiData.replies, publishedReplies)
    val lastPublishedReplyId = publishedReplies.lastOrNull()?.rpid

    LaunchedEffect(lastPublishedReplyId) {
        if (lastPublishedReplyId != null && replies.isNotEmpty()) {
            replyListState.animateScrollToItem(2 + replies.lastIndex)
        }
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.48f)
        ) {
            Text(
                text = "回复详情",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )

            ReplyDetailList(
                root = uiData.root,
                replies = replies,
                totalCount = totalCount,
                listState = replyListState,
                modifier = Modifier.weight(1f),
                onReplyClick = { comment ->
                    replyTarget = comment
                },
                footer = buildReplyDetailFooter(
                    uiData = uiData,
                    onRetry = onRetry
                )
            )

            CommentInputBar(
                value = replyMessage,
                onValueChange = { value ->
                    replyMessage = value
                    onInputChanged()
                },
                placeholder = "回复 @${replyTarget.member.uname.ifBlank { "评论" }}",
                replyTargetText = "回复 @${replyTarget.member.uname.ifBlank { "评论" }}",
                onCancelReplyTarget = if (replyTarget.rpid == uiData.root.rpid) {
                    null
                } else {
                    { replyTarget = uiData.root }
                },
                isSending = isPublishingReply,
                errorMessage = publishError,
                onSend = {
                    onPublishReply(uiData.root, replyTarget, replyMessage) {
                        replyMessage = ""
                        replyTarget = uiData.root
                    }
                }
            )
        }
    }
}

private data class ReplyDetailUiData(
    val root: NewComment,
    val replies: List<NewComment>,
    val totalCount: Int,
    val loadingText: String? = null,
    val errorMessage: String? = null,
)

@Composable
private fun ReplyDetailList(
    root: NewComment,
    replies: List<NewComment>,
    totalCount: Int,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    onReplyClick: (NewComment) -> Unit,
    footer: @Composable (() -> Unit)? = null,
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(bottom = 12.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        item(key = "root-${root.rpid}") {
            // 主评论使用完整评论卡片，让发布者信息、内容和操作信息保持主详情视图。
            CommentCard(
                comment = root,
                showReplyPreview = false,
                onReplyClick = onReplyClick
            )
        }

        item(key = "divider") {
            HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp))
            Text(
                text = if (totalCount > 0) "全部回复 $totalCount" else "暂无回复",
                color = TipColor,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp)
            )
        }

        items(
            items = replies,
            key = { reply -> reply.rpid }
        ) { reply ->
            // 二级回复在主评论下方顺序排列；点击行为留给后续对话树能力扩展。
            CommentCard(
                comment = reply,
                showReplyPreview = false,
                onReplyClick = onReplyClick
            )
        }

        if (footer != null) {
            item(key = "footer") {
                footer()
            }
        }
    }
}

private fun BiliResponse<CommentReplyPage>.toReplyDetailUiData(
    selectedComment: NewComment,
): ReplyDetailUiData {
    return when (this) {
        is BiliResponse.SuccessOrNull -> {
            val detail = data
            if (detail == null) {
                ReplyDetailUiData(
                    root = selectedComment,
                    replies = selectedComment.replies.orEmpty(),
                    totalCount = selectedComment.rcount,
                    errorMessage = "回复详情为空"
                )
            } else {
                ReplyDetailUiData(
                    root = detail.root,
                    replies = detail.replies.orEmpty(),
                    totalCount = detail.page.count
                )
            }
        }

        is BiliResponse.Success -> {
            ReplyDetailUiData(
                root = data.root,
                replies = data.replies.orEmpty(),
                totalCount = data.page.count
            )
        }

        is BiliResponse.Loading -> {
            ReplyDetailUiData(
                root = selectedComment,
                replies = selectedComment.replies.orEmpty(),
                totalCount = selectedComment.rcount,
                loadingText = "加载回复中..."
            )
        }

        is BiliResponse.Error -> {
            ReplyDetailUiData(
                root = selectedComment,
                replies = selectedComment.replies.orEmpty(),
                totalCount = selectedComment.rcount,
                errorMessage = msg.ifBlank { "回复详情加载失败" }
            )
        }

        is BiliResponse.Wait -> {
            ReplyDetailUiData(
                root = selectedComment,
                replies = selectedComment.replies.orEmpty(),
                totalCount = selectedComment.rcount
            )
        }
    }
}

private fun mergeReplies(
    baseReplies: List<NewComment>,
    publishedReplies: List<NewComment>,
): List<NewComment> {
    return (baseReplies + publishedReplies).distinctBy { reply -> reply.rpid }
}

private fun countNewReplies(
    baseReplies: List<NewComment>,
    publishedReplies: List<NewComment>,
): Int {
    return publishedReplies.count { published ->
        baseReplies.none { base -> base.rpid == published.rpid }
    }
}

@Composable
private fun buildReplyDetailFooter(
    uiData: ReplyDetailUiData,
    onRetry: () -> Unit,
): (@Composable () -> Unit)? {
    uiData.loadingText?.let { loadingText ->
        return {
            LoadingIndicator(
                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                text = loadingText
            )
        }
    }
    uiData.errorMessage?.let { errorMessage ->
        return {
            DetailError(
                message = errorMessage,
                onRetry = onRetry
            )
        }
    }
    return null
}

@Composable
private fun DetailError(
    message: String,
    onRetry: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = message,
            color = TipColor,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        TextButton(onClick = onRetry, modifier = Modifier.fillMaxWidth()) {
            Text("重试")
        }
    }
}
