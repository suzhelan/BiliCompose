package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.entity.RelationUser
import top.suzhelan.bili.shared.common.logger.error


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
            //关注关注列表
            val response = api.queryFollowList(tagId, params.key ?: 1)
            val items: List<RelationUser> = response.data
            if (items.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                    nextKey = null
                )
            }
            //批量查询关系信息
            val relations = api.queryRelations(items.map { it.mid })
            //循环获取关系信息
            for (item in items) {
                item.attribute = relations.data[item.mid.toString()]!!.attribute
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