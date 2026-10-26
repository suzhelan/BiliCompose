package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.biz.user.api.UserSubmissionApi
import top.suzhelan.bili.biz.user.entity.ProfilePage
import top.suzhelan.bili.biz.user.entity.SubmissionCategory
import top.suzhelan.bili.biz.user.entity.SubmissionOrder
import top.suzhelan.bili.biz.user.entity.SubmissionVideo
import top.suzhelan.bili.biz.user.entity.UserCollection
import top.suzhelan.bili.biz.user.entity.UserCollectionKind
import top.suzhelan.bili.shared.common.base.BaseViewModel

data class ProfileListState<T>(
    val items: List<T> = emptyList(),
    val total: Int = 0,
    val page: Int = 0,
    val categories: List<SubmissionCategory> = emptyList(),
    val hasLoaded: Boolean = false,
    val hasMore: Boolean = false,
    val isRefreshing: Boolean = false,
    val isLoadingMore: Boolean = false,
    val error: String? = null,
    val appendFailed: Boolean = false,
    val notice: String? = null,
)

/** 刷新保留当前快照，切换查询则清空；版本号隔离被取消请求的迟到响应。 */
class ProfileListLoader<T>(private val scope: CoroutineScope, private val itemKey: (T) -> String) {
    private val _state = MutableStateFlow(ProfileListState<T>())
    val state = _state.asStateFlow()
    private var key: Any? = null
    private var fetch: (suspend (Int) -> ProfilePage<T>)? = null
    private var job: Job? = null
    private var version = 0
    private var stale = false

    fun invalidate() {
        stale = true
    }

    fun load(key: Any, force: Boolean = false, fetch: suspend (Int) -> ProfilePage<T>) {
        val sameQuery = this.key == key
        if (sameQuery && (_state.value.isRefreshing || (!force && !stale && _state.value.hasLoaded))) return
        job?.cancel()
        this.key = key
        this.fetch = fetch
        stale = false
        val previous = if (sameQuery) _state.value else ProfileListState()
        val requestVersion = ++version
        _state.value = previous.copy(
            isRefreshing = true,
            isLoadingMore = false,
            error = null,
            appendFailed = false
        )
        job = scope.launch {
            try {
                val result = fetch(1)
                if (version != requestVersion) return@launch
                _state.value = ProfileListState(
                    items = result.items.distinctBy(itemKey), total = result.total, page = 1,
                    categories = result.categories, hasLoaded = true, hasMore = result.hasMore,
                    notice = result.notice,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (version == requestVersion) _state.value = previous.copy(
                    isRefreshing = false,
                    isLoadingMore = false,
                    error = errorMessage(exception),
                    appendFailed = false,
                )
            }
        }
    }

    fun loadMore() {
        val previous = _state.value
        val fetch = fetch ?: return
        if (!previous.hasMore || previous.isRefreshing || previous.isLoadingMore) return
        val requestVersion = ++version
        _state.value = previous.copy(isLoadingMore = true, error = null, appendFailed = false)
        job = scope.launch {
            try {
                val result = fetch(previous.page + 1)
                if (version != requestVersion) return@launch
                val items = (previous.items + result.items).distinctBy(itemKey)
                _state.value = previous.copy(
                    items = items, page = previous.page + 1, total = result.total,
                    hasMore = result.hasMore && items.size > previous.items.size,
                    isLoadingMore = false, error = null, appendFailed = false,
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (version == requestVersion) _state.value = previous.copy(
                    isLoadingMore = false,
                    error = errorMessage(exception),
                    appendFailed = true
                )
            }
        }
    }

    private fun errorMessage(exception: Exception): String =
        if (exception is ApiException && exception.code in listOf(-412, -352, -403)) {
            "请求暂时受限，请稍后重试"
        } else exception.message ?: "加载失败，请重试"
}

class UserSubmissionsViewModel : BaseViewModel() {
    val watchHistory = SubmissionWatchHistoryLoader(viewModelScope)
    val videos = ProfileListLoader<SubmissionVideo>(viewModelScope) { it.key }
    val collections = ProfileListLoader<UserCollection>(viewModelScope) { it.key }
    private val _order = MutableStateFlow(SubmissionOrder.NEWEST)
    val order = _order.asStateFlow()
    private val _tid = MutableStateFlow(0)
    val tid = _tid.asStateFlow()
    private var mid: Long? = null

    fun loadVideos(
        mid: Long,
        order: SubmissionOrder = _order.value,
        tid: Int = _tid.value,
        force: Boolean = false,
    ) {
        if (force) watchHistory.refresh()
        if (this.mid != mid) {
            _order.value = SubmissionOrder.NEWEST
            _tid.value = 0
        } else {
            _order.value = order
            _tid.value = tid
        }
        this.mid = mid
        val selectedOrder = _order.value
        val selectedTid = _tid.value
        videos.load(
            Triple(mid, selectedOrder, selectedTid), force,
            UserSubmissionApi.videoPages(mid, selectedOrder, selectedTid),
        )
    }

    fun loadCollections(mid: Long, force: Boolean = false) = collections.load(mid, force) {
        UserSubmissionApi.collections(mid, it)
    }
}

class UserCollectionVideosViewModel : BaseViewModel() {
    val watchHistory = SubmissionWatchHistoryLoader(viewModelScope)
    val videos = ProfileListLoader<SubmissionVideo>(viewModelScope) { it.key }
    fun load(
        mid: Long,
        id: Long,
        kind: UserCollectionKind,
        reverse: Boolean,
        force: Boolean = false,
    ) {
        if (force) watchHistory.refresh()
        videos.load(listOf(mid, id, kind, reverse), force) {
            UserSubmissionApi.collectionVideos(mid, id, kind, reverse, it)
        }
    }
}
