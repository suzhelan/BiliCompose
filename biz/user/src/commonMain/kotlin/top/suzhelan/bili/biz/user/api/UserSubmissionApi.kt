package top.suzhelan.bili.biz.user.api

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonNull
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.intOrNull
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.api.BiliWebClient
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.biz.user.entity.CollectionArchive
import top.suzhelan.bili.biz.user.entity.CollectionEntry
import top.suzhelan.bili.biz.user.entity.ProfilePage
import top.suzhelan.bili.biz.user.entity.SubmissionCategory
import top.suzhelan.bili.biz.user.entity.SubmissionData
import top.suzhelan.bili.biz.user.entity.SubmissionOrder
import top.suzhelan.bili.biz.user.entity.SubmissionVideo
import top.suzhelan.bili.biz.user.entity.UserCollection
import top.suzhelan.bili.biz.user.entity.UserCollectionKind
import top.suzhelan.bili.shared.auth.config.LoginMapper

object UserSubmissionApi {
    private const val API = "https://api.bilibili.com"
    private const val PAGE_SIZE = 30
    private const val COLLECTION_PAGE_SIZE = 20
    private val json = Json(HttpJsonDecoder) { coerceInputValues = true }

    fun videoPages(
        mid: Long,
        order: SubmissionOrder,
        tid: Int,
    ): suspend (Int) -> ProfilePage<SubmissionVideo> =
        submissionPagesWithFallback(
            order, tid,
            primary = { videos(mid, order, tid, it) },
            backup = { page ->
                decodeArchives(
                    data(
                        BiliWebClient.get(
                            "$API/x/series/recArchivesByKeywords",
                            mapOf(
                                "mid" to "$mid",
                                "keywords" to "",
                                "pn" to "$page",
                                "ps" to "$PAGE_SIZE",
                                "orderby" to if (order == SubmissionOrder.MOST_PLAYED) "views" else "pubdate"
                            ),
                        )
                    ), page
                ).copy(notice = "分区信息暂时不可用，可重试获取")
            },
        )

    suspend fun videos(
        mid: Long,
        order: SubmissionOrder,
        tid: Int,
        page: Int,
    ): ProfilePage<SubmissionVideo> =
        decodeVideos(
            data(
                BiliWebClient.get(
                    "$API/x/space/wbi/arc/search",
                    mapOf(
                        "mid" to "$mid",
                        "order" to order.parameter,
                        "tid" to "$tid",
                        "pn" to "$page",
                        "ps" to "$PAGE_SIZE"
                    ),
                    signed = true,
                )
            ), page
        )

    suspend fun collections(mid: Long, page: Int): ProfilePage<UserCollection> =
        decodeCollections(
            data(
                BiliWebClient.get(
                    "$API/x/polymer/web-space/seasons_series_list",
                    mapOf(
                        "mid" to "$mid",
                        "page_num" to "$page",
                        "page_size" to "$COLLECTION_PAGE_SIZE"
                    ),
                )
            ), page
        )

    suspend fun collectionVideos(
        mid: Long,
        id: Long,
        kind: UserCollectionKind,
        reverse: Boolean,
        page: Int,
    ): ProfilePage<SubmissionVideo> {
        val params = if (kind == UserCollectionKind.SEASON) mapOf(
            "mid" to "$mid",
            "season_id" to "$id",
            "sort_reverse" to "$reverse",
            "page_num" to "$page",
            "page_size" to "$PAGE_SIZE",
        ) else mapOf(
            "mid" to "$mid", "series_id" to "$id", "sort" to if (reverse) "asc" else "desc",
            "pn" to "$page", "ps" to "$PAGE_SIZE", "only_normal" to "true",
            "current_mid" to if (LoginMapper.isLogin()) "${LoginMapper.getMid()}" else "0",
        )
        val path =
            if (kind == UserCollectionKind.SEASON) "/x/polymer/web-space/seasons_archives_list" else "/x/series/archives"
        return decodeArchives(data(BiliWebClient.get("$API$path", params)), page)
    }

    private fun data(body: JsonObject): JsonObject = (body["data"] as? JsonObject)
        ?.also(::checkRisk) ?: throw SerializationException("投稿接口未返回有效数据，请重试")

    private fun checkRisk(data: JsonObject) {
        if ((data["is_risk"] as? JsonPrimitive)?.booleanOrNull == true ||
            ((data["gaia_res_type"] as? JsonPrimitive)?.intOrNull ?: 0) != 0 ||
            data["gaia_data"]?.let { it != JsonNull } == true
        ) throw ApiException(
            -352,
            "投稿请求需要验证，请稍后重试",
            IllegalStateException("Space verification required")
        )
    }

    fun decodeVideos(data: JsonObject, page: Int): ProfilePage<SubmissionVideo> {
        checkRisk(data)
        val result = json.decodeFromJsonElement<SubmissionData>(data)
        val categories = when (val value = result.list.categories) {
            null, JsonNull -> emptyList()
            is JsonObject -> value.values.map { json.decodeFromJsonElement<SubmissionCategory>(it) }
            is JsonArray -> if (value.isEmpty()) emptyList() else throw SerializationException("无效的视频分区数据")
            else -> throw SerializationException("无效的视频分区数据")
        }.filter { it.tid > 0 }.distinctBy { it.tid }
            .sortedWith(compareByDescending<SubmissionCategory> { it.count }.thenBy { it.tid })
        val videos = result.list.videos.orEmpty()
        return ProfilePage(
            videos,
            videos.isNotEmpty() && page.toLong() * result.page.size.coerceAtLeast(1) < result.page.count,
            result.page.count,
            categories
        )
    }

    fun decodeCollections(data: JsonObject, page: Int): ProfilePage<UserCollection> {
        val lists =
            data["items_lists"] as? JsonObject ?: throw SerializationException("缺少合集与系列数据")

        fun entries(field: String, kind: UserCollectionKind): List<UserCollection> =
            array(lists, field).map {
                val entry = json.decodeFromJsonElement<CollectionEntry>(it)
                val meta = entry.meta
                val id = if (kind == UserCollectionKind.SEASON) meta.seasonId else meta.seriesId
                check(id > 0) { "合集或系列缺少有效标识" }
                UserCollection(
                    id,
                    kind,
                    meta.name,
                    meta.cover.ifBlank { entry.archives.firstOrNull()?.cover.orEmpty() },
                    meta.total,
                    meta.description
                )
            }

        val items = entries("seasons_list", UserCollectionKind.SEASON) + entries(
            "series_list",
            UserCollectionKind.SERIES
        )
        val info = lists["page"] as? JsonObject ?: throw SerializationException("缺少合集分页信息")
        val total = (info["total"] as? JsonPrimitive)?.intOrNull
            ?: throw SerializationException("缺少合集总数")
        // 文档将 total 描述为总页数，但示例及实际响应使用条目总数；同时以空页/重复页保护分页。
        val size = (info["page_size"] as? JsonPrimitive)?.intOrNull?.coerceAtLeast(1)
            ?: COLLECTION_PAGE_SIZE
        return ProfilePage(items, items.isNotEmpty() && page.toLong() * size < total, total)
    }

    fun decodeArchives(data: JsonObject, page: Int): ProfilePage<SubmissionVideo> {
        val videos = array(data, "archives").map {
            json.decodeFromJsonElement<CollectionArchive>(it).toVideo()
        }
        val info = data["page"] as? JsonObject ?: throw SerializationException("缺少视频分页信息")
        val total = (info["total"] as? JsonPrimitive)?.intOrNull
            ?: throw SerializationException("缺少视频总数")
        val size =
            ((info["page_size"] ?: info["size"]) as? JsonPrimitive)?.intOrNull?.coerceAtLeast(1)
                ?: PAGE_SIZE
        return ProfilePage(videos, videos.isNotEmpty() && page.toLong() * size < total, total)
    }

    private fun array(data: JsonObject, field: String): JsonArray = when (val value = data[field]) {
        is JsonArray -> value
        JsonNull -> JsonArray(emptyList())
        else -> throw SerializationException("缺少有效的 $field 列表")
    }
}

/** 备用接口仅支持这两种未筛选查询；一轮分页固定来源，刷新时再尝试主接口。 */
fun submissionPagesWithFallback(
    order: SubmissionOrder,
    tid: Int,
    primary: suspend (Int) -> ProfilePage<SubmissionVideo>,
    backup: suspend (Int) -> ProfilePage<SubmissionVideo>,
): suspend (Int) -> ProfilePage<SubmissionVideo> {
    var usingBackup = false
    return { page ->
        if (usingBackup) backup(page) else try {
            primary(page)
        } catch (error: ApiException) {
            // 已经取得首页后不能混用两个数据源的后续页。
            if (page != 1 || tid != 0 || order == SubmissionOrder.MOST_FAVORITED ||
                error.code !in listOf(-412, -352, -403)
            ) throw error
            backup(page).also { usingBackup = true }
        }
    }
}
