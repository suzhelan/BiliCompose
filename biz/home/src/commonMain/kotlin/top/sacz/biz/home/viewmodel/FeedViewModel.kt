package top.sacz.biz.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.Response
import top.sacz.biz.home.api.FeedApi
import top.sacz.biz.home.model.VideoList
import kotlin.time.ExperimentalTime

class FeedViewModel : ViewModel() {
    private val _recommendedLevelList = MutableStateFlow<Response<VideoList>>(Response.Loading)
    val recommendedLevelList = _recommendedLevelList.asStateFlow()

    @OptIn(ExperimentalTime::class)
    fun getFeed() = viewModelScope.launch {
        try {
            val feedApi = FeedApi()
            val feed = feedApi.getFeed()
            _recommendedLevelList.value = feed
        } catch (e: Exception) {
            e.printStackTrace()
            _recommendedLevelList.value = Response.Error(0, e.message ?: "")
        }
    }
}
