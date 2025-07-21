package top.sacz.bili.biz.user.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.user.api.AccountApi
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.shared.auth.config.LoginMapper

class MineViewModel : ViewModel() {

    private val _mine = MutableStateFlow<BiliResponse<Mine>>(BiliResponse.Loading)
    val mine = _mine.asStateFlow()
    fun updateMine() = viewModelScope.launch {
        val api = AccountApi()
        val accessKey = LoginMapper.getAccessKey()
        _mine.value = api.fetchMineData(accessKey)
    }

    private val _myInfo = MutableStateFlow<BiliResponse<AccountInfo>>(BiliResponse.Loading)
    val myInfo = _myInfo.asStateFlow()
    fun fetchMyInfo() = viewModelScope.launch {
        //从网络更新
        val api = AccountApi()
        val accessKey = LoginMapper.getAccessKey()
        _myInfo.value = apiCall { api.fetchMyInfo(accessKey) }
    }
}