package top.sacz.bili.biz.recvids.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.sacz.bili.biz.recvids.data.FeedVideoDataSource
import top.sacz.bili.shared.common.base.BaseViewModel

class FeedViewModel : BaseViewModel() {

    val recommendedListFlow = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,//提前多少页开始预加载
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            FeedVideoDataSource()
        }
    ).flow.cachedIn(viewModelScope)


}
