package top.sacz.bili.biz.recvids.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ShortVideoData(
    val items: List<JsonObject>
)
