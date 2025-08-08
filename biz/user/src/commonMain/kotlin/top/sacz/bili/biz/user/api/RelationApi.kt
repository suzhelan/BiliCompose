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
import top.sacz.bili.biz.user.entity.Relation
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
    suspend fun modify(mid: Long, action: Int): BiliResponse.SuccessOrNull<Nothing> {
        return ktor.post("x/relation/modify") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("fid", mid.toString())
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

    /**
     * 获取用户和自己的关系
     * @param mid 用户mid
     * @return
     */
    suspend fun queryRelation(mid: Long): BiliResponse.Success<Relation> {
        return ktor.get("/x/relation") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
                parameter("fid", mid)
            }
        }.body()
    }

    /**
     * 批量查询用户和自己的关系
     * @param midList 用户mid列表
     * @return
     */
    suspend fun queryRelations(midList: List<Long>): BiliResponse.Success<Map<String, Relation>> {
        //拼接为 mid1,mid2,mid3这样的字符串
        val mids = midList.joinToString(",")
        return ktor.get("/x/relation/relations") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
                parameter("fids", mids)
            }
        }.body()
    }

    /**
     * 获取分组列表 不包含-20(全部分组)
     * 包含
     * -10(特别关注)
     * 0(默认分组)
     * 331816752(自定义的分组)
     */
    suspend fun queryTags(): BiliResponse.Success<List<RelationTags>> {
        return ktor.get("/x/relation/tags") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
            }
        }.body()
    }

    /**
     * 查询用户所在分组
     * @param mid 用户mid
     * @return 用户所在的分组列表
     */
    suspend fun queryUserInTags(mid: Long): BiliResponse.Success<Map<Int, String>> {
        return ktor.get("/x/relation/tag/user") {
            url {
                parameter("access_key", LoginMapper.getAccessKey())
                parameter("fid", mid)
            }
        }.body()
    }

    /**
     * 创建分组
     * @param tagName 分组名称
     * @return
     */
    suspend fun createTag(tagName: String): BiliResponse.SuccessOrNull<Map<String, Int>> {
        return ktor.post("/x/relation/tag/create") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("tag", tagName)
            }))
        }.body()
    }

    /**
     * 删除分组
     * @param tagId 分组id
     * @return
     */
    suspend fun deleteTag(tagId: Int): BiliResponse.SuccessOrNull<Nothing> {
        return ktor.post("/x/relation/tag/del") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("tagid", tagId.toString())
            }))
        }.body()
    }

    /**
     * 将用户移动到默认分组
     **/
    suspend fun deleteUserFromTag(
        fid: Long,
    ): BiliResponse.SuccessOrNull<Nothing> {
        return ktor.post("/x/relation/tags/addUsers") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("tagids", "0")
                append("fids", fid.toString())
            }))
        }.body()
    }

    /**
     * 添加用户到分组
     */
    suspend fun addUserToTag(
        fids: List<Long>,
        tagIds: List<Int>,
    ): BiliResponse.SuccessOrNull<Nothing> {
        return ktor.post("/x/relation/tags/addUsers") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("tagids", tagIds.joinToString(","))
                append("fids", fids.joinToString(","))
            }))
        }.body()
    }

    /**
     * 批量移动用户到分组
     * 操作难度极高
     * 已知问题 所有字段均不能为空
     * 新旧任何不能重复
     * 出现操作关注分组 也容易出错
     * 优点 一次请求就能完成移动
     * 缺点 难用
     * 推荐使用 [deleteUserFromTag] + [addUserToTag]
     */
    suspend fun moveUserToTag(
        fids: List<Long>,
        beforeTagIds: List<Int>,
        afterTagIds: List<Int>
    ): BiliResponse.SuccessOrNull<Nothing> {
        if (fids.isEmpty()) {
            throw IllegalArgumentException("fids is empty")
        }
        if (beforeTagIds.isEmpty()) {
            throw IllegalArgumentException("beforeTagIds is empty")
        }
        if (afterTagIds.isEmpty()) {
            throw IllegalArgumentException("afterTagIds is empty")
        }
        return ktor.post("x/relation/tags/moveUsers") {
            setBody(FormDataContent(parameters {
                append("access_key", LoginMapper.getAccessKey())
                append("beforeTagids", beforeTagIds.joinToString(","))
                append("afterTagids", afterTagIds.joinToString(","))
                append("fids", fids.joinToString(","))
            }))
        }.body()
    }
}
