package top.sacz.bili.biz.recvids.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ShortVideoData(
    val items: List<JsonObject>
)
