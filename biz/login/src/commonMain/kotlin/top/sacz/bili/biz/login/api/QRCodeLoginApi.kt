package top.sacz.bili.biz.login.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.AppKeyType
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.login.model.TvQRCode
import top.sacz.bili.biz.login.model.WebApplyQRCode
import top.sacz.bili.biz.login.model.WebScanQRCodeResult
import top.sacz.bili.shared.auth.entity.TvLoginResult


class QRCodeLoginApi {
    private val baseUrl = AppConfig.LOGIN_URL

    @Deprecated("web端扫码登录无法获取到access_key")
    suspend fun getCheckScanQRCodeResult(qrcodeKey: String): BiliResponse.Success<WebScanQRCodeResult> {
        return getKtorClient(baseUrl)
            .get("x/passport-login/web/qrcode/poll") {
                url {
                    parameters.append("qrcode_key", qrcodeKey)
                }
            }.body()
    }

    @Deprecated("web端扫码登录无法获取到access_key")
    suspend fun genQRCode(): BiliResponse.Success<WebApplyQRCode> =
        getKtorClient(baseUrl)
            .get("x/passport-login/web/qrcode/generate")
            .body()


    /**
     * appkey	str	APP 密钥	APP 方式必要	可用
     * local_id	num	TV 端 id	TV 端必要	可为0
     * ts	    num	当前时间戳	APP 方式必要
     * sign	str	APP 签名	APP 方式必要
     * mobi_app	str	平台标识	非必要	会被拼接到返回的 url query
     */
    suspend fun getTvQRCode(): BiliResponse.Success<TvQRCode> {
        return getKtorClient(baseUrl, AppKeyType.USER_INFO)
            .post("/x/passport-tv-login/qrcode/auth_code") {
                //appKey,和sign在签名时会自动添加
                //mobi_app和ts在请求时会自动添加到参数
                setBody(
                    FormDataContent(parameters {
                        //tv端必要
                        append("local_id", "0")
                    })
                )
            }
            .body()
    }

    /**
     *  appkey     str  APP密钥  APP方式必要
     *  auth_code  str  扫码秘钥   必要
     *  local_id   num  TV端id  TV端必要    可为0
     *  ts         num  当前时间戳  APP方式必要
     *  sign       str  APP签名  APP方式必要
     */
    suspend fun queryTvQRCodeResult(authCode: String): BiliResponse.SuccessOrNull<TvLoginResult> {
        return getKtorClient(baseUrl, AppKeyType.USER_INFO)
            .post("/x/passport-tv-login/qrcode/poll") {
                //appKey,和sign在签名时会自动添加
                //mobi_app和ts在请求时会自动添加到参数
                setBody(
                    FormDataContent(parameters {
                        append("local_id", "0")
                        append("auth_code", authCode)
                    })
                )
            }.body()
    }
}