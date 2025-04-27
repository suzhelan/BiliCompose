package top.sacz.bili.biz.recvids.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.sacz.bili.biz.recvids.data.PopularDataSource

class PopularViewModel : ViewModel() {
    val popularListFlow = Pager(
        config = PagingConfig(
            pageSize = 20,
            prefetchDistance = 5,//提前多少页开始预加载
            enablePlaceholders = false
        ),
        pagingSourceFactory = {
            PopularDataSource()
        }
    ).flow.cachedIn(viewModelScope)
}