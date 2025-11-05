package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.entity.UserSpaceInfo
import top.suzhelan.bili.shared.common.base.BaseViewModel

class UserProfileViewModel : BaseViewModel() {
    private val userApi = UserApi()

    private val _userProfileSpace =
        MutableStateFlow<BiliResponse<UserSpaceInfo>>(BiliResponse.Loading)
    val userSpaceInfo = _userProfileSpace.asSharedFlow()

    fun getUserSpaceInfo(mid: Long) = launchTask {
        _userProfileSpace.value = userApi.getUserSpaceInfo(mid)
    }

    private val _userSpace = MutableStateFlow<BiliResponse<UserSpace>>(BiliResponse.Loading)
    val userSpace = _userSpace.asSharedFlow()
    fun getUserSpace(mid: Long) = launchTask {
        _userSpace.value = userApi.getUserSpace(mid)
    }.invokeOnCompletion {
        setLoading(false)
    }
}