package top.sacz.bili.shared.auth.config

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import top.sacz.bili.shared.auth.entity.LoginResult
import top.sacz.bili.storage.Storage
import top.sacz.bili.storage.ext.contains

object LoginMapper {
    private val dataSource = Storage("login")

    //是否登录状态
    private val _isLoginState = mutableStateOf(isLogin())
    val isLoginState: State<Boolean> = _isLoginState

    /**
     * 设置登录信息
     */
    fun setLoginInfo(smsLoginResult: LoginResult) {
        dataSource.putObject("loginInfo", smsLoginResult)
        _isLoginState.value = true
    }

    fun getCookie(): String {
        if (!isLogin()) return ""
        return getLoginInfo().cookieInfo?.cookies?.joinToString(";") {
            "${it.name}=${it.value}"
        } ?: ""
    }

    /**
     * 获取登录信息
     */
    fun getLoginInfo(): LoginResult {
        return dataSource.getObjectOrNull<LoginResult>("loginInfo")!!
    }

    fun getAccessKey(): String {
        return getLoginInfo().tokenInfo!!.accessToken
    }

    fun clear() {
        dataSource.clear()
        _isLoginState.value = false
    }

    fun isLogin(): Boolean {
        return dataSource.contains("loginInfo")
    }


}

