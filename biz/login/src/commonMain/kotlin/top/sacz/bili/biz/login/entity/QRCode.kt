package top.sacz.bili.biz.login.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WebScanQRCodeResult(
    @SerialName("code")
    val code: Int,
    @SerialName("data")
    val message: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    @SerialName("timestamp")
    val timestamp: Int,
    @SerialName("url")
    val url: String
)

@Serializable
data class WebApplyQRCode(
    @SerialName("qrcode_key")
    val qrcodeKey: String,
    @SerialName("url")
    val url: String
)


@Serializable
data class TvQRCode(
    @SerialName("auth_code")
    val authCode: String = "",
    @SerialName("url")
    val url: String = ""
)