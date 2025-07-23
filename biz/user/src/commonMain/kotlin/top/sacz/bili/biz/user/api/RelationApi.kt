package top.sacz.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.shared.auth.config.LoginMapper

/**
 * 用户关系api
 */
class RelationApi {

    private val ktor = getKtorClient(baseUrl = AppConfig.API_BASE_URL)

    /**
     * 关注
     * @param mid 目标mid
     * @param action 1为关注，2为取消关注,3为悄悄关注，4为取消悄悄关注,5为拉黑，6为取消拉黑,7为踢出粉丝
     * @return
     */
    suspend fun concern(mid: String, action: Int): BiliResponse.Success<String> {
        return ktor.post("x/relation/modify") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("fid", mid)
                append("act", action.toString())
            }))
        }.body()
    }

    /**
     * 获取关注列表
     * @param tagId 关注标签id
     *  0：默认分组
     * -10：特别关注
     * -20：所有
     * @param page 页码
     * @return
     */
    suspend fun queryFollowList(tagId: Int = -20, page: Int): BiliResponse.Success<Unit> {
        val pageSize = 50
        return ktor.post("/x/relation/tag") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("tagid", tagId.toString())
                append("order_type", "")
                append("ps", pageSize.toString())
                append("pn", page.toString())
            }))
        }.body()
    }
}