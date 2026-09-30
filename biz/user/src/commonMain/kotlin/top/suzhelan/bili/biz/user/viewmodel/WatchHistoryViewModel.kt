package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.suzhelan.bili.biz.user.data.WatchHistoryPagingSource
import top.suzhelan.bili.shared.common.base.BaseViewModel

class WatchHistoryViewModel : BaseViewModel() {
    val history = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { WatchHistoryPagingSource() },
    ).flow.cachedIn(viewModelScope)
}
