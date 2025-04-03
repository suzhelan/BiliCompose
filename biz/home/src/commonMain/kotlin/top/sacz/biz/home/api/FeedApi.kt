package top.sacz.biz.home.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.Parameters
import io.ktor.http.contentType
import io.ktor.http.formUrlEncode
import io.ktor.http.parameters
import io.ktor.http.path
import kotlinx.serialization.json.JsonObject
import top.sacz.bili.api.Response
import top.sacz.bili.api.ktorClient
import top.sacz.biz.home.model.VideoList

suspend fun main() {
    val feedApi = FeedApi()
    val feedList = feedApi.getFeed()
    println(feedList.data)
}

class FeedApi {

    suspend fun checkUpdate() : JsonObject {
        val body = Parameters.build { append("version", "1") }
        return ktorClient.post {
            url {
                host = "qstory.sacz.top"
                path("/update/hasUpdate")
            }
            contentType(ContentType.Application.FormUrlEncoded)
            setBody(body.formUrlEncode())
        }.body()
    }

    suspend fun getFeed(): Response<VideoList> {
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
            "qn" to 32,
            "recsys_mode" to 0,
            "s_locale" to "zh_CN",
            "video_mode" to 1,
            "voice_balance" to 1,
        )

        return ktorClient.get {
            url {
                path("/x/v2/feed/index")
                parameters {
                    for ((key, value) in params) {
                        append(key, value.toString())
                    }
                }
            }
        }.body()
    }
}