package top.sacz.bili.biz.login.model

import kotlinx.serialization.Serializable

@Serializable
data class Captcha(
    val geetest: Geetest,
    val tencent: Tencent,
    val token: String,
    val type: String
)
@Serializable
data class Geetest(
    val challenge: String,
    val gt: String
)
@Serializable
data class Tencent(
    val appid: String
)