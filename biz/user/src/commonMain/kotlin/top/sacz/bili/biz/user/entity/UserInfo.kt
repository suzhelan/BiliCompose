package top.sacz.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserInfo(
    val mid: Int,
    val name: String,
    val sign: String,
    val coins: Double,
    val birthday: String,
    val face: String,
    val sex: Int,
    val level: Int,
    val rank: Int,
    val silence: Int,
    @SerialName("vip") val vip: Vip,
    @SerialName("email_status") val emailStatus: Int,
    @SerialName("tel_status") val telStatus: Int,
    @SerialName("official") val official: Official,
    val identification: Int,
    @SerialName("invite") val invite: Invite,
    @SerialName("is_tourist") val isTourist: Int,
    @SerialName("pin_prompting") val pinPrompting: Int
)

@Serializable
data class Vip(
    val type: Int,
    val status: Int,
    @SerialName("due_date") val dueDate: Long,
    @SerialName("vip_pay_type") val vipPayType: Int,
    @SerialName("theme_type") val themeType: Int,
    val label: Label,
    @SerialName("avatar_subscript") val avatarSubscript: Int,
    @SerialName("nickname_color") val nicknameColor: String
)

@Serializable
data class Label(
    val path: String,
    val text: String,
    @SerialName("label_theme") val labelTheme: String
)

@Serializable
data class Official(
    val role: Int,
    val title: String,
    val desc: String,
    val type: Int
)

@Serializable
data class Invite(
    @SerialName("invite_remind") val inviteRemind: Int,
    val display: Boolean
)