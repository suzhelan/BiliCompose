package top.sacz.bili.biz.recvids.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.sacz.bili.biz.recvids.api.PopularApi
import top.sacz.bili.biz.recvids.model.PopularItem

class PopularDataSource : PagingSource<Int, PopularItem>() {

    private val firstPageIndex = 1

    private val api = PopularApi()
    override fun getRefreshKey(state: PagingState<Int, PopularItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, PopularItem> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val response = api.fetchPopular(params.key ?: 1)
            val popularListItem = response.data
            val items: List<PopularItem> = popularListItem.list
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = if (popularListItem.noMore) null else currentKey + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}