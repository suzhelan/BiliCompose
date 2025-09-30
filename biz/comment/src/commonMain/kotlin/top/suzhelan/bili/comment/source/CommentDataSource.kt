package top.suzhelan.bili.comment.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.Comment
import top.suzhelan.bili.comment.entity.CommentSourceType


class CommentDataSource(
    private val oid: String,
    private val type: CommentSourceType
) : PagingSource<Int, Comment>() {

    private val firstPageIndex = 1

    private val api = CommentApi()
    override fun getRefreshKey(state: PagingState<Int, Comment>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Comment> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val response = api.getCommentList(oid, type, currentKey)
            val popularListItem = response.data
            val items: List<Comment> = popularListItem.replies ?: emptyList()
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = if (items.isEmpty()) null else currentKey + 1
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}