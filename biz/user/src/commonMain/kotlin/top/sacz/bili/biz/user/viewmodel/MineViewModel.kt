package top.sacz.bili.biz.user.viewmodel

import kotlinx.coroutines.flow.asStateFlow
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.api.ext.getCacheMutableStateFlow
import top.sacz.bili.api.ext.launchCacheUpdateTask
import top.sacz.bili.biz.user.api.AccountApi
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.common.base.BaseViewModel

class MineViewModel : BaseViewModel() {

    private val api = AccountApi()

    private val _mine = getCacheMutableStateFlow<Mine>("mine")
    val mine = _mine.asStateFlow()
    fun updateMine() = launchCacheUpdateTask(
        "mine",
        _mine
    )
    {
        apiCall {
            api.fetchMineData(LoginMapper.getAccessKey())
        }
    }

    private val _myInfo = getCacheMutableStateFlow<AccountInfo>("myInfo")

    val myInfo = _myInfo.asStateFlow()
    fun fetchMyInfo() = launchCacheUpdateTask(
        "myInfo",
        _myInfo
    )
    {
        apiCall {
            api.fetchMyInfo(LoginMapper.getAccessKey())
        }
    }
}