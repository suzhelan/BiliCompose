package top.sacz.bili.biz.biliplayer.entity

import kotlinx.serialization.Serializable
import top.sacz.bili.api.HttpJsonDecoder

//状态类型为稳定
@Serializable
data class PlayerParams(
    val avid: Long? = null,
    val bvid: String? = null,
    val epid: String? = null,
    val seasonId: String? = null,
    val cid: Long,
    val qn: Int = 80
) {
    fun toJson() = HttpJsonDecoder.encodeToString(this)

    companion object {
        fun fromJson(json: String) = HttpJsonDecoder.decodeFromString<PlayerParams>(json)
    }
}