package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.entity.FollowingUser
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.logger.error


/**
 * 分页常用模板 别的页面可以直接
 */
class FollowListDataSource(
    val tagId: Int,
    private val mid: Long? = null,
) : PagingSource<Int, FollowingUser>() {

    private val firstPageIndex = 1

    private val api = RelationApi()
    override fun getRefreshKey(state: PagingState<Int, FollowingUser>): Int? {
        return state.anchorPosition?.let { position ->
            val page = state.closestPageToPosition(position)
            page?.prevKey?.plus(1) ?: page?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, FollowingUser> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val page = mid?.let { api.queryFollowings(it, currentKey).data }
            val items = page?.list ?: api.queryFollowList(tagId, currentKey).data
            if (items.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                    nextKey = null
                )
            }
            //批量查询关系信息
            val currentMid = LoginMapper.getMid()
            val relationMids = items.map { it.mid }.filter { it != currentMid }
            val relations = if (relationMids.isEmpty()) emptyMap()
            else api.queryRelations(relationMids).data
            val users = items.map { item ->
                // 他人列表返回的是空间主人的关系，按钮必须使用当前登录账号的关系。
                val attribute = if (item.mid == currentMid) 0 else {
                    checkNotNull(relations[item.mid.toString()]) { "关注状态获取失败，请重试" }.attribute
                }
                item.copy(attribute = attribute)
            }
            //返回结果
            LoadResult.Page(
                data = users,
                prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                nextKey = if (items.size < 50 || (page != null && currentKey * 50 >= page.total)) {
                    null
                } else currentKey + 1
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.error()
            LoadResult.Error(e)
        }
    }
}
