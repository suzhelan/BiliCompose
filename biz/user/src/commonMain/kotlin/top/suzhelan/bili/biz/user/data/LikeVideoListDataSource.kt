package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.biz.user.entity.LikeVideoList
import top.suzhelan.bili.shared.common.logger.error


/**
 * 分页常用模板 别的页面可以直接
 */
class LikeVideoListDataSource(
    private val mid: Long,
    private val loadPage: suspend (Long, Int, Int) -> BiliResponse.Success<LikeVideoList>,
    private val onRefreshingChanged: (Boolean) -> Unit = {},
) : PagingSource<Int, LikeVideo>() {

    private val firstPageIndex = 1

    var hasCompletedRefresh = false
        private set

    // 新点赞在第一页，返回页面、切换 Tab 和下拉刷新都从第一页重新读取。
    override fun getRefreshKey(state: PagingState<Int, LikeVideo>): Int? = null

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LikeVideo> {
        if (params is LoadParams.Refresh) onRefreshingChanged(true)
        return try {
            val currentKey = params.key ?: firstPageIndex
            val response = loadPage(mid, currentKey, PAGE_SIZE)
            check(response.code == 0) { response.message }
            val items: List<LikeVideo> = response.data.item
            if (items.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                    nextKey = null
                )
            }
            //返回结果
            LoadResult.Page(
                data = items,
                prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                nextKey = currentKey + 1
            )
        } catch (exception: CancellationException) {
            throw exception
        } catch (e: Exception) {
            e.error()
            LoadResult.Error(e)
        } finally {
            if (params is LoadParams.Refresh) {
                hasCompletedRefresh = true
                onRefreshingChanged(false)
            }
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
