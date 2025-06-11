package top.sacz.bili.shared.auth.config

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import top.sacz.bili.shared.auth.entity.LoginResult
import top.sacz.bili.shared.auth.entity.TvLoginResult
import top.sacz.bili.shared.auth.entity.UniversalLoginResult
import top.sacz.bili.storage.Storage
import top.sacz.bili.storage.ext.contains

object LoginMapper {
    private val dataSource = Storage("login")

    //是否登录状态
    private val _isLoginState = mutableStateOf(isLogin())
    val isLoginState: State<Boolean> = _isLoginState

    //------------手机端短信登录逻辑------------
    fun setAppLoginInfo(smsLoginResult: LoginResult) {
        dataSource.putObject("loginInfo", smsLoginResult)
        _isLoginState.value = true
    }

    fun getAppLoginInfo(): LoginResult? {
        return dataSource.getObjectOrNull<LoginResult>("loginInfo")
    }

    fun isAppLogin(): Boolean {
        return dataSource.contains("loginInfo")
    }

    //------------TV端扫码登录逻辑------------
    fun getTvLoginInfo(): TvLoginResult? {
        return dataSource.getObjectOrNull<TvLoginResult>("tvLoginInfo")
    }

    fun setTvLoginInfo(tvLoginInfo: TvLoginResult) {
        dataSource.putObject("tvLoginInfo", tvLoginInfo)
        _isLoginState.value = true
    }

    fun isTvLogin(): Boolean {
        return dataSource.contains("tvLoginInfo")
    }

    /**
     * 获取通用登录信息
     */
    fun getUniversalLoginInfo(): UniversalLoginResult {
        val cacheKey = "universalLoginInfo"
        //查询缓存
        if (dataSource.hasKey(cacheKey)) {
            return dataSource.getObjectOrNull<UniversalLoginResult>(cacheKey)!!
        }
        if (isTvLogin() || isAppLogin()) {
            val json = dataSource.json
            val sourceJson = when {
                isAppLogin() -> json.encodeToString(getAppLoginInfo()!!)
                isTvLogin() -> json.encodeToString(getTvLoginInfo()!!)
                else -> throw IllegalStateException("未登录")
            }

            val universalLoginInfo = json.decodeFromString<UniversalLoginResult>(sourceJson)
            dataSource.putObject(cacheKey, universalLoginInfo)
            return universalLoginInfo
        }
        throw IllegalStateException("未登录")
    }

    fun isLogin(): Boolean {
        return isAppLogin() || isTvLogin()
    }

    fun getCookie(): String {
        return getUniversalLoginInfo().cookieInfo.cookies.joinToString(";") {
            "${it.name}=${it.value}"
        }
    }

    fun getAccessKey(): String {
        return getUniversalLoginInfo().tokenInfo.accessToken
    }

    fun clear() {
        dataSource.clear()
        _isLoginState.value = false
    }

    fun getMid(): Int {
        return getUniversalLoginInfo().tokenInfo.mid
    }
}

