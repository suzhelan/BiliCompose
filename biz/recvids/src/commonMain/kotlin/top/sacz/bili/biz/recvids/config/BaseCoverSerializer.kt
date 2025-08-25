package top.sacz.bili.biz.recvids.config

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import top.sacz.bili.biz.recvids.entity.BaseCoverItem
import top.sacz.bili.biz.recvids.entity.SmallCoverV2Item
import top.sacz.bili.biz.recvids.entity.UnknownCoverItem
import top.sacz.bili.biz.recvids.entity.targetCardType

/**
 * 解析 BaseCoverItem到对应的子类
 */
object BaseCoverSerializer : JsonContentPolymorphicSerializer<BaseCoverItem>(BaseCoverItem::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<BaseCoverItem> {
        val gotoType = element.jsonObject["card_type"]?.jsonPrimitive?.content
        return when (gotoType) {
            SmallCoverV2Item.targetCardType -> SmallCoverV2Item.serializer()
            else -> UnknownCoverItem.serializer()
        }
    }
}