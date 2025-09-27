package top.suzhelan.bili.biz.login.model

import kotlinx.serialization.SerialName
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

@Serializable
data class VerifyResult(
    val data: VerifySuccessResult?,
    val eventType: String,
    val timestamp: Long
)

@Serializable
data class VerifySuccessResult(
    @SerialName("geetest_challenge")
    val geetestChallenge: String,
    @SerialName("geetest_seccode")
    val geetestSeccode: String,
    @SerialName("geetest_validate")
    val geetestValidate: String
)

