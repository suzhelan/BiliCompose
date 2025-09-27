package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.api.ext.getCacheMutableStateFlow
import top.suzhelan.bili.api.ext.launchCacheUpdateTask
import top.suzhelan.bili.biz.user.api.AccountApi
import top.suzhelan.bili.biz.user.entity.AccountInfo
import top.suzhelan.bili.biz.user.entity.mine.Mine
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.base.BaseViewModel

class MineViewModel : BaseViewModel() {
    private val api = AccountApi()

    private val _mine = getCacheMutableStateFlow<Mine>("mine")
    val mine = _mine.asStateFlow()
    fun updateMine() = launchCacheUpdateTask(
        "mine",
        _mine
    ) {
        api.fetchMineData(LoginMapper.getAccessKey())
    }

    private val _myInfo = getCacheMutableStateFlow<AccountInfo>("myInfo")
    val myInfo = _myInfo.asStateFlow()
    fun fetchMyInfo() = launchCacheUpdateTask(
        "myInfo",
        _myInfo
    ) {
        api.fetchMyInfo(LoginMapper.getAccessKey())
    }
}