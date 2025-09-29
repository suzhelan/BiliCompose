package top.suzhelan.bili.comment.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import top.suzhelan.bili.api.AppConfig
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getKtorClient
import top.suzhelan.bili.comment.entity.CommentPage
import top.suzhelan.bili.comment.entity.CommentSourceType

class CommentApi {
    private val client = getKtorClient(baseUrl = AppConfig.API_BASE_URL)
    suspend fun getCommentList(
        oid: String,
        type: CommentSourceType,
        page: Int,
        pageSize: Int
    ): BiliResponse.Success<CommentPage> {
        /*
        type	num	评论区类型代码	必要	类型代码见表
oid	num	目标评论区 id	必要
sort	num	排序方式	非必要	默认为0
0：按时间
1：按点赞数
2：按回复数
nohot	num	是否不显示热评	非必要	默认为0
1：不显示
0：显示
ps	num	每页项数	非必要	默认为20
定义域：1-20
pn	num	页码	非必要	默认为1
         */
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
}