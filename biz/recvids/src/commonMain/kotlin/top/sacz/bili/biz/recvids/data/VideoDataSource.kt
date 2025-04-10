package top.sacz.bili.biz.recvids.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.sacz.bili.biz.recvids.api.FeedApi
import top.sacz.bili.biz.recvids.model.Video

class VideoDataSource : PagingSource<Int, Video>() {

    private val firstPageIndex = 1

    private val api: FeedApi = FeedApi()
    override fun getRefreshKey(state: PagingState<Int, Video>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Video> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            if (currentKey != 1 && params is LoadParams.Refresh) {
                return LoadResult.Page(emptyList(), prevKey = null, nextKey = 1)
            }
            val response = api.getFeed()
            val items = response.data.items
            LoadResult.Page(
                data = items,
                prevKey = null/*if (currentKey == firstPageIndex) null else currentKey - 1*/,
                //我们的数据是无限的 所以填入Int.MAX_VALUE,返回null 表示仍然会继续加载更多,如果数据有限 那把Int.MAX_VALUE换成page总数
                nextKey = if (currentKey == Int.MAX_VALUE) null else currentKey + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}