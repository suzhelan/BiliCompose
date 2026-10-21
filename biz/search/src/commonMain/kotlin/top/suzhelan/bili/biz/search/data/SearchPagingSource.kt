package top.suzhelan.bili.biz.search.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import kotlinx.serialization.SerializationException
import top.suzhelan.bili.biz.search.entity.SearchPage

class SearchPagingSource<T : Any>(
    private val itemKey: (T) -> Any = { it },
    private val loadPage: suspend (Int) -> SearchPage<T>,
) : PagingSource<Int, T>() {
    private val seenKeys = mutableSetOf<Any>()

    // 新关键词、筛选和手动重试都从第一页开始，返回页面则复用已缓存的 PagingData。
    override fun getRefreshKey(state: PagingState<Int, T>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, T> {
        return try {
            val page = params.key ?: 1
            val response = loadPage(page)
            if (response.page != page || response.pages < 0 || response.count < 0 ||
                (response.result == null && response.count > 0)
            ) {
                throw SerializationException("搜索结果格式异常，请重试")
            }
            val rawItems = response.result.orEmpty()
            val items = rawItems.filter { seenKeys.add(itemKey(it)) }
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = if (rawItems.isEmpty() || page >= response.pages) null else page + 1,
            )
        } catch (cancelled: CancellationException) {
            throw cancelled
        } catch (exception: Exception) {
            LoadResult.Error(exception)
        }
    }
}
