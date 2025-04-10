package top.sacz.bili.biz.login.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ScanQRCodeResult(
    val code: Int,
    val message: String,
    @SerialName("refresh_token")
    val refreshToken: String,
    val timestamp: Int,
    val url: String
)

@Serializable
data class ApplyQRCode(
    @SerialName("qrcode_key")
    val qrcodeKey: String,
    val url: String
)