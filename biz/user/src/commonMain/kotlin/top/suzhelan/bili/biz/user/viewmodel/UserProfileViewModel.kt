package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.entity.UserSpaceInfo
import top.suzhelan.bili.biz.user.util.RelationUtils
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.base.BaseViewModel

data class ProfileRelationState(
    val attribute: Int? = null,
    val isUpdating: Boolean = false,
    val error: String? = null,
) {
    val isFollowing: Boolean get() = attribute == 2 || attribute == 6
}

class UserProfileViewModel : BaseViewModel() {
    private val userApi = UserApi()
    private val relationApi = RelationApi()
    private val _relationState = MutableStateFlow(ProfileRelationState())
    val relationState = _relationState.asStateFlow()
    private val _actionMessages = Channel<String>(Channel.BUFFERED)
    val actionMessages = _actionMessages.receiveAsFlow()
    private var relationJob: Job? = null
    private var relationKey: Pair<Long, Long?>? = null
    private var relationVersion = 0

    fun refreshRelation(mid: Long) {
        val owner = if (LoginMapper.isLogin()) LoginMapper.getMid() else null
        val key = mid to owner
        if (relationKey == key && relationJob?.isActive == true) return
        relationJob?.cancel()
        if (relationKey != key) _relationState.value = ProfileRelationState()
        relationKey = key
        val version = ++relationVersion
        if (owner == null || owner == mid) {
            _relationState.value = ProfileRelationState(attribute = 0)
            return
        }
        _relationState.value = _relationState.value.copy(error = null)
        relationJob = launchTask {
            try {
                val attribute = relationApi.queryRelation(mid).data.attribute
                if (version == relationVersion) {
                    _relationState.value = ProfileRelationState(attribute = attribute)
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (version == relationVersion) {
                    val message = exception.message ?: "关注状态加载失败，请重试"
                    _relationState.value = _relationState.value.copy(error = message)
                    _actionMessages.send(message)
                }
            }
        }
    }

    fun setFollowing(mid: Long, following: Boolean) {
        val state = _relationState.value
        if (!LoginMapper.isLogin() || LoginMapper.getMid() == mid ||
            relationKey != (mid to LoginMapper.getMid()) || state.attribute == null ||
            state.isUpdating || state.isFollowing == following
        ) return
        // 取消尚未完成的静默查询，避免旧关系覆盖本次操作。
        relationJob?.cancel()
        val version = ++relationVersion
        _relationState.value = state.copy(isUpdating = true, error = null)
        relationJob = launchTask {
            try {
                val response = relationApi.modify(mid, if (following) 1 else 2, source = 11)
                check(response.code == 0) { RelationUtils.getToastByModifyResult(response) }
                if (version != relationVersion) return@launchTask
                // 写入成功即更新按钮；后续查询失败也不能把已成功的操作回滚。
                _relationState.value = ProfileRelationState(
                    attribute = if (following) 2 else 0,
                    isUpdating = true,
                )
                _actionMessages.send(if (following) "关注成功" else "已取消关注")
                getUserSpace(mid)
                try {
                    val attribute = relationApi.queryRelation(mid).data.attribute
                    if (version == relationVersion) {
                        _relationState.value = _relationState.value.copy(attribute = attribute)
                    }
                } catch (exception: CancellationException) {
                    throw exception
                } catch (_: Exception) {
                    // 下次回到页面时再次查询，以确认是否互关。
                }
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                if (version == relationVersion) {
                    _actionMessages.send(exception.message ?: "操作失败，请重试")
                }
            } finally {
                if (version == relationVersion) {
                    _relationState.value = _relationState.value.copy(isUpdating = false)
                }
            }
        }
    }

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
        if (spaceMid != mid || _userSpace.value !is BiliResponse.Success) {
            _userSpace.value = BiliResponse.Loading
        }
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
