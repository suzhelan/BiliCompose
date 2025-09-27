package top.suzhelan.bili.biz.biliplayer.entity

import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

    fun toJson() = json.encodeToString(this)

    companion object {
        val json = Json {
            //忽略未知jsonKey
            ignoreUnknownKeys = true
            //是否将null的属性写入json 默认true
            explicitNulls = true
            //是否使用默认值 默认false
            encodeDefaults = true
            //宽容解析模式 可以解析不规范的json格式
            isLenient = false
        }

        fun fromJson(json: String) = Companion.json.decodeFromString<PlayerParams>(json)
    }
}