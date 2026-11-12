package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import top.suzhelan.bili.biz.user.api.UserDynamicApi
import top.suzhelan.bili.biz.user.entity.ProfilePage
import top.suzhelan.bili.biz.user.entity.UserDynamic
import top.suzhelan.bili.shared.common.base.BaseViewModel

class UserDynamicsViewModel : BaseViewModel() {
    val dynamics = ProfileListLoader<UserDynamic>(viewModelScope) { it.id }

    fun load(mid: Long, force: Boolean = false) =
        dynamics.load(mid, force, UserDynamicApi.pages(mid))
}

class DynamicDetailViewModel : BaseViewModel() {
    val detail = ProfileListLoader<UserDynamic>(viewModelScope) { it.id }

    fun load(id: String, force: Boolean = false) = detail.load(id, force) {
        ProfilePage(listOf(UserDynamicApi.detail(id)), hasMore = false, total = 1)
    }
}
