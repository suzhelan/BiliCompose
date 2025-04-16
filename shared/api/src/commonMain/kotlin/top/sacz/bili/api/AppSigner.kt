package top.sacz.bili.api

//https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/APPKey.md
enum class AppKeyType(val appKey: String, val appSec: String) {
    //android-粉版-获取资源通用
    APP_COMMON("1d8b6e7d45233436","560c52ccd288fed045859ed18bffd973"),
    //android-粉版-获取用户信息用
    USER_INFO("783bbb7264451d82","2653583c8873dea268ab9386918b1d65")
}

class BiliSignUtils(private val appKeyType: AppKeyType) {
    fun sign(params: MutableMap<String, String>): String {
        return AppSigner.sign(appKeyType.appKey, appKeyType.appSec, params)
    }
}

/**
 * 签名算法
 * https://github.com/SocialSisterYi/bilibili-API-collect/blob/master/docs/misc/sign/APP.md
 */
expect object AppSigner {
    fun sign(
        appKey: String,
        appSec: String,
        params: MutableMap<String, String>
    ): String
}