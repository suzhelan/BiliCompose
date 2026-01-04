package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.shared.common.logger.error


/**
 * 分页常用模板 别的页面可以直接
 */
class LikeVideoListDataSource(
    private val mid: Long,
) : PagingSource<Int, LikeVideo>() {

    private val firstPageIndex = 1

    private val api = UserApi()
    override fun getRefreshKey(state: PagingState<Int, LikeVideo>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, LikeVideo> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            //关注关注列表
            val response = api.getUserLikeVideoList(mid, params.key ?: 1, 20)
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
                nextKey = if (items.isNotEmpty()) currentKey + 1 else null
            )
        } catch (e: Exception) {
            e.error()
            LoadResult.Error(e)
        }
    }
}