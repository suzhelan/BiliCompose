package top.suzhelan.bili.biz.biliplayer.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import top.suzhelan.bili.biz.biliplayer.api.VideoInfoApi
import top.suzhelan.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.suzhelan.bili.shared.common.logger.error

/**
 * 推荐视频列表的分页数据源
 */
class RecommendedVideoPagingSource(
    private val aid: Long,
) : PagingSource<Int, RecommendedVideoByVideo.Item>() {

    private val firstPageIndex = 1
    private val api = VideoInfoApi()

    override fun getRefreshKey(state: PagingState<Int, RecommendedVideoByVideo.Item>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, RecommendedVideoByVideo.Item> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            val response = api.getRecommendedVideosByVideo(aid)
            // 提取推荐视频列表
            val items: List<RecommendedVideoByVideo.Item> = response.data.items
            if (items.isEmpty()) {
                return LoadResult.Page(
                    data = emptyList(),
                    prevKey = if (currentKey > firstPageIndex) currentKey - 1 else null,
                    nextKey = null
                )
            }
            LoadResult.Page(
                data = items,
                prevKey = null,
                //Bil的数据是无限的 ,所以nextKey总是为currentKey + 1 ,返回null表示没有更多页了
                nextKey = if (currentKey == Int.MAX_VALUE) null else currentKey + 1
            )
        } catch (e: Exception) {
            e.error()
            LoadResult.Error(e)
        }
    }
}