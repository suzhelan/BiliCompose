package top.suzhelan.bili.comment.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.comment.entity.CommentLazyPage
import top.suzhelan.bili.comment.entity.CommentPage
import top.suzhelan.bili.comment.entity.CommentReplyPage
import top.suzhelan.bili.comment.entity.CommentSourceType

class CommentApi {
    private val client = getKtorClient(baseUrl = AppConfig.API_BASE_URL)
    private val wbiClient = getKtorClient(
        baseUrl = AppConfig.API_BASE_URL,
        withCookie = true,
        withWbi = true
    )

    /**
     * 获取评论区明细，使用旧的页码分页接口。
     *
     * 接口：`GET https://api.bilibili.com/x/v2/reply`
     *
     * 这个接口使用 `pn` + `ps` 翻页，调用方传入页码即可获取对应页数据。
     * 当前项目仍保留这个方法，供需要按页码访问评论的场景使用；新的列表加载优先使用
     * [getCommentLazyList]。
     *
     * @param oid 目标评论区 id。视频评论区一般传 aid。
     * @param type 评论区类型代码，见 [CommentSourceType]。
     * @param page 页码，从 1 开始。
     * @param pageSize 每页评论数量，接口定义范围通常为 1-20，默认 20。
     * @return B 站响应体，`data.replies` 为当前页评论列表。
     */
    suspend fun getCommentList(
        oid: String,
        type: CommentSourceType,
        page: Int,
        pageSize: Int = 20
    ): BiliResponse.Success<CommentPage> {
        return client.get("x/v2/reply") {
            url {
                parameter("type", type.type)
                parameter("oid", oid)
                parameter("sort", 0)
                parameter("nohot", 0)
                parameter("ps", pageSize)
                parameter("pn", page)
            }
        }.body()
    }

    /**
     * 获取评论区明细，使用 WBI 懒加载接口。
     *
     * 接口：`GET https://api.bilibili.com/x/v2/reply/wbi/main`
     *
     * 这个接口使用 WBI 签名鉴权，并通过响应中的
     * `data.cursor.pagination_reply.next_offset` 继续加载下一页。
     *
     * 使用方式：
     * - 首次加载时 `paginationOffset` 传 `null`，方法会携带空的 `seek_rpid`。
     * - 后续加载时，把上一次响应的 `cursor.paginationReply.nextOffset`
     *   作为 `paginationOffset` 传入。
     * - 方法会把 offset 包装为接口要求的 `pagination_str={"offset":"..."}`。
     *
     * @param oid 目标评论区 id。视频评论区一般传 aid。
     * @param type 评论区类型代码，见 [CommentSourceType]。
     * @param paginationOffset 下一页偏移信息，来自上一次响应的
     * `data.cursor.pagination_reply.next_offset`；首次加载传 `null`。
     * @param mode 排序模式。`2` 表示按时间，`3` 表示按热度，默认使用 `2` 以接近旧接口的按时间加载。
     * @return B 站响应体。成功时 `data.replies` 为本次懒加载评论列表，
     * `data.cursor.paginationReply.nextOffset` 可用于下一次请求；被风控时可能返回
     * `code=-412` 且没有 `data` 字段。
     */
    suspend fun getCommentLazyList(
        oid: String,
        type: CommentSourceType,
        paginationOffset: String? = null,
        mode: Int = 2
    ): BiliResponse.SuccessOrNull<CommentLazyPage> {
        return wbiClient.get("x/v2/reply/wbi/main") {
            url {
                parameter("type", type.type)
                parameter("oid", oid)
                parameter("mode", mode)
                parameter("web_location", 1315875)
                if (paginationOffset != null) {
                    parameter("pagination_str", buildJsonObject {
                        put("offset", paginationOffset)
                    }.toString())
                } else {
                    parameter("seek_rpid", "")
                }
            }
        }.body()
    }

    /**
     * 获取指定主评论的回复详情。
     *
     * 接口：`GET https://api.bilibili.com/x/v2/reply/reply`
     *
     * 返回值里 `data.root` 是主评论，`data.replies` 是回复这条主评论的二级评论。
     * 当前 BottomDialog 只需要第一页即可快速展示详情；后续如果需要完整分页，
     * 可以继续把 `pn` 暴露给 PagingSource。
     */
    suspend fun getCommentReplies(
        oid: String,
        type: CommentSourceType,
        root: Long,
        page: Int = 1,
        pageSize: Int = 20,
    ): BiliResponse.SuccessOrNull<CommentReplyPage> {
        return client.get("x/v2/reply/reply") {
            url {
                parameter("type", type.type)
                parameter("oid", oid)
                parameter("root", root)
                parameter("ps", pageSize)
                parameter("pn", page)
            }
        }.body()
    }
}
