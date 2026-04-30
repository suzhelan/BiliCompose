package top.suzhelan.bili.comment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.CommentReplyPage
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.entity.NewComment
import top.suzhelan.bili.comment.entity.PublishCommentResult
import top.suzhelan.bili.comment.source.CommentDataSource
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.base.BaseViewModel

enum class CommentPublishMessageScope {
    Root,
    Reply,
}

data class CommentPublishState(
    val isPublishingRoot: Boolean = false,
    val publishingReplyRoot: Long? = null,
    val message: String? = null,
    val messageScope: CommentPublishMessageScope? = null,
    val messageRoot: Long? = null,
)

class CommentViewModel : BaseViewModel() {

    private data class CommentListKey(
        val oid: String,
        val type: CommentSourceType,
    )

    private val api = CommentApi()
    private val _selectedComment = MutableStateFlow<NewComment?>(null)
    val selectedComment = _selectedComment.asStateFlow()

    private val _replyDetail = MutableStateFlow<BiliResponse<CommentReplyPage>>(BiliResponse.Wait)
    val replyDetail = _replyDetail.asStateFlow()

    private val _publishState = MutableStateFlow(CommentPublishState())
    val publishState = _publishState.asStateFlow()

    private val _publishedRootCommentsByList =
        MutableStateFlow<Map<CommentListKey, List<NewComment>>>(emptyMap())

    private val _publishedRepliesByRoot = MutableStateFlow<Map<Long, List<NewComment>>>(emptyMap())
    val publishedRepliesByRoot = _publishedRepliesByRoot.asStateFlow()

    private val _showLoginRequiredDialog = MutableStateFlow(false)
    val showLoginRequiredDialog = _showLoginRequiredDialog.asStateFlow()

    private val commentListFlows = mutableMapOf<CommentListKey, Flow<PagingData<NewComment>>>()

    fun getCommentList(oid: String, type: CommentSourceType): Flow<PagingData<NewComment>> =
        commentListFlows.getOrPut(CommentListKey(oid, type)) {
            Pager(
                config = PagingConfig(
                    pageSize = 20,
                    prefetchDistance = 1, // 到最后一条才加载下一页
                    initialLoadSize = 20, // 避免首次默认加载 pageSize * 3
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    CommentDataSource(oid, type)
                }
            ).flow.cachedIn(viewModelScope)
        }

    fun getPublishedRootComments(
        oid: String,
        type: CommentSourceType,
    ): Flow<List<NewComment>> {
        val key = CommentListKey(oid, type)
        return _publishedRootCommentsByList.map { commentsByList ->
            commentsByList[key].orEmpty()
        }
    }

    /**
     * 打开回复详情弹窗时加载第一页二级回复。
     *
     * 这里保留 selectedComment，是为了接口失败时仍能用列表里的评论展示主视图，
     * 避免弹窗只有错误文案，维护时也更容易理解 UI 的数据兜底来源。
     */
    fun openReplyDetail(oid: String, type: CommentSourceType, comment: NewComment) {
        _selectedComment.value = comment
        _replyDetail.value = BiliResponse.Loading
        launchTask {
            _replyDetail.value = apiCall {
                api.getCommentReplies(
                    oid = oid,
                    type = type,
                    root = comment.rpid
                )
            }
        }
    }

    fun closeReplyDetail() {
        _selectedComment.value = null
        _replyDetail.value = BiliResponse.Wait
    }

    fun clearPublishMessage() {
        _publishState.update { state ->
            state.copy(message = null, messageScope = null, messageRoot = null)
        }
    }

    fun dismissLoginRequiredDialog() {
        _showLoginRequiredDialog.value = false
    }

    /**
     * 发布根评论。
     *
     * 成功后只把接口返回的新评论插到本地附加列表的第一位，不刷新 PagingSource。
     * 主列表 UI 会把这个附加列表渲染在分页数据之前，从而实现“最新一条在最顶部”。
     */
    fun publishRootComment(
        oid: String,
        type: CommentSourceType,
        message: String,
        onSuccess: () -> Unit = {},
    ) {
        if (!LoginMapper.isLogin()) {
            showLoginRequiredDialog()
            return
        }
        val checkedMessage = validateMessage(message, CommentPublishMessageScope.Root) ?: return
        _publishState.update {
            it.copy(
                isPublishingRoot = true,
                message = null,
                messageScope = null,
                messageRoot = null
            )
        }
        launchTask {
            when (val response = apiCall {
                api.publishComment(
                    oid = oid,
                    type = type,
                    message = checkedMessage,
                )
            }) {
                is BiliResponse.SuccessOrNull -> {
                    val newComment = response.getPublishedCommentOrNull()
                    if (response.code == 0 && newComment != null) {
                        val key = CommentListKey(oid, type)
                        _publishedRootCommentsByList.update { commentsByList ->
                            val oldComments = commentsByList[key].orEmpty()
                            commentsByList + (key to oldComments.insertAtTopIfAbsent(newComment))
                        }
                        _publishState.update { it.copy(isPublishingRoot = false) }
                        onSuccess()
                    } else {
                        _publishState.update {
                            it.copy(
                                isPublishingRoot = false,
                                message = response.toPublishMessage("评论发布失败"),
                                messageScope = CommentPublishMessageScope.Root,
                                messageRoot = null
                            )
                        }
                    }
                }

                is BiliResponse.Error -> {
                    _publishState.update {
                        it.copy(
                            isPublishingRoot = false,
                            message = response.msg.ifBlank { "评论发布失败" },
                            messageScope = CommentPublishMessageScope.Root,
                            messageRoot = null
                        )
                    }
                }

                else -> {
                    _publishState.update {
                        it.copy(
                            isPublishingRoot = false,
                            message = "评论发布失败",
                            messageScope = CommentPublishMessageScope.Root,
                            messageRoot = null
                        )
                    }
                }
            }
        }
    }

    /**
     * 发布回复评论。
     *
     * [root] 始终是主评论；[parent] 是当前回复目标。回复主评论时两者 rpid 相同，
     * 回复二级评论时 parent 使用二级评论的 rpid。
     */
    fun publishReplyComment(
        oid: String,
        type: CommentSourceType,
        root: NewComment,
        parent: NewComment,
        message: String,
        onSuccess: () -> Unit = {},
    ) {
        if (!LoginMapper.isLogin()) {
            showLoginRequiredDialog()
            return
        }
        val checkedMessage = validateMessage(
            message = message,
            scope = CommentPublishMessageScope.Reply,
            rootRpid = root.rpid
        ) ?: return
        _publishState.update {
            it.copy(
                publishingReplyRoot = root.rpid,
                message = null,
                messageScope = null,
                messageRoot = null
            )
        }
        launchTask {
            when (val response = apiCall {
                api.publishComment(
                    oid = oid,
                    type = type,
                    message = checkedMessage,
                    root = root.rpid,
                    parent = parent.rpid
                )
            }) {
                is BiliResponse.SuccessOrNull -> {
                    val newReply = response.getPublishedCommentOrNull()
                    if (response.code == 0 && newReply != null) {
                        _publishedRepliesByRoot.update { current ->
                            val oldReplies = current[root.rpid].orEmpty()
                            current + (root.rpid to oldReplies.appendIfAbsent(newReply))
                        }
                        // selectedComment 是详情弹窗失败时的兜底数据，这里同步计数让兜底视图也能保持一致。
                        _selectedComment.update { selected ->
                            if (selected?.rpid == root.rpid) {
                                selected.copy(rcount = selected.rcount + 1)
                            } else {
                                selected
                            }
                        }
                        _publishState.update { it.copy(publishingReplyRoot = null) }
                        onSuccess()
                    } else {
                        _publishState.update {
                            it.copy(
                                publishingReplyRoot = null,
                                message = response.toPublishMessage("回复发布失败"),
                                messageScope = CommentPublishMessageScope.Reply,
                                messageRoot = root.rpid
                            )
                        }
                    }
                }

                is BiliResponse.Error -> {
                    _publishState.update {
                        it.copy(
                            publishingReplyRoot = null,
                            message = response.msg.ifBlank { "回复发布失败" },
                            messageScope = CommentPublishMessageScope.Reply,
                            messageRoot = root.rpid
                        )
                    }
                }

                else -> {
                    _publishState.update {
                        it.copy(
                            publishingReplyRoot = null,
                            message = "回复发布失败",
                            messageScope = CommentPublishMessageScope.Reply,
                            messageRoot = root.rpid
                        )
                    }
                }
            }
        }
    }

    private fun validateMessage(
        message: String,
        scope: CommentPublishMessageScope,
        rootRpid: Long? = null,
    ): String? {
        val checkedMessage = message.trim()
        val errorMessage = when {
            checkedMessage.isEmpty() -> "评论内容不能为空"
            checkedMessage.length > 1000 -> "评论内容不能超过1000字"
            else -> null
        }
        if (errorMessage != null) {
            _publishState.update {
                it.copy(
                    message = errorMessage,
                    messageScope = scope,
                    messageRoot = rootRpid
                )
            }
            return null
        }
        return checkedMessage
    }

    private fun showLoginRequiredDialog() {
        clearPublishMessage()
        _showLoginRequiredDialog.value = true
    }


    private fun BiliResponse.SuccessOrNull<PublishCommentResult>.getPublishedCommentOrNull(): NewComment? {
        return data?.reply
    }

    private fun BiliResponse.SuccessOrNull<PublishCommentResult>.toPublishMessage(defaultMessage: String): String {
        return data?.successToast
            ?.takeIf { toast -> toast.isNotBlank() }
            ?: message.takeIf { responseMessage -> responseMessage.isNotBlank() && responseMessage != "0" }
            ?: defaultMessage
    }

    private fun List<NewComment>.insertAtTopIfAbsent(comment: NewComment): List<NewComment> {
        if (any { item -> item.rpid == comment.rpid }) {
            return this
        }
        return listOf(comment) + this
    }

    private fun List<NewComment>.appendIfAbsent(comment: NewComment): List<NewComment> {
        if (any { item -> item.rpid == comment.rpid }) {
            return this
        }
        return this + comment
    }

}
