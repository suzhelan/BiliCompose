package top.sacz.bili.biz.user.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.launch
import top.sacz.bili.biz.user.api.RelationApi
import top.sacz.bili.biz.user.data.FollowListDataSource
import top.sacz.bili.biz.user.entity.RelationTags
import top.sacz.bili.shared.common.base.BaseViewModel

class FollowListViewModel : BaseViewModel() {

    private val api = RelationApi()

    private val _tags = mutableStateListOf(
        RelationTags(
            count = 0,
            name = "全部",
            tagid = -20,
            tip = ""
        )
    )
    val tags: List<RelationTags> = _tags

    fun queryTags() = viewModelScope.launch {
        _tags.addAll(api.queryTags().data)
    }

    fun getFollowListFlow(tagId: Int) = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,//提前多少页开始预加载
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            FollowListDataSource(tagId)
        }
    ).flow.cachedIn(viewModelScope)

}