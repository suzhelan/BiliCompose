package top.sacz.bili.biz.recvids.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UnknownCoverItem(
    @SerialName("card_goto")
    override val cardGoto: String = "",
    @SerialName("card_type")
    override val cardType: String = "",
    @SerialName("idx")
    override val idx: Int = 0
) : BaseCoverItem()