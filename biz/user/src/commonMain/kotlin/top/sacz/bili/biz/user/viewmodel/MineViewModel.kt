package top.sacz.bili.biz.user.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    private val _hasUserInfo = mutableStateOf(AccountMapper.hasUserInfo())
    val hasUserInfo : State<Boolean> = _hasUserInfo

    private val _userInfo = MutableStateFlow<AccountInfo?>(null)
    val userInfo = _userInfo.asStateFlow()

    fun updateMine() = viewModelScope.launch {
        val api = AccountApi()
        val tokenInfo = LoginMapper.getLoginInfo().tokenInfo ?: return@launch
        val accessKey = tokenInfo.accessToken
        val userInfo = api.getMyUserInfo(accessKey)
        _userInfo.value = userInfo.data
        _hasUserInfo.value = true
        AccountMapper.setUserInfo(userInfo.data)
    }
}