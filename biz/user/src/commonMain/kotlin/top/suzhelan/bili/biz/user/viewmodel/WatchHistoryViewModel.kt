package top.suzhelan.bili.biz.user.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.data.WatchHistoryPagingSource
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.shared.common.base.BaseViewModel

class WatchHistoryViewModel : BaseViewModel() {
    private val api = UserApi()

    val history = Pager(
        config = PagingConfig(pageSize = 20, prefetchDistance = 5),
        pagingSourceFactory = { WatchHistoryPagingSource() },
    ).flow.cachedIn(viewModelScope)

    private val selectedItems = mutableStateMapOf<String, WatchHistoryPage.Item>()
    val selectedCount: Int get() = selectedItems.size
    var isDeleting by mutableStateOf(false)
        private set

    fun isSelected(item: WatchHistoryPage.Item): Boolean =
        selectedItems.containsKey(selectionKey(item))

    fun toggleSelection(item: WatchHistoryPage.Item) {
        val key = selectionKey(item)
        if (selectedItems.containsKey(key)) selectedItems.remove(key) else selectedItems[key] = item
    }

    fun selectAll(items: List<WatchHistoryPage.Item>) {
        items.forEach { selectedItems[selectionKey(it)] = it }
    }

    fun clearSelection() {
        selectedItems.clear()
    }

    fun deleteSelected(onFinished: () -> Unit) = launchTask {
        val items = selectedItems.values.toList()
        if (items.isEmpty() || isDeleting) return@launchTask
        isDeleting = true
        val failures = mutableListOf<String>()
        items.forEach { item ->
            try {
                val result = api.deleteWatchHistory(deleteKid(item))
                if (result.code == 0) {
                    selectedItems.remove(selectionKey(item))
                } else {
                    failures += result.message
                }
            } catch (exception: Exception) {
                failures += (exception.message ?: "删除历史记录失败")
            }
        }
        isDeleting = false
        onFinished()
        if (failures.isNotEmpty()) {
            showMessageDialog(message = failures.first())
        }
    }

    private fun selectionKey(item: WatchHistoryPage.Item) =
        "${item.history.business}_${item.kid}_${item.viewAt}"

    private fun deleteKid(item: WatchHistoryPage.Item) = "${item.history.business}_${item.kid}"
}
