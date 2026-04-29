package top.suzhelan.bili.comment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.CommentReplyPage
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.entity.NewComment
import top.suzhelan.bili.comment.source.CommentDataSource
import top.suzhelan.bili.shared.common.base.BaseViewModel

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

    private val commentListFlows = mutableMapOf<CommentListKey, Flow<PagingData<NewComment>>>()

    fun getCommentList(oid: String, type: CommentSourceType): Flow<PagingData<NewComment>> =
        commentListFlows.getOrPut(CommentListKey(oid, type)) {
            Pager(
                config = PagingConfig(
                    pageSize = 10,
                    prefetchDistance = 3,//提前多少页开始预加载
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    CommentDataSource(oid, type)
                }
            ).flow.cachedIn(viewModelScope)
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

}
