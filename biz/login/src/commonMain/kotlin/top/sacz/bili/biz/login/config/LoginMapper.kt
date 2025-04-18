package top.sacz.bili.biz.login.config

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import top.sacz.bili.biz.login.model.SmsLoginResult
import top.sacz.bili.storage.Storage
import top.sacz.bili.storage.ext.contains

object LoginMapper {
    private val dataSource = Storage("login")

    //是否登录状态
    private val _isLoginState = mutableStateOf(isLogin())
    val isLoginState: State<Boolean> = _isLoginState

    fun setLoginInfo(smsLoginResult: SmsLoginResult) {
        dataSource.putObject("loginInfo", smsLoginResult)
        _isLoginState.value = true
    }

    fun getLoginInfo(): SmsLoginResult {
        return dataSource.getObjectOrNull<SmsLoginResult>("loginInfo")!!
    }

    fun clear() {
        dataSource.clear()
        _isLoginState.value = false
    }

    fun isLogin(): Boolean {
        return dataSource.contains("loginInfo")
    }

}