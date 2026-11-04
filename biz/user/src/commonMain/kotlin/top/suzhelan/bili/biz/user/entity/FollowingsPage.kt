package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowingsPage(
    @SerialName("list") val list: List<FollowingUser>,
    @SerialName("total") val total: Int,
)

@Serializable
data class FollowingUser(
    @SerialName("mid") val mid: Long,
    @SerialName("uname") val uname: String,
    @SerialName("face") val face: String,
    @SerialName("sign") val sign: String = "",
    @SerialName("attribute") val attribute: Int = 0,
)
