package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FavoriteFolderList(
    val count: Int = 0,
    val list: List<FavoriteFolder>? = null,
)

@Serializable
data class FavoriteFolder(
    val id: Long,
    val fid: Long = 0,
    val mid: Long = 0,
    val attr: Int = 0,
    val title: String = "",
    val cover: String = "",
    val upper: FavoriteUpper? = null,
    @SerialName("cover_type") val coverType: Int = 0,
    @SerialName("cnt_info") val countInfo: FavoriteCountInfo = FavoriteCountInfo(),
    val type: Int = 0,
    val intro: String = "",
    val ctime: Long = 0,
    val mtime: Long = 0,
    val state: Int = 0,
    @SerialName("fav_state") val favoriteState: Int = 0,
    @SerialName("like_state") val likeState: Int = 0,
    @SerialName("media_count") val mediaCount: Int = 0,
) {
    val isPrivate: Boolean get() = attr and 1 != 0
    val isDefault: Boolean get() = attr and 2 == 0
    val isInvalid: Boolean get() = state != 0
}

@Serializable
data class FavoriteUpper(
    val mid: Long = 0,
    val name: String = "",
    val face: String = "",
    val followed: Boolean = false,
    @SerialName("vip_type") val vipType: Int = 0,
    @SerialName("vip_statue") val vipStatus: Int = 0,
)

@Serializable
data class FavoriteCountInfo(
    val collect: Int = 0,
    val play: Int = 0,
    @SerialName("thumb_up") val thumbUp: Int = 0,
    val share: Int = 0,
    val danmaku: Int = 0,
)

@Serializable
data class FavoriteResourcePage(
    val info: FavoriteFolder,
    val medias: List<FavoriteResource>? = null,
    @SerialName("has_more") val hasMore: Boolean = false,
    val ttl: Long = 0,
)

@Serializable
data class FavoriteResource(
    val id: Long,
    val type: Int = 2,
    val title: String = "",
    val cover: String = "",
    val intro: String = "",
    val page: Int = 0,
    val duration: Int = 0,
    val upper: FavoriteUpper = FavoriteUpper(),
    val attr: Int = 0,
    @SerialName("cnt_info") val countInfo: FavoriteCountInfo = FavoriteCountInfo(),
    val link: String = "",
    val ctime: Long = 0,
    val pubtime: Long = 0,
    @SerialName("fav_time") val favoriteTime: Long = 0,
    @SerialName("bv_id") val bvId: String = "",
    val bvid: String = "",
) {
    val isInvalid: Boolean get() = attr != 0
}
