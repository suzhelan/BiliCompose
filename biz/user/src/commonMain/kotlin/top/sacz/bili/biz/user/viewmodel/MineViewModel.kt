package top.sacz.bili.biz.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.biz.login.config.LoginMapper
import top.sacz.bili.biz.user.api.AccountApi
import top.sacz.bili.biz.user.config.AccountMapper
import top.sacz.bili.biz.user.entity.AccountInfo

class MineViewModel : ViewModel() {


    private val _userInfo = MutableStateFlow<AccountInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun updateMine() = viewModelScope.launch {
        //先从缓存查
        _userInfo.value = AccountMapper.getUserInfo()
        //从网络更新
        val api = AccountApi()
        val tokenInfo = LoginMapper.getLoginInfo().tokenInfo ?: return@launch
        val accessKey = tokenInfo.accessToken
        val userInfo = api.getMyUserInfo(accessKey)
        _userInfo.value = userInfo.data
        //更新缓存
        AccountMapper.setUserInfo(userInfo.data)
    }
}