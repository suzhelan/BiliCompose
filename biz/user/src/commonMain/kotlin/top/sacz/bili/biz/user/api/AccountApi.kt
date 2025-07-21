package top.sacz.bili.biz.user.api

import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.http.path
import top.sacz.bili.api.AppConfig
import top.sacz.bili.api.AppKeyType
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.getKtorClient
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.Stat
import top.sacz.bili.biz.user.entity.mine.Mine

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class AccountApi {

    /**
     * 获取我的页信息
     */
    suspend fun fetchMineData(accessKey: String): BiliResponse.Success<Mine> {
        return getKtorClient(AppConfig.APP_BASE_URL).get {
            url {
                path("/x/v2/account/mine")
                parameters.apply {
                    append("access_key", accessKey)
                    append("bili_link_new", "1")
                    append("disable_rcmd", "0")
                    append("from", "mine")
                }
            }
        }.body()
    }

    /**
     * 获取用户信息
     */
    @OptIn(ExperimentalTime::class)
    suspend fun fetchMyInfo(accessKey: String): BiliResponse.Success<AccountInfo> {
        return getKtorClient(
            AppConfig.APP_BASE_URL,
            AppKeyType.APP_COMMON
        ).get("/x/v2/account/myinfo") {
            url {
                parameters.append("access_key", accessKey)
                parameters.append("ts", Clock.System.now().epochSeconds.toString())
                //还有appKey和sign,在ktorClient中的拦截器自动计算并拼接
            }
        }.body()
    }

    /**
     * 获取用户状态数
     */
    suspend fun getStatus(accessKey: String): BiliResponse.Success<Stat> {
        return getKtorClient(
            AppConfig.API_BASE_URL,
            AppKeyType.USER_INFO
        ).get("/x/web-interface/nav/stat") {
            url {
                parameters.append("access_key", accessKey)
            }
        }.body()
    }

    /**
     * 获取硬币数
     */
    suspend fun getCoins(accessKey: String): BiliResponse.Success<Double> {
        return getKtorClient(AppConfig.ACCOUNT_URL, AppKeyType.USER_INFO).get("/site/getCoin") {
            url {
                parameters.append("access_key", accessKey)
            }
        }.body()
    }
}