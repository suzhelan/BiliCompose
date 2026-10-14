package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
    val userSpace = _userSpace.asStateFlow()
    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()
    private val _refreshError = MutableStateFlow<String?>(null)
    val refreshError = _refreshError.asStateFlow()
    private var spaceJob: Job? = null
    private var spaceMid: Long? = null
    private var requestVersion = 0

    fun getUserSpace(mid: Long) {
        if (spaceMid == mid && spaceJob?.isActive == true) return
        spaceJob?.cancel()
        if (spaceMid != mid) _userSpace.value = BiliResponse.Loading
        spaceMid = mid
        val version = ++requestVersion
        _isRefreshing.value = true
        _refreshError.value = null
        spaceJob = launchTask {
            try {
                val response = userApi.getUserSpace(mid)
                check(response.code == 0) { response.message }
                if (version == requestVersion) _userSpace.value = response
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (version == requestVersion) {
                    val message = exception.message ?: "资料刷新失败，请重试"
                    _refreshError.value = message
                    if (_userSpace.value !is BiliResponse.Success) {
                        _userSpace.value = BiliResponse.Error(-1, message, exception)
                    }
                }
            } finally {
                if (version == requestVersion) _isRefreshing.value = false
            }
        }
    }

}
