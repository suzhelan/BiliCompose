package top.suzhelan.bili.biz.user.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.CancellationException
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.RelationApi.Companion.LIST_PAGE_SIZE
import top.suzhelan.bili.biz.user.entity.FollowingUser
import top.suzhelan.bili.biz.user.entity.RelationListUser
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.logger.error

data class RelationPageKey(
    val number: Int,
    val offset: String? = null,
    val loadedCount: Int = 0,
)

class FollowListDataSource(
    private val tagId: Int,
    private val mid: Long?,
    private val fans: Boolean,
    private val api: RelationApi,
) : PagingSource<RelationPageKey, RelationListUser>() {

    private var previousAttributes = emptyMap<Long, Int>()

    override fun getRefreshKey(state: PagingState<RelationPageKey, RelationListUser>): RelationPageKey? {
        // 新一轮分页缺少关系结果时，保留上一轮已确认的按钮状态。
        previousAttributes = state.pages.asSequence()
            .flatMap { it.data.asSequence() }
            .mapNotNull { item -> item.attribute?.let { item.user.mid to it } }
            .toMap()
        // 粉丝偏移量取自上一页，刷新时从首屏重新获取。
        if (fans) return null
        val position = state.anchorPosition ?: return null
        val page = state.closestPageToPosition(position)
        val number = page?.prevKey?.number?.plus(1) ?: page?.nextKey?.number?.minus(1)
        return number?.let { RelationPageKey(it) }
    }

    override suspend fun load(
        params: LoadParams<RelationPageKey>,
    ): LoadResult<RelationPageKey, RelationListUser> {
        return try {
            val key = params.key ?: RelationPageKey(1)
            val fansPage =
                if (fans) api.queryFans(requireNotNull(mid), key.number, key.offset).data else null
            val followingsPage =
                if (!fans && mid != null) api.queryFollowings(mid, key.number).data else null
            val items = fansPage?.list ?: followingsPage?.list ?: api.queryFollowList(
                tagId,
                key.number
            ).data
            if (items.isEmpty() && key.number > 1 && params is LoadParams.Refresh) {
                return load(LoadParams.Refresh(null, params.loadSize, params.placeholdersEnabled))
            }
            val total = fansPage?.total ?: followingsPage?.total
            if (key.number == 1 && items.isEmpty() && total != null && total > 0) {
                val listName = if (fans) "粉丝" else "关注"
                return LoadResult.Error(IllegalStateException("暂时无法查看该${listName}列表"))
            }

            val offset = fansPage?.offset?.takeIf { it.isNotBlank() && it != "rcmd" }
            val loadedCount = key.loadedCount + items.size
            val nextKey = when {
                items.isEmpty() -> null
                fansPage != null -> {
                    val hasMoreFans =
                        loadedCount < fansPage.total && (offset == null || offset != key.offset)
                    if (hasMoreFans) RelationPageKey(key.number + 1, offset, loadedCount) else null
                }

                items.size < LIST_PAGE_SIZE -> null
                total != null && key.number * LIST_PAGE_SIZE >= total -> null
                else -> RelationPageKey(key.number + 1)
            }
            LoadResult.Page(
                data = withCurrentAccountRelations(items),
                prevKey = if (!fans && key.number > 1) RelationPageKey(key.number - 1) else null,
                nextKey = nextKey,
            )
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.error()
            LoadResult.Error(e)
        }
    }

    private suspend fun withCurrentAccountRelations(items: List<FollowingUser>): List<RelationListUser> {
        if (items.isEmpty()) return emptyList()
        val currentMid = LoginMapper.getMid()
        val users = items.distinctBy { it.mid }
        val mids = users.map { it.mid }.filter { it != currentMid }
        val relations = try {
            if (mids.isEmpty()) emptyMap() else api.queryRelations(mids).data
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            e.error()
            emptyMap()
        }
        // 列表里的关系可能属于空间主人。批量失败或缺少结果时，由可见行自动补查。
        return users.map { item ->
            val attribute = relations[item.mid.toString()]?.attribute
            RelationListUser(
                user = item,
                attribute = attribute ?: previousAttributes[item.mid],
                needsRelationLookup = attribute == null && item.mid != currentMid,
            )
        }
    }
}
