package top.suzhelan.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import kotlinx.serialization.json.JsonElement
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.biz.user.entity.WatchLaterList
import top.suzhelan.bili.shared.auth.config.LoginMapper

/** 对应 docs/historytoview/toview.md，Web 接口使用 Cookie 和 bili_jct。 */
class WatchLaterApi {
    private val client = getKtorClient(AppConfig.API_BASE_URL, withCookie = true)

    suspend fun getList(): BiliResponse.SuccessOrNull<WatchLaterList> {
        check(LoginMapper.isLogin()) { "请先登录后使用稍后再看" }
        return client.get("/x/v2/history/toview").body()
    }

    suspend fun add(aid: Long): BiliResponse.SuccessOrNull<JsonElement> =
        client.post("/x/v2/history/toview/add") {
            setBody(videoForm(aid))
        }.body()

    suspend fun remove(aid: Long): BiliResponse.SuccessOrNull<JsonElement> =
        client.post("/x/v2/history/toview/del") {
            setBody(videoForm(aid))
        }.body()

    private fun videoForm(aid: Long): FormDataContent {
        check(LoginMapper.isLogin()) { "请先登录后使用稍后再看" }
        require(aid > 0) { "无效的视频编号" }
        val csrf = LoginMapper.getUniversalLoginInfo().cookieInfo.cookies
            .firstOrNull { it.name == "bili_jct" }
            ?.value?.takeIf { it.isNotBlank() }
            ?: error("登录信息中缺少 bili_jct，请重新登录")
        return FormDataContent(parameters {
            append("aid", aid.toString())
            append("csrf", csrf)
        })
    }
}
