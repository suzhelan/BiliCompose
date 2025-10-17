package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.UserCard
import top.suzhelan.bili.shared.common.base.BaseViewModel

class UserViewModel : BaseViewModel() {
    private val _userCard = MutableStateFlow<BiliResponse<UserCard>>(BiliResponse.Loading)
    val userCard = _userCard.asStateFlow()
    fun getUserInfo(mid: Long) = viewModelScope.launch {
        val api = UserApi()
        _userCard.value = apiCall {
            api.getUserInfo(mid)
        }
    }


    private val _actionState = MutableStateFlow<BiliResponse<Nothing>>(BiliResponse.Wait)
    val actionState = _actionState.asStateFlow()
    fun unfollow(mid: Long) = launchTask {
        val api = RelationApi()
        _actionState.value = BiliResponse.Loading
        _actionState.value = apiCall {
            api.modify(mid, 2).apply {
                if (isSuccess()) {
                    getUserInfo(mid)
                } else {
                    showMessageDialog(message = message)
                }
            }
        }
    }

    fun follow(mid: Long) = launchTask {
        val api = RelationApi()
        _actionState.value = BiliResponse.Loading
        _actionState.value = apiCall {
            api.modify(mid, 1).apply {
                if (isSuccess()) {
                    getUserInfo(mid)
                } else {
                    showMessageDialog(message = message)
                }
            }
        }
    }
}