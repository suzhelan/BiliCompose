package top.sacz.bili.api

object AppConfig {
    const val traceId =
        "11111111111111111111111111111111:1111111111111111:0:0"
    val userAgent = "Mozilla/5.0 BiliDroid/1.46.2 (bbcallen@gmail.com) os/android model/vivo mobi_app/android_hd build/2001100 channel/yingyongbao innerVer/2001100 osVer/14 network/2"
    val statistics = """
        {"appId":5,"platform":3,"version":"1.46.2","abtest":""}
    """
    const val API_BASE_URL = "api.bilibili.com"
    const val APP_BASE_URL = "app.bilibili.com"
    const val LOGIN_URL = "passport.bilibili.com"
}
