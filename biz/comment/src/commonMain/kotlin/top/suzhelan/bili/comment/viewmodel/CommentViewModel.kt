package top.suzhelan.bili.comment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.source.CommentDataSource
import top.suzhelan.bili.shared.common.base.BaseViewModel

class CommentViewModel : BaseViewModel() {

    private val commentApi = CommentApi()

    private val _sendCommentResult = MutableStateFlow<BiliResponse<*>>(BiliResponse.Wait)
    val sendCommentResult: StateFlow<BiliResponse<*>> = _sendCommentResult

    fun getCommentList(oid: String, type: CommentSourceType) = Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentDataSource(oid, type)
            }
        ).flow.cachedIn(viewModelScope)

    /**
     * 发送评论
     * @param oid 视频ID
     * @param type 评论类型
     * @param message 评论内容
     * @param root 根评论ID(可选,用于回复)
     * @param parent 父评论ID(可选,用于回复子评论)
     */
    fun sendComment(
        oid: Long,
        type: CommentSourceType,
        message: String,
        root: Long? = null,
        parent: Long? = null
    ) {
        viewModelScope.launch {
            _sendCommentResult.value = BiliResponse.Loading
            try {
                val result = commentApi.addComment(
                    oid = oid,
                    type = type,
                    message = message,
                    root = root,
                    parent = parent
                )
                _sendCommentResult.value = result
                if (result.isSuccess()) {
                    refreshComments()
                }
            } catch (e: Exception) {
                _sendCommentResult.value = BiliResponse.Error(
                    code = -1,
                    msg = e.message ?: "发送评论失败"
                )
            }
        }
    }

    private fun refreshComments() {
        viewModelScope.launch {
        }
    }

    fun clearSendResult() {
        _sendCommentResult.value = BiliResponse.Wait
    }
}