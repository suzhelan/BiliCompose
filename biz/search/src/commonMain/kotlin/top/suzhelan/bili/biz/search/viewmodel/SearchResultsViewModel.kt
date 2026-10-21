package top.suzhelan.bili.biz.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.search.api.SearchApi
import top.suzhelan.bili.biz.search.data.SearchPagingSource
import top.suzhelan.bili.biz.search.entity.SearchPage
import top.suzhelan.bili.biz.search.entity.SearchUser
import top.suzhelan.bili.biz.search.entity.SearchVideo
import top.suzhelan.bili.biz.search.entity.UserOrder
import top.suzhelan.bili.biz.search.entity.UserSearchSpec
import top.suzhelan.bili.biz.search.entity.VideoDuration
import top.suzhelan.bili.biz.search.entity.VideoOrder
import top.suzhelan.bili.biz.search.entity.VideoSearchSpec

class SearchResultsViewModel(
    keyword: String,
    private val loadVideos: suspend (VideoSearchSpec, Int) -> SearchPage<SearchVideo> = SearchApi::videos,
    private val loadUsers: suspend (UserSearchSpec, Int) -> SearchPage<SearchUser> = SearchApi::users,
) : ViewModel() {
    private val mutableVideoSpec = MutableStateFlow(VideoSearchSpec(keyword.trim()))
    val videoSpec = mutableVideoSpec.asStateFlow()
    private val mutableUserSpec = MutableStateFlow(UserSearchSpec(keyword.trim()))
    val userSpec = mutableUserSpec.asStateFlow()

    private val config = PagingConfig(
        pageSize = 20,
        initialLoadSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = false
    )
    private var videoPaging: Pair<VideoSearchSpec, Flow<PagingData<SearchVideo>>>? = null
    private var userPaging: Pair<UserSearchSpec, Flow<PagingData<SearchUser>>>? = null
    private var videoScope: CoroutineScope? = null
    private var userScope: CoroutineScope? = null

    // 每种类型只保留当前查询。新筛选拿到独立的流，不会回放上一筛选的缓存结果。
    fun videoResults(spec: VideoSearchSpec): Flow<PagingData<SearchVideo>> {
        videoPaging?.takeIf { it.first == spec }?.let { return it.second }
        videoScope?.cancel()
        val scope = pagingScope().also { videoScope = it }
        return Pager(config) {
            SearchPagingSource(itemKey = { video: SearchVideo -> video.aid }) { page ->
                loadVideos(
                    spec,
                    page
                )
            }
        }
            .flow.cachedIn(scope).also { videoPaging = spec to it }
    }

    fun userResults(spec: UserSearchSpec): Flow<PagingData<SearchUser>> {
        userPaging?.takeIf { it.first == spec }?.let { return it.second }
        userScope?.cancel()
        val scope = pagingScope().also { userScope = it }
        return Pager(config) {
            SearchPagingSource(itemKey = { user: SearchUser -> user.mid }) { page ->
                loadUsers(
                    spec,
                    page
                )
            }
        }
            .flow.cachedIn(scope).also { userPaging = spec to it }
    }

    private fun pagingScope() = CoroutineScope(
        viewModelScope.coroutineContext + SupervisorJob(viewModelScope.coroutineContext[Job])
    )

    fun setVideoOrder(order: VideoOrder) {
        mutableVideoSpec.value = mutableVideoSpec.value.copy(order = order)
    }

    fun setDuration(duration: VideoDuration) {
        mutableVideoSpec.value = mutableVideoSpec.value.copy(duration = duration)
    }

    fun setUserOrder(order: UserOrder) {
        mutableUserSpec.value = mutableUserSpec.value.copy(order = order)
    }
}
