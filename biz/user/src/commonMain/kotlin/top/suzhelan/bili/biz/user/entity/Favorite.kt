package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderList(
    @SerialName("count") val count: Int = 0,
    @SerialName("list") val list: List<FavoriteFolder>? = null,
)

@Serializable
data class FavoriteFolder(
    @SerialName("id") val id: Long,
    @SerialName("fid") val fid: Long = 0,
    @SerialName("mid") val mid: Long = 0,
    @SerialName("attr") val attr: Int = 0,
    @SerialName("title") val title: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("upper") val upper: FavoriteUpper? = null,
    @SerialName("cover_type") val coverType: Int = 0,
    @SerialName("cnt_info") val countInfo: FavoriteCountInfo = FavoriteCountInfo(),
    @SerialName("type") val type: Int = 0,
    @SerialName("intro") val intro: String = "",
    @SerialName("ctime") val ctime: Long = 0,
    @SerialName("mtime") val mtime: Long = 0,
    @SerialName("state") val state: Int = 0,
    @SerialName("fav_state") val favoriteState: Int = 0,
    @SerialName("like_state") val likeState: Int = 0,
    @SerialName("media_count") val mediaCount: Int = 0,
    @SerialName("bvid") val bvid: String = "",
    @SerialName("link") val link: String = "",
) {
    val isCollection: Boolean get() = type == 21

    // 创建列表及旧响应可能不带 type；收藏夹类型为 11，UGC 合集为 21。
    val canOpen: Boolean get() = !isInvalid && (type == 0 || type == 11 || isCollection)
    val isPrivate: Boolean get() = !isCollection && attr and 1 != 0
    val isDefault: Boolean get() = attr and 2 == 0
    val isInvalid: Boolean get() = state != 0
}

@Serializable
data class FavoriteUpper(
    @SerialName("mid") val mid: Long = 0,
    @SerialName("name") val name: String = "",
    @SerialName("face") val face: String = "",
    @SerialName("followed") val followed: Boolean = false,
    @SerialName("vip_type") val vipType: Int = 0,
    @SerialName("vip_statue") val vipStatus: Int = 0,
)

@Serializable
data class FavoriteCountInfo(
    @SerialName("collect") val collect: Int = 0,
    @SerialName("play") val play: Int = 0,
    @SerialName("thumb_up") val thumbUp: Int = 0,
    @SerialName("share") val share: Int = 0,
    @SerialName("danmaku") val danmaku: Int = 0,
)

@Serializable
data class FavoriteResourcePage(
    @SerialName("info") val info: FavoriteFolder,
    @SerialName("medias") val medias: List<FavoriteResource>? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
    @SerialName("ttl") val ttl: Long = 0,
)

@Serializable
data class FavoriteResource(
    @SerialName("id") val id: Long,
    @SerialName("type") val type: Int = 2,
    @SerialName("title") val title: String = "",
    @SerialName("cover") val cover: String = "",
    @SerialName("intro") val intro: String = "",
    @SerialName("page") val page: Int = 0,
    @SerialName("duration") val duration: Int = 0,
    @SerialName("upper") val upper: FavoriteUpper = FavoriteUpper(),
    @SerialName("attr") val attr: Int = 0,
    @SerialName("cnt_info") val countInfo: FavoriteCountInfo = FavoriteCountInfo(),
    @SerialName("link") val link: String = "",
    @SerialName("ctime") val ctime: Long = 0,
    @SerialName("pubtime") val pubtime: Long = 0,
    @SerialName("fav_time") val favoriteTime: Long = 0,
    @SerialName("bv_id") val bvId: String = "",
    @SerialName("bvid") val bvid: String = "",
) {
    val isInvalid: Boolean get() = attr != 0
}
