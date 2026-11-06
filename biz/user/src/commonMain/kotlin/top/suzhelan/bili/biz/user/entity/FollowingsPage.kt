package top.suzhelan.bili.biz.user.entity

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FollowingsPage(
    @SerialName("list") val list: List<FollowingUser>,
    @SerialName("total") val total: Int,
)

@Serializable
data class FansPage(
    @SerialName("list") val list: List<FollowingUser>,
    @SerialName("total") val total: Int,
    @SerialName("offset") val offset: String? = null,
)

@Serializable
data class FollowingUser(
    @SerialName("mid") val mid: Long,
    @SerialName("uname") val uname: String,
    @SerialName("face") val face: String,
    @SerialName("sign") val sign: String = "",
    @SerialName("attribute") val attribute: Int = 0,
)

class RelationListUser(
    val user: FollowingUser,
    attribute: Int?,
    needsRelationLookup: Boolean,
) {
    var attribute by mutableStateOf(attribute)
    var needsRelationLookup by mutableStateOf(needsRelationLookup)
}
