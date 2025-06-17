package top.sacz.bili.biz.player.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.player.api.UserApi
import top.sacz.bili.biz.player.model.UserCard

class UserViewModel : ViewModel() {
    private val _userCard = MutableStateFlow<BiliResponse<UserCard>>(BiliResponse.Wait)
    val userCard = _userCard.asStateFlow()
    fun getUserInfo(mid: String) = viewModelScope.launch {
        val api = UserApi()
        _userCard.value = BiliResponse.Loading
        _userCard.value = apiCall {
            api.getUserInfo(mid)
        }
    }
}