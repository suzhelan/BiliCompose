package top.sacz.bili.api.config

import top.sacz.bili.shared.auth.config.LoginMapper
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


@OptIn(ExperimentalTime::class)
val commonParams: Map<String, String>
    get() {
        val result = mutableMapOf(
            "build" to "8410300",
            "c_locale" to "zh_CN",
            "channel" to "bili",
            "mobi_app" to "android",
            "platform" to "android",
            "s_locale" to "zh_CN",
            "statistics" to BiliHeaders.statistics,
            "ts" to Clock.System.now().epochSeconds.toString(),
        )
        if (LoginMapper.isLogin()) {
            result["access_key"] = LoginMapper.getAccessKey()
        }
        return result
    }