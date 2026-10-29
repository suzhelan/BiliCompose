package top.suzhelan.bili.biz.user.api

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.HttpHeaders
import io.ktor.http.parameters
import kotlinx.serialization.json.JsonElement
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.bodyBiliSuccess
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.user.entity.CollectionFavoriteStatus
import top.suzhelan.bili.biz.user.entity.FavoriteFolder
import top.suzhelan.bili.biz.user.entity.FavoriteFolderList
import top.suzhelan.bili.biz.user.entity.FavoriteResourcePage
import top.suzhelan.bili.shared.auth.config.LoginMapper

class FavoriteApi(
    private val client: HttpClient = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true,
    ),
    private val csrfProvider: () -> String = {
        LoginMapper.getUniversalLoginInfo().cookieInfo.cookies
            .firstOrNull { it.name == "bili_jct" }
            ?.value
            ?: error("登录信息中缺少 bili_jct，无法执行收藏操作")
    },
) {

    suspend fun getCreatedFolders(
        upMid: Long,
        resourceId: Long? = null,
    ): BiliResponse.SuccessOrNull<FavoriteFolderList> =
        client.get("/x/v3/fav/folder/created/list-all") {
            parameter("up_mid", upMid)
            parameter("type", 2)
            resourceId?.let { parameter("rid", it) }
        }.body()

    suspend fun getCollectedFolders(
        upMid: Long,
        page: Int = 1,
        pageSize: Int = 20,
    ): BiliResponse.SuccessOrNull<FavoriteFolderList> =
        client.get("/x/v3/fav/folder/collected/list") {
            parameter("up_mid", upMid)
            parameter("pn", page)
            parameter("ps", pageSize)
            parameter("platform", "web")
        }.body()

    /** 合集收藏状态独立于当前视频的收藏状态。 */
    suspend fun isCollectionFavorite(aid: Long): Boolean {
        val status = client.get("/x/web-interface/archive/relation") {
            parameter("aid", aid)
        }.bodyBiliSuccess<CollectionFavoriteStatus>().data
        return status.isFavorite ?: error("接口未返回合集收藏状态，请重试")
    }

    /** 收藏整个 UGC 合集，不需要选择视频收藏夹。 */
    suspend fun setCollectionFavorite(
        seasonId: Long,
        favorite: Boolean,
    ): BiliResponse.SuccessOrNull<JsonElement> =
        client.post(if (favorite) "/x/v3/fav/season/fav" else "/x/v3/fav/season/unfav") {
            header(HttpHeaders.Referrer, "https://www.bilibili.com/")
            setBody(formData {
                append("season_id", seasonId.toString())
                append("platform", "web")
            })
        }.body()

    suspend fun getFolderInfo(mediaId: Long): BiliResponse.Success<FavoriteFolder> =
        client.get("/x/v3/fav/folder/info") {
            parameter("media_id", mediaId)
        }.body()

    suspend fun getFolderResources(
        mediaId: Long,
        page: Int = 1,
        pageSize: Int = 20,
        keyword: String = "",
        order: String = "mtime",
    ): BiliResponse.Success<FavoriteResourcePage> = client.get("/x/v3/fav/resource/list") {
        parameter("media_id", mediaId)
        parameter("pn", page)
        parameter("ps", pageSize.coerceIn(1, 20))
        parameter("order", order)
        parameter("platform", "web")
        if (keyword.isNotBlank()) parameter("keyword", keyword.trim())
    }.body()

    suspend fun createFolder(
        title: String,
        intro: String,
        isPrivate: Boolean,
    ): BiliResponse.Success<FavoriteFolder> = client.post("/x/v3/fav/folder/add") {
        setBody(formData {
            append("title", title.trim())
            append("intro", intro.trim())
            append("privacy", if (isPrivate) "1" else "0")
        })
    }.body()

    suspend fun editFolder(
        mediaId: Long,
        title: String,
        intro: String,
        isPrivate: Boolean,
    ): BiliResponse.Success<FavoriteFolder> = client.post("/x/v3/fav/folder/edit") {
        setBody(formData {
            append("media_id", mediaId.toString())
            append("title", title.trim())
            append("intro", intro.trim())
            append("privacy", if (isPrivate) "1" else "0")
        })
    }.body()

    suspend fun deleteFolder(mediaId: Long): BiliResponse.SuccessOrNull<JsonElement> =
        client.post("/x/v3/fav/folder/del") {
            setBody(formData { append("media_ids", mediaId.toString()) })
        }.body()

    suspend fun cleanInvalidResources(mediaId: Long): BiliResponse.SuccessOrNull<JsonElement> =
        client.post("/x/v3/fav/resource/clean") {
            setBody(formData { append("media_id", mediaId.toString()) })
        }.body()

    suspend fun removeResource(
        mediaId: Long,
        resourceId: Long,
        resourceType: Int,
    ): BiliResponse.SuccessOrNull<JsonElement> = client.post("/x/v3/fav/resource/batch-del") {
        header(HttpHeaders.Referrer, "https://www.bilibili.com/")
        setBody(formData {
            append("media_id", mediaId.toString())
            append("resources", "$resourceId:$resourceType")
            append("platform", "web")
        })
    }.body()

    suspend fun updateVideoFolders(
        aid: Long,
        addMediaIds: Collection<Long>,
        deleteMediaIds: Collection<Long>,
    ): BiliResponse.SuccessOrNull<JsonElement> = client.post("/x/v3/fav/resource/deal") {
        header(HttpHeaders.Referrer, "https://www.bilibili.com/")
        setBody(formData {
            append("rid", aid.toString())
            append("type", "2")
            append("add_media_ids", addMediaIds.joinToString(","))
            append("del_media_ids", deleteMediaIds.joinToString(","))
            append("platform", "web")
        })
    }.body()

    private fun formData(block: io.ktor.http.ParametersBuilder.() -> Unit) =
        FormDataContent(parameters {
            block()
            append("csrf", csrfProvider())
        })
}
