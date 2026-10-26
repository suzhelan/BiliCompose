package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.SubmissionVideo
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.shared.auth.config.LoginMapper

data class SubmissionWatchHistoryState(
    val accountId: Long? = null,
    val byAid: Map<Long, WatchHistoryPage.Item> = emptyMap(),
    val byBvid: Map<String, WatchHistoryPage.Item> = emptyMap(),
) {
    fun find(video: SubmissionVideo): WatchHistoryPage.Item? =
        byAid[video.aid] ?: byBvid[video.bvid]

    fun lastWatchedKey(videos: List<SubmissionVideo>): String? = videos
        .mapNotNull { video -> find(video)?.viewAt?.takeIf { it > 0 }?.let { video.key to it } }
        .maxByOrNull { it.second }?.first
}

/** 一次读取最近观看记录补充时间，不为每张卡片单独请求，也不阻塞投稿列表。 */
class SubmissionWatchHistoryLoader(
    private val scope: CoroutineScope,
    private val fetch: suspend () -> WatchHistoryPage = {
        UserApi().getWatchHistory().also { check(it.code == 0) { it.message } }.data
    },
) {
    private val _state = MutableStateFlow(SubmissionWatchHistoryState())
    val state = _state.asStateFlow()
    private var job: Job? = null
    private var version = 0

    fun refresh(accountId: Long? = if (LoginMapper.isLogin()) LoginMapper.getMid() else null) {
        if (_state.value.accountId == accountId && job?.isActive == true) return
        job?.cancel()
        val requestVersion = ++version
        if (_state.value.accountId != accountId || accountId == null) {
            _state.value = SubmissionWatchHistoryState(accountId)
        }
        if (accountId == null) return
        job = scope.launch {
            try {
                val records = fetch().list.filter { it.history.business == "archive" }
                    .sortedByDescending { it.viewAt }
                if (requestVersion != version) return@launch
                _state.value = SubmissionWatchHistoryState(
                    accountId,
                    records.filter { it.history.oid > 0 }.distinctBy { it.history.oid }
                        .associateBy { it.history.oid },
                    records.filter { it.history.bvid.isNotBlank() }.distinctBy { it.history.bvid }
                        .associateBy { it.history.bvid },
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Exception) {
                // 历史不可用时仍显示投稿接口自己的百分比；不能把列表变为错误或清空已有记录。
            }
        }
    }
}
