package top.suzhelan.bili.comment.source

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.entity.NewComment

class CommentApiException(code: Int, message: String) : Exception(
    "评论加载失败：code=$code, message=$message"
)

class CommentDataSource(
    private val oid: String,
    private val type: CommentSourceType,
) : PagingSource<Int, NewComment>() {

    private val firstPageIndex = 1
    private val pageOffsetMap = mutableMapOf<Int, String?>(firstPageIndex to null)

    private val api = CommentApi()
    override fun getRefreshKey(state: PagingState<Int, NewComment>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewComment> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val currentOffset = pageOffsetMap[currentKey]
            val response = api.getCommentLazyList(oid, type, currentOffset)
            if (response.code != 0) {
                return LoadResult.Error(
                    CommentApiException(
                        response.code,
                        response.message
                    )
                )
            }
            val commentPage = response.data ?: return LoadResult.Error(
                CommentApiException(
                    response.code,
                    response.message
                )
            )
            val items: List<NewComment> = commentPage.replies ?: emptyList()
            val nextOffset = commentPage.cursor.paginationReply
                ?.nextOffset
                ?.takeIf { nextOffset ->
                    !commentPage.cursor.isEnd &&
                            items.isNotEmpty() &&
                            nextOffset.isNotBlank() &&
                            nextOffset != currentOffset
                }
            val nextKey = nextOffset?.let {
                (currentKey + 1).also { pageOffsetMap[it] = nextOffset }
            }
            LoadResult.Page(
                data = items,
                prevKey = null,
                nextKey = nextKey
            )
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
