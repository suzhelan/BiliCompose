package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.Flow
import top.suzhelan.bili.biz.user.data.LikeVideoListDataSource
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.shared.common.base.BaseViewModel

class MoreLikeViewModel : BaseViewModel() {
    private val moreLikeVideoFlows = mutableMapOf<Long, Flow<PagingData<LikeVideo>>>()

    fun getMoreLikeVideoFlow(mid: Long): Flow<PagingData<LikeVideo>> =
        moreLikeVideoFlows.getOrPut(mid) {
            Pager(
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
}
