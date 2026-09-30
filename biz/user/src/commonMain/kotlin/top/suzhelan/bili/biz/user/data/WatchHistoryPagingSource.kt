package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.shared.common.logger.error

class WatchHistoryPagingSource :
    PagingSource<WatchHistoryPagingSource.Key, WatchHistoryPage.Item>() {
    data class Key(
        val max: Long,
        val business: String,
        val viewAt: Long,
    )

    private val api = UserApi()

    override fun getRefreshKey(state: PagingState<Key, WatchHistoryPage.Item>): Key? = null

    override suspend fun load(params: LoadParams<Key>): LoadResult<Key, WatchHistoryPage.Item> =
        try {
            val key = params.key
            val page = api.getWatchHistory(
                max = key?.max,
                business = key?.business,
                viewAt = key?.viewAt,
                pageSize = params.loadSize.coerceAtMost(30),
            ).data
            val nextKey = page.cursor
                .takeIf { page.list.isNotEmpty() && it.max != 0L }
                ?.let { Key(it.max, it.business, it.viewAt) }

            LoadResult.Page(
                data = page.list,
                prevKey = null,
                nextKey = nextKey,
            )
        } catch (exception: Exception) {
            exception.error()
            LoadResult.Error(exception)
        }
}
