package top.suzhelan.bili.api

import io.ktor.http.encodeURLParameter
import io.ktor.utils.io.core.toByteArray
import korlibs.crypto.MD5

//https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/APPKey.md
enum class AppKeyType(val appKey: String, val appSec: String) {
    //android-粉版-获取资源通用
    APP_COMMON("1d8b6e7d45233436", "560c52ccd288fed045859ed18bffd973"),

    //android-粉版-获取用户信息用/登录用
    USER_INFO("783bbb7264451d82", "2653583c8873dea268ab9386918b1d65")
}

class BiliSignUtils(private val appKeyType: AppKeyType) {
    fun sign(params: MutableMap<String, String>): String {
        return AppSigner.appSign(appKeyType.appSec, params)
    }
}

/**
 * 签名算法
 * https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/APP.md
 */
object AppSigner {
    fun appSign(
        appSec: String,
        params: MutableMap<String, String>
    ): String {
        // 使用 Kotlin 标准库的排序方法
        val sortedEntries = params.entries.sortedBy { it.key }
        // 拼接参数
        val queryString = sortedEntries.joinToString("&") { (key, value) ->
            "${urlEncode(key)}=${urlEncode(value)}"
        }
        return (queryString + appSec).md5()
    }

    // 通用 URL 编码
    private fun urlEncode(str: String): String {
        return str.encodeURLParameter()
    }

    // 通用 MD5（使用 Krypto 等跨平台库）
    private fun String.md5(): String {
        return MD5.digest(this@md5.toByteArray()).hex
    }
}
