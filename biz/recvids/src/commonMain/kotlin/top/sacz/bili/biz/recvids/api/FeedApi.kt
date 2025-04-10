package top.sacz.bili.biz.recvids.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.http.path
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.Response
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.recvids.model.VideoList
import kotlin.time.Clock
import kotlin.time.ExperimentalTime


class FeedApi {

    @OptIn(ExperimentalTime::class)
    suspend fun getFeed(): Response.Success<VideoList> {
        val params: MutableMap<String, Any?> = mutableMapOf(
            "fnval" to 272,
            "fnver" to 1,
            "force_host" to 0,
            "fourk" to 0,
            "guidance" to 0,
            "https_url_req" to 0,
            "inline_danmu" to 2,
            "inline_sound" to 1,
            "interest_id" to 0,
            "login_event" to 0,
            "mobi_app" to "android",
            "network" to "wifi",
            "open_event" to "",
            "platform" to "android",
            "pull" to false,
            "idx" to Clock.System.now().epochSeconds.toString(),
            "qn" to 32,
            "recsys_mode" to 0,
            "s_locale" to "zh_CN",
            "video_mode" to 1,
            "voice_balance" to 1,
        )

        return getKtorClient(AppConfig.APP_BASE_URL).get {
            url {
                path("/x/v2/feed/index")
                for ((key, value) in params) {
                    parameter(key, value)
                }
            }
        }.body()
    }
}