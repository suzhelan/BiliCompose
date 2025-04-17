package top.sacz.bili.biz.user.entity

import kotlinx.serialization.Serializable

/**
 * 硬币数
 */
@Serializable
data class Coin(
    val money: Double?
)