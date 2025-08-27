package top.sacz.bili.biz.recvids.data

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.serialization.json.jsonPrimitive
import top.sacz.bili.api.HttpJsonDecoder
import top.sacz.bili.biz.recvids.api.FeedApi
import top.sacz.bili.biz.recvids.config.BaseCoverSerializer
import top.sacz.bili.biz.recvids.entity.BaseCoverItem
import top.sacz.bili.biz.recvids.entity.SmallCoverV2Item
import top.sacz.bili.biz.recvids.entity.targetCardType
import top.sacz.bili.shared.common.logger.LogUtils

class FeedVideoDataSource : PagingSource<Int, BaseCoverItem>() {

    private val firstPageIndex = 1

    private val api: FeedApi = FeedApi()
    override fun getRefreshKey(state: PagingState<Int, BaseCoverItem>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, BaseCoverItem> {
        return try {
            val currentKey = params.key ?: firstPageIndex
            if (currentKey != 1 && params is LoadParams.Refresh) {
                return LoadResult.Page(emptyList(), prevKey = null, nextKey = 1)
            }
            val response = api.getFeed()
            //对响应体的json进行解码自定义反序列化 将JSONObject转的BaseCoverItem
            val items: List<BaseCoverItem> = response.data.items
                .filter {
                    //只得到标准的视频数据
                    it["card_type"]?.jsonPrimitive?.content == SmallCoverV2Item.targetCardType
                }.mapNotNull {
                    runCatching {
                        HttpJsonDecoder.decodeFromJsonElement(BaseCoverSerializer, it)
                    }.onFailure { e ->
                        LogUtils.e("主页视频获取失败", HttpJsonDecoder.encodeToString(it), e)
                    }.getOrNull()
                }
            LoadResult.Page(
                data = items,
                prevKey = null/*if (currentKey == firstPageIndex) null else currentKey - 1*/,
                //Bil的数据是无限的 ,所以nextKey总是为currentKey + 1 ,返回null表示没有更多页了
                nextKey = if (currentKey == Int.MAX_VALUE) null else currentKey + 1
            )
        } catch (e: Exception) {
            LogUtils.e("主页视频获取失败", e)
            LoadResult.Error(e)
        }
    }
}