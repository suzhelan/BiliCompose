package top.suzhelan.bili.biz.biliplayer.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OnlineCount(
    @SerialName("online")
    val online: Online
) {
    @Serializable
    data class Online(
        @SerialName("total_text")
        val totalText: String // 8.8万+人在看
    )
}