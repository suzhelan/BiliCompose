package top.sacz.bili.biz.player.entity

import kotlinx.serialization.Serializable
import top.sacz.bili.api.HttpJsonDecoder


@Serializable
data class PlayerParams(
    val avid: String? = null,
    val bvid: String? = null,
    val epid: String? = null,
    val seasonId: String? = null,
    val cid: String,
    val qn: Int = 80
) {
    fun toJson() = HttpJsonDecoder.encodeToString(this)

    companion object {
        fun fromJson(json: String) = HttpJsonDecoder.decodeFromString<PlayerParams>(json)
    }
}