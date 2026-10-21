package top.suzhelan.bili.biz.search.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.search.api.SearchApi
import top.suzhelan.bili.biz.search.data.SearchHistoryStore
import top.suzhelan.bili.biz.search.entity.SearchSuggestion
import top.suzhelan.bili.biz.search.entity.TrendingWord

data class SuggestionsState(
    val query: String = "",
    val items: List<SearchSuggestion> = emptyList(),
    val loading: Boolean = false,
    val failed: Boolean = false,
)

data class TrendingState(
    val items: List<TrendingWord> = emptyList(),
    val loading: Boolean = true,
    val failed: Boolean = false,
)

class SearchInputViewModel(
    private val loadSuggestions: suspend (String) -> List<SearchSuggestion> = SearchApi::suggestions,
    private val loadTrendingWords: suspend () -> List<TrendingWord> = SearchApi::trending,
    val history: StateFlow<List<String>> = SearchHistoryStore.history,
) : ViewModel() {
    private val mutableSuggestions = MutableStateFlow(SuggestionsState())
    val suggestions = mutableSuggestions.asStateFlow()
    private val mutableTrending = MutableStateFlow(TrendingState())
    val trending = mutableTrending.asStateFlow()
    private var suggestionJob: Job? = null
    private var trendingJob: Job? = null

    init {
        loadTrending()
    }

    fun updateInput(text: String) {
        suggestionJob?.cancel()
        val query = text.trim()
        mutableSuggestions.value = SuggestionsState(query = query, loading = query.isNotEmpty())
        if (query.isEmpty()) return
        suggestionJob = viewModelScope.launch {
            delay(300)
            try {
                val items = loadSuggestions(query)
                if (mutableSuggestions.value.query == query) {
                    mutableSuggestions.value = SuggestionsState(query, items)
                }
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                if (mutableSuggestions.value.query == query) {
                    mutableSuggestions.value = SuggestionsState(query = query, failed = true)
                }
            }
        }
    }

    fun stopSuggestions() {
        suggestionJob?.cancel()
        mutableSuggestions.value = SuggestionsState()
    }

    fun loadTrending() {
        if (trendingJob?.isActive == true) return
        mutableTrending.value = mutableTrending.value.copy(loading = true, failed = false)
        trendingJob = viewModelScope.launch {
            try {
                mutableTrending.value = TrendingState(items = loadTrendingWords(), loading = false)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                mutableTrending.value = mutableTrending.value.copy(loading = false, failed = true)
            }
        }
    }
}
