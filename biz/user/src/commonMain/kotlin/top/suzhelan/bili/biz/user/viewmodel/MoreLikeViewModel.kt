package top.suzhelan.bili.biz.user.viewmodel

import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.api.WatchLaterApi
import top.suzhelan.bili.biz.user.data.LikeVideoListDataSource
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.biz.user.entity.LikeVideoList
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.base.BaseViewModel

class MoreLikeViewModel(
    private val loadPage: (suspend (Long, Int, Int) -> BiliResponse.Success<LikeVideoList>)? = null,
) : BaseViewModel() {
    private val userApi = UserApi()
    private val watchLaterApi = WatchLaterApi()
    private val _isActionBusy = MutableStateFlow(false)
    val isActionBusy = _isActionBusy.asStateFlow()
    private val _messages = Channel<String>(Channel.BUFFERED)
    val messages = _messages.receiveAsFlow()
    private val moreLikeVideoFlows = mutableMapOf<Long, Flow<PagingData<LikeVideo>>>()
    private val pagingSources = mutableMapOf<Long, LikeVideoListDataSource>()

    fun hasLoadedData(mid: Long): Boolean = pagingSources[mid]?.hasCompletedRefresh == true

    fun getMoreLikeVideoFlow(mid: Long): Flow<PagingData<LikeVideo>> =
        moreLikeVideoFlows.getOrPut(mid) {
            Pager(
                config = PagingConfig(
                    pageSize = LikeVideoListDataSource.PAGE_SIZE,
                    initialLoadSize = LikeVideoListDataSource.PAGE_SIZE,
                    prefetchDistance = 5,
                    enablePlaceholders = true,
                ),
                pagingSourceFactory = {
                    LikeVideoListDataSource(mid, loadPage ?: userApi::getUserLikeVideoList)
                        .also { pagingSources[mid] = it }
                },
            ).flow.cachedIn(viewModelScope)
        }

    fun refresh(mid: Long, force: Boolean = false) {
        // 首次读取已经是最新数据；合并恢复显示和 Tab 激活产生的重复刷新。
        pagingSources[mid]?.takeIf { force || it.hasCompletedRefresh }?.invalidate()
    }

    fun cancelLike(mid: Long, video: LikeVideo) = performAction(video) { aid ->
        val response = userApi.cancelLike(aid)
        if (response.code != 0) error(response.message)
        // 操作成功后连正在读取的旧快照也要失效，不能被自动刷新的去重挡住。
        refresh(mid, force = true)
        _messages.send("已取消点赞")
    }

    fun addToWatchLater(video: LikeVideo) = performAction(video) { aid ->
        check(video.state) { "视频已失效，无法添加至稍后再看" }
        val response = watchLaterApi.add(aid)
        if (response.code != 0) {
            error(
                when (response.code) {
                    90001 -> "稍后再看列表已满，请先移除部分视频"
                    90003 -> "视频已被删除，无法添加至稍后再看"
                    else -> response.message
                }
            )
        }
        _messages.send("已添加至稍后再看")
    }

    private fun performAction(video: LikeVideo, action: suspend (Long) -> Unit) = launchTask {
        if (_isActionBusy.value) return@launchTask
        _isActionBusy.value = true
        try {
            check(LoginMapper.isLogin()) { "请先登录后操作" }
            val aid = video.param.toLongOrNull()?.takeIf { it > 0 } ?: error("无效的视频编号")
            action(aid)
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            _messages.send(exception.message ?: "操作失败，请重试")
        } finally {
            _isActionBusy.value = false
        }
    }

}
