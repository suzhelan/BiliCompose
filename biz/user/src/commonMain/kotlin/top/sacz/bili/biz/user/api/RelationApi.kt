package top.sacz.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.forms.FormDataContent
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.parameters
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.user.entity.RelationTags
import top.sacz.bili.biz.user.entity.RelationUser
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
     * 获取分组列表 不包含-20(全部分组)
     * 包含 -10(特别关注) 0(默认分组) 331816752(自定义的分组)
     */
    suspend fun queryTags(): BiliResponse.Success<List<RelationTags>> {
        return ktor.get("/x/relation/tags") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
            }
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
    suspend fun queryFollowList(
        tagId: Int = -20,
        page: Int = 1
    ): BiliResponse.Success<List<RelationUser>> {
        val pageSize = 50
        return ktor.get("/x/relation/tag") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
                parameter("tagid", tagId.toString())
                parameter("order_type", "")
                parameter("ps", pageSize.toString())
                parameter("pn", page.toString())
            }
        }.body()
    }
}