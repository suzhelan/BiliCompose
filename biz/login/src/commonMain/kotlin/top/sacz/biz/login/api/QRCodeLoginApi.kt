package top.sacz.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.Response
import top.sacz.bili.api.getKtorClient
import top.sacz.biz.login.model.ApplyQRCode
import top.sacz.biz.login.model.ScanQRCodeResult

suspend fun main() {
    val api = QRCodeLoginApi()
    val applyQRCode =  api.genQRCode().data
    api.getCheckScanQRCodeResult(applyQRCode.qrcodeKey)
}

class QRCodeLoginApi {
    private val baseUrl = AppConfig.LOGIN_URL

    suspend fun getCheckScanQRCodeResult(qrcodeKey: String): Response.Success<ScanQRCodeResult> {
        return getKtorClient(baseUrl)
            .get("x/passport-login/web/qrcode/poll") {
                url {
                    parameters.append("qrcode_key", qrcodeKey)
                }
            }.body()
    }


    suspend fun genQRCode(): Response.Success<ApplyQRCode> =
        getKtorClient(baseUrl)
            .get("x/passport-login/web/qrcode/generate")
            .body()
}