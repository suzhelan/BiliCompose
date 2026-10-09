package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.user.api.WatchLaterApi
import top.suzhelan.bili.biz.user.entity.WatchLaterVideo
import top.suzhelan.bili.shared.common.base.BaseViewModel

data class WatchLaterUiState(
    val videos: List<WatchLaterVideo> = emptyList(),
    val isLoading: Boolean = false,
    val removingAid: Long? = null,
    val error: String? = null,
)

class WatchLaterViewModel : BaseViewModel() {
    private val api = WatchLaterApi()
    private val _state = MutableStateFlow(WatchLaterUiState())
    val state = _state.asStateFlow()

    fun load() = launchTask {
        if (_state.value.isLoading || _state.value.removingAid != null) return@launchTask
        _state.value = _state.value.copy(isLoading = true, error = null)
        try {
            val response = api.getList()
            if (response.code != 0) error(response.message)
            val data = response.data ?: error("稍后再看列表数据为空")
            _state.value = _state.value.copy(videos = data.list.orEmpty())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            _state.value = _state.value.copy(error = exception.message ?: "稍后再看加载失败")
        } finally {
            _state.value = _state.value.copy(isLoading = false)
        }
    }

    fun remove(aid: Long) = launchTask {
        if (_state.value.isLoading || _state.value.removingAid != null) return@launchTask
        _state.value = _state.value.copy(removingAid = aid)
        try {
            val response = api.remove(aid)
            if (response.code != 0) error(response.message)
            _state.value =
                _state.value.copy(videos = _state.value.videos.filterNot { it.aid == aid })
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            showMessageDialog(message = exception.message ?: "移除失败，请重试")
        } finally {
            _state.value = _state.value.copy(removingAid = null)
        }
    }
}
