package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.suzhelan.bili.biz.user.data.LikeVideoListDataSource
import top.suzhelan.bili.shared.common.base.BaseViewModel

class MoreLikeViewModel : BaseViewModel() {
    fun getMoreLikeVideoFlow(mid: Long) = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,//提前多少页开始预加载
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            LikeVideoListDataSource(mid)
        }
    ).flow.cachedIn(viewModelScope)
}