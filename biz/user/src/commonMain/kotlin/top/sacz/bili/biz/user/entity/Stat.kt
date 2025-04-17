package top.sacz.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Stat(
    @SerialName("dynamic_count")
    val dynamicCount: Int,//动态数
    val follower: Int,//粉丝数
    val following: Int//关注数
)