package top.sacz.bili.biz.login.config

import top.sacz.bili.biz.login.model.SmsLoginResult
import top.sacz.bili.storage.Storage
import top.sacz.bili.storage.ext.contains

object LoginMapper {
    private val dataSource = Storage("login")

    fun setLoginInfo(smsLoginResult: SmsLoginResult) {
        dataSource.putObject("loginInfo", smsLoginResult)
    }

    fun getLoginInfo(): SmsLoginResult {
        return dataSource.getObjectOrNull<SmsLoginResult>("loginInfo")!!
    }

    fun clear() {
        dataSource.clear()
    }

    fun isLogin(): Boolean {
        return dataSource.contains("loginInfo")
    }
}