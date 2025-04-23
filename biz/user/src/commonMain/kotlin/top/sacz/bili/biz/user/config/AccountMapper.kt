package top.sacz.bili.biz.user.config


import androidx.compose.runtime.State
import top.sacz.bili.biz.login.config.LoginMapper

import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.storage.Storage

object AccountMapper {
    private val dataSource = Storage("user")

    //这样其他的类就无需 login 模块的依赖了
    val isLoginState: State<Boolean> = LoginMapper.isLoginState

    fun clear() {
        dataSource.clear()
    }

    fun hasMyInfo(): Boolean {
        return dataSource.hasKey("userInfo")
    }

    fun getMyInfo(): AccountInfo? {
        return dataSource.getObjectOrNull("userInfo")
    }

    fun setMyInfo(accountInfo: AccountInfo) {
        dataSource.putObject("userInfo", accountInfo)
    }

    fun getMine(): Mine? {
        return dataSource.getObjectOrNull("mine")
    }

    fun setMine(mine: Mine) {
        dataSource.putObject("mine", mine)
    }
}