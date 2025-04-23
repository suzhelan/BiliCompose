package top.sacz.bili.biz.user.config


import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.storage.Storage

object AccountMapper {
    private val dataSource = Storage("user")

    fun clear() {
        dataSource.clear()
    }

    fun hasUserInfo(): Boolean {
        return dataSource.hasKey("userInfo")
    }

    fun getUserInfo(): AccountInfo {
        return dataSource.getObjectOrNull("userInfo")!!
    }

    fun getMine(): Mine? {
        return dataSource.getObjectOrNull("mine")
    }

    fun setMine(mine: Mine) {
        dataSource.putObject("mine", mine)
    }

    fun setUserInfo(accountInfo: AccountInfo) {
        dataSource.putObject("userInfo", accountInfo)
    }
}