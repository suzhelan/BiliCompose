package top.suzhelan.bili.biz.search.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.decodeFromJsonElement
import top.suzhelan.bili.api.BiliWebClient
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.biz.search.entity.SearchPage
import top.suzhelan.bili.biz.search.entity.SearchSuggestion
import top.suzhelan.bili.biz.search.entity.SearchUser
import top.suzhelan.bili.biz.search.entity.SearchVideo
import top.suzhelan.bili.biz.search.entity.SuggestionResult
import top.suzhelan.bili.biz.search.entity.TrendingData
import top.suzhelan.bili.biz.search.entity.TrendingWord
import top.suzhelan.bili.biz.search.entity.UserSearchSpec
import top.suzhelan.bili.biz.search.entity.VideoSearchSpec

object SearchApi {
    private const val API = "https://api.bilibili.com/x/web-interface/wbi/search"

    suspend fun videos(spec: VideoSearchSpec, page: Int): SearchPage<SearchVideo> =
        decodeData(
            BiliWebClient.get(
                "$API/type", mapOf(
                    "keyword" to spec.keyword,
                    "search_type" to "video",
                    "order" to spec.order.parameter,
                    "duration" to spec.duration.parameter.toString(),
                    "page" to page.toString(),
                ), signed = true
            )
        )

    suspend fun users(spec: UserSearchSpec, page: Int): SearchPage<SearchUser> =
        decodeData(
            BiliWebClient.get(
                "$API/type", mapOf(
                    "keyword" to spec.keyword,
                    "search_type" to "bili_user",
                    "order" to spec.order.parameter,
                    "order_sort" to "0",
                    "page" to page.toString(),
                ), signed = true
            )
        )

    suspend fun suggestions(keyword: String): List<SearchSuggestion> {
        val body = BiliWebClient.get(
            "https://s.search.bilibili.com/main/suggest", mapOf(
                "term" to keyword, "main_ver" to "v1", "highlight" to "1",
            )
        )
        val result = body["result"] ?: throw SerializationException("缺少搜索建议")
        return HttpJsonDecoder.decodeFromJsonElement<SuggestionResult>(result).tags
            .filter { it.value.isNotBlank() }.distinctBy { it.value }.take(10)
    }

    suspend fun trending(): List<TrendingWord> = decodeData<TrendingData>(
        BiliWebClient.get(
            "$API/square", mapOf("limit" to "20", "platform" to "web"), signed = true
        )
    ).trending.list.filter { it.keyword.isNotBlank() }.distinctBy { it.keyword }

    private inline fun <reified T> decodeData(body: JsonObject): T =
        HttpJsonDecoder.decodeFromJsonElement(
            body["data"] ?: throw SerializationException("缺少搜索数据")
        )
}
