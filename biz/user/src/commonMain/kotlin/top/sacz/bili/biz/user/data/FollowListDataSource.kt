package top.sacz.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.sacz.bili.biz.user.api.RelationApi
import top.sacz.bili.biz.user.entity.RelationUser


/**
 * 分页常用模板 别的页面可以直接
 */
class FollowListDataSource(
    val tagId: Int,
) : PagingSource<Int, RelationUser>() {

    private val firstPageIndex = 1

    private val api = RelationApi()
    override fun getRefreshKey(state: PagingState<Int, RelationUser>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RelationUser> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val response = api.queryFollowList(tagId, params.key ?: 1)
            val items: List<RelationUser> = response.data
            LoadResult.Page(
                data = items,
                prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                nextKey = if (items.isNotEmpty()) currentKey + 1 else null
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}