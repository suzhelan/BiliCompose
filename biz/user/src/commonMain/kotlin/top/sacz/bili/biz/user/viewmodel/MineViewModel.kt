package top.sacz.bili.biz.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall

import top.sacz.bili.biz.user.api.AccountApi
import top.sacz.bili.biz.user.config.AccountMapper
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.shared.auth.config.LoginMapper

class MineViewModel : ViewModel() {

    private val _mine = MutableStateFlow<Mine?>(null)
    val mine = _mine.asStateFlow()
    fun updateMine() = viewModelScope.launch {
        //先从缓存查
        _mine.value = AccountMapper.getMine()
        if (!LoginMapper.isLogin()) return@launch
        val api = AccountApi()
        val accessKey = LoginMapper.getAccessKey()
        val mine = api.fetchMineData(accessKey)
        _mine.value = mine.data
        AccountMapper.setMine(mine.data)
    }

    private val _myInfo = MutableStateFlow<AccountInfo?>(null)
    val myInfo = _myInfo.asStateFlow()
    fun updateMyInfo() = viewModelScope.launch {
        //先从缓存查
        _myInfo.value = AccountMapper.getMyInfo()
        //从网络更新
        val api = AccountApi()
        if (!LoginMapper.isLogin()) return@launch
        val accessKey = LoginMapper.getAccessKey()
        val userInfoRsp = apiCall { api.getMyInfo(accessKey) }
        when (userInfoRsp) {
            is BiliResponse.Success -> {
                val userInfo = userInfoRsp.data
                _myInfo.value = userInfo
                AccountMapper.setMyInfo(userInfo)
            }
            is BiliResponse.Error -> {
                //TODO: 错误处理
            }
            else -> {

            }
        }
    }
}