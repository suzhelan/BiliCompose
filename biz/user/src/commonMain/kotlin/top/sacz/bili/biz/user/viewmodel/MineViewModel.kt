package top.sacz.bili.biz.user.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.user.api.AccountApi
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.common.base.BaseViewModel
import top.sacz.bili.storage.Storage

class MineViewModel : BaseViewModel() {

    private val storage = Storage("bili")

    private val _mine = MutableStateFlow<BiliResponse<Mine>>(BiliResponse.Loading)
    val mine = _mine.asStateFlow()
    fun updateMine() = launchTask {
        val cacheKey = "mine"
        val mine: Mine? = storage.getObjectOrNull(cacheKey)
        if (LoginMapper.isLogin() && mine != null) {
            _mine.value = BiliResponse.buildSuccess(mine)
        }
        val api = AccountApi()
        val accessKey = LoginMapper.getAccessKey()
        val mineData = api.fetchMineData(accessKey)
        _mine.value = mineData
        storage.putObject(cacheKey, mineData.data)
    }

    private val _myInfo = MutableStateFlow<BiliResponse<AccountInfo>>(BiliResponse.Wait)
    val myInfo = _myInfo.asStateFlow()
    fun fetchMyInfo() = launchTask {
        val cacheKey = "myInfo"
        val myInfo: AccountInfo? = storage.getObjectOrNull(cacheKey)
        if (myInfo != null) {
            _myInfo.value = BiliResponse.buildSuccess(myInfo)
        }
        //从网络更新
        val api = AccountApi()
        val accessKey = LoginMapper.getAccessKey()
        val myInfoData = api.fetchMyInfo(accessKey)
        _myInfo.value = myInfoData
        storage.putObject(cacheKey, myInfoData.data)
    }
}