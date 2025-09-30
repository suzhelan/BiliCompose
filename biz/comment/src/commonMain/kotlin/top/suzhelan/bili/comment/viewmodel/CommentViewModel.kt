package top.suzhelan.bili.comment.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.source.CommentDataSource
import top.suzhelan.bili.shared.common.base.BaseViewModel

class CommentViewModel : BaseViewModel() {

    fun getCommentList(oid: String, type: CommentSourceType) = Pager(
            config = PagingConfig(
                pageSize = 10,
                prefetchDistance = 5,//提前多少页开始预加载
                enablePlaceholders = false
            ),
            pagingSourceFactory = {
                CommentDataSource(oid, type)
            }
        ).flow.cachedIn(viewModelScope)

}