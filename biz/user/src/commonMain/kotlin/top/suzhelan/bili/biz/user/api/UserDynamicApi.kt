package top.suzhelan.bili.biz.user.api

import top.suzhelan.bili.api.BiliWebClient
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.api.decodeBiliSuccess
import top.suzhelan.bili.biz.user.entity.ProfilePage
import top.suzhelan.bili.biz.user.entity.UserDynamic
import top.suzhelan.bili.biz.user.entity.UserDynamicDetail
import top.suzhelan.bili.biz.user.entity.UserDynamicPage

object UserDynamicApi {
    private const val BASE = "https://api.bilibili.com/x/polymer/web-dynamic/v1"

    fun pages(mid: Long): suspend (Int) -> ProfilePage<UserDynamic> {
        var offset = ""
        return { page ->
            val result = HttpJsonDecoder.decodeBiliSuccess<UserDynamicPage>(
                BiliWebClient.get(
                    "$BASE/feed/space",
                    mapOf(
                        "host_mid" to mid.toString(),
                        "offset" to if (page == 1) "" else offset,
                        "timezone_offset" to "-480",
                        "features" to "itemOpusStyle",
                    ),
                )
            ).data
            offset = result.offset
            ProfilePage(result.items, result.hasMore, total = 0)
        }
    }

    suspend fun detail(id: String): UserDynamic =
        HttpJsonDecoder.decodeBiliSuccess<UserDynamicDetail>(
            BiliWebClient.get(
                "$BASE/detail",
                mapOf(
                    "id" to id,
                    "timezone_offset" to "-480",
                    "features" to "itemOpusStyle,opusBigCover"
                ),
            )
        ).data.item
}
