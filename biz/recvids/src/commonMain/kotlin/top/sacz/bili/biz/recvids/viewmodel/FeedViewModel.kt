package top.sacz.bili.biz.recvids.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.sacz.bili.biz.recvids.data.FeedVideoDataSource

class FeedViewModel : ViewModel() {

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
