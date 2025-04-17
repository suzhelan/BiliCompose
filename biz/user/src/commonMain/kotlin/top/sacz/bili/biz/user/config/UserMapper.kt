package top.sacz.bili.biz.user.config


import top.sacz.bili.biz.user.entity.UserInfo
import top.sacz.bili.storage.Storage

object UserMapper {
    private val dataSource = Storage("user")

    fun clear() {
        dataSource.clear()
    }

    fun hasUserInfo(): Boolean {
        return dataSource.hasKey("userInfo")
    }

    fun getUserInfo(): UserInfo {
        return dataSource.getObjectOrNull("userInfo")!!
    }

    fun setUserInfo(userInfo: UserInfo) {
        dataSource.putObject("userInfo", userInfo)
    }
}