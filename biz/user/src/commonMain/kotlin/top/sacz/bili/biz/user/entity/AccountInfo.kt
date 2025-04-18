package top.sacz.bili.biz.user.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class AccountInfo(
    @SerialName("mid") val mid: Int, // 用户mid
    @SerialName("name") val name: String, // 名字
    @SerialName("sign") val sign: String, // 签名
    @SerialName("coins") val coins: Double, // 
    @SerialName("birthday") val birthday: String, // 用户生日（格式：YYYY-MM-DD） 
    @SerialName("face") val face: String, // 
    @SerialName("sex") val sex: Int, // 用户性别（0：私密；1：男；2：女） 
    @SerialName("level") val level: Int, // 用户等级（0-6） 
    @SerialName("rank") val rank: Int, // **作用尚不明确** 
    @SerialName("silence") val silence: Int, // 用户是否被封禁（0：正常；1：封禁） 
    @SerialName("vip") val vip: Vip,
    @SerialName("email_status") val emailStatus: Int, // 是否验证邮箱地址（0：未验证；1：已验证）
    @SerialName("tel_status") val telStatus: Int, // 是否验证手机号（0：未验证；1：已验证）
    @SerialName("official") val official: Official,
    @SerialName("identification") val identification: Int, // **作用尚不明确** 
    @SerialName("invite") val invite: Invite,
    @SerialName("is_tourist") val isTourist: Int, // **作用尚不明确**
    @SerialName("pin_prompting") val pinPrompting: Int // **作用尚不明确**
)

@Serializable
data class Vip(
    @SerialName("type") val type: Int, // 大会员类型（0：无；1：月度；2：年度） 
    @SerialName("status") val status: Int, // 会员开通状态（0：无；1：有） 
    @SerialName("due_date") val dueDate: Long, // 大会员到期时间（毫秒时间戳）
    @SerialName("vip_pay_type") val vipPayType: Int, // 会员开通状态（0：无；1：有）
    @SerialName("theme_type") val themeType: Int, // 会员开通状态（0：无；1：有）
    @SerialName("label") val label: Label, //
    @SerialName("avatar_subscript") val avatarSubscript: Int, // 是否显示会员图标（0：不显示；1：显示）
    @SerialName("nickname_color") val nicknameColor: String // 会员昵称颜色（颜色码）
)

@Serializable
data class Label(
    @SerialName("path") val path: String, // **作用尚不明确** 
    @SerialName("text") val text: String, // 会员类型文字
    @SerialName("label_theme") val labelTheme: String // 会员类型
)

@Serializable
data class Official(
    @SerialName("role") val role: Int, // 认证类型（0：无；1、2、7：个人认证；3、4、5、6：机构认证） 
    @SerialName("title") val title: String, // 无为空 
    @SerialName("desc") val desc: String, // 无为空 
    @SerialName("type") val type: Int // 无为空 
)

@Serializable
data class Invite(
    @SerialName("invite_remind") val inviteRemind: Int, // **作用尚不明确**
    @SerialName("display") val display: Boolean // **作用尚不明确** 
)