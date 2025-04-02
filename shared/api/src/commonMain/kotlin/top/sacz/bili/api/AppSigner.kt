package top.sacz.bili.api

class BiliSignUtils(private val appKeyType: AppKeyType) {
    fun sign(params: MutableMap<String, Any?>): String {
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
        params: MutableMap<String, Any?>
    ): String
}