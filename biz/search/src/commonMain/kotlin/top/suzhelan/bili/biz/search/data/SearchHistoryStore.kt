package top.suzhelan.bili.biz.search.data

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.storage.Storage

object SearchHistoryStore {
    private val storage by lazy { Storage("search") }
    private val mutableHistory by lazy {
        MutableStateFlow(runCatching { storage.getObject<List<String>>("history", emptyList()) }
            .getOrDefault(emptyList()).filter { it.isNotBlank() }.distinct().take(30))
    }
    val history get() = mutableHistory.asStateFlow()

    fun add(keyword: String) {
        val value = keyword.trim()
        if (value.isEmpty()) return
        save((listOf(value) + mutableHistory.value.filterNot { it == value }).take(30))
    }

    fun remove(keyword: String) = save(mutableHistory.value.filterNot { it == keyword })
    fun clear() = save(emptyList())

    private fun save(history: List<String>) {
        storage.putObject("history", history)
        mutableHistory.value = history
    }
}
