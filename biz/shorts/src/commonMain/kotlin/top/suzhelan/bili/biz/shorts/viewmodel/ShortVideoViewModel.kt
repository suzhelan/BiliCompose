package top.suzhelan.bili.biz.shorts.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.shorts.data.ShortVideoDataSource
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.shared.common.base.BaseViewModel
import top.suzhelan.bili.shared.common.logger.LogUtils
import top.suzhelan.bili.shared.common.util.toStringCount

/**
 * 短视频ViewModel
 *
 * 负责管理短视频列表的状态、播放控制和数据加载
 * 采用MVVM架构，通过DataSource层获取数据
 */
class ShortVideoViewModel : BaseViewModel() {

    private val dataSource = ShortVideoDataSource()

    /**
     * 视频列表状态
     */
    private val _videoList = MutableStateFlow<List<ShortVideoItem>>(emptyList())
    val videoList: StateFlow<List<ShortVideoItem>> = _videoList.asStateFlow()

    /**
     * 当前播放索引
     */
    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex: StateFlow<Int> = _currentPlayingIndex.asStateFlow()

    /**
     * 加载状态
     */
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /**
     * 错误信息
     */
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    /**
     * 会话ID - 每次初始化时生成新的，确保每次进入都是全新状态
     */
    private val _sessionId = MutableStateFlow("")
    val sessionId: StateFlow<String> = _sessionId.asStateFlow()

    /**
     * 是否已初始化
     */
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    /**
     * 视频缓存池大小限制
     */
    private val maxPoolSize = 50

    /**
     * 视频缓存池 - 使用LinkedHashMap保持插入顺序
     */
    private val videoPoolMap = LinkedHashMap<Long, ShortVideoItem>()

    /**
     * 作者头像缓存 - 避免重复请求
     */
    private val authorAvatarCache = mutableMapOf<Long, String>()

    /**
     * 作者粉丝数缓存 - 避免重复请求
     */
    private val authorFollowerCountCache = mutableMapOf<Long, Int>()

    /**
     * 作者关注状态缓存 - key为authorId, value为关注状态(0:未关注 2:已关注 6:已互粉)
     */
    private val followStateCache = mutableMapOf<Long, Int>()

    /**
     * 视频点赞状态缓存 - key为aid, value为是否已点赞
     */
    private val likeStateCache = mutableMapOf<Long, Boolean>()

    /**
     * 初始化会话
     *
     * 每次进入Screen时调用，重置所有状态并开始新的播放会话
     *
     * @param initialAid 初始视频的aid
     * @param initialVideo 初始视频数据（可选）
     */
    fun initSession(initialAid: Long, initialVideo: SmallCoverV2Item?) {
        // 生成新的会话ID
        _sessionId.value = "${initialAid}-${(0..Long.MAX_VALUE).random()}"

        // 重置所有状态
        resetState()

        LogUtils.d("ShortVideoViewModel: 初始化新会话 sessionId=${_sessionId.value}")

        // 如果有初始视频，添加到列表
        if (initialVideo != null) {
            val shortVideo = ShortVideoItem.fromSmallCoverV2Item(initialVideo)
            addVideoToPool(shortVideo, atStart = true)
            _isInitialized.value = true

            // 立即加载更多视频
            loadMoreVideos()

            // 异步加载初始视频的作者信息
            launchTask {
                fetchAuthorInfoForVideos(listOf(shortVideo))
            }
        } else {
            // 没有初始视频，直接加载
            loadMoreVideos()
        }
    }

    /**
     * 更新当前播放索引
     *
     * @param index 新的播放索引
     */
    fun updatePlayingIndex(index: Int) {
        val videoList = _videoList.value
        if (index in videoList.indices) {
            _currentPlayingIndex.value = index

            // 检查是否需要加载更多
            if (shouldLoadMore(index)) {
                loadMoreVideos()
            }
        }
    }

    /**
     * 加载更多视频
     *
     * 从数据源获取新的视频数据并添加到缓存池
     */
    fun loadMoreVideos() {
        if (_isLoading.value) return

        launchTask {
            _isLoading.value = true
            _errorMessage.value = null

            when (val result = dataSource.loadVideos()) {
                is ShortVideoDataSource.LoadResult.Success -> {
                    // 添加新视频到缓存池
                    result.videos.forEach { video ->
                        addVideoToPool(video, atStart = false)
                    }

                    LogUtils.d("ShortVideoViewModel: 成功加载 ${result.videos.size} 个视频，缓存池共 ${_videoList.value.size} 个")

                    // 如果未初始化且现在有视频了，标记为已初始化
                    if (!_isInitialized.value && _videoList.value.isNotEmpty()) {
                        _isInitialized.value = true
                    }

                    // 如果仍然没有视频，显示提示
                    if (_videoList.value.isEmpty()) {
                        _errorMessage.value = "暂时没有视频，请稍后再试"
                    } else {
                        // 异步加载作者头像
                        fetchAuthorAvatarsForVideos(_videoList.value)

                        // 异步加载视频统计信息
                        fetchVideoStatsForVideos(_videoList.value)
                    }
                }

                is ShortVideoDataSource.LoadResult.Error -> {
                    _errorMessage.value = result.message
                    LogUtils.e("ShortVideoViewModel: 加载失败 - ${result.message}")
                }
            }

            _isLoading.value = false
        }
    }

    /**
     * 关注/取消关注作者
     *
     * @param authorId 作者ID
     * @param currentFollowState 当前关注状态(0:未关注 2:已关注 6:已互粉)
     */
    fun toggleFollow(authorId: Long, currentFollowState: Int) {
        launchTask {
            try {
                // 判断是关注还是取消关注
                val isFollow = currentFollowState == 0

                LogUtils.d("ShortVideoViewModel: ${if (isFollow) "关注" else "取消关注"} 用户 - authorId=$authorId")

                val result = dataSource.modifyFollow(authorId, isFollow)

                result.onSuccess { message ->
                    // 更新缓存的关注状态
                    val newState = if (isFollow) 2 else 0
                    followStateCache[authorId] = newState

                    // 显示成功消息
                    showMessageDialog(title = "提示", message = message)
                    LogUtils.d("ShortVideoViewModel: 关注操作成功 - $message")
                }.onFailure { e ->
                    // 显示错误消息
                    showMessageDialog(title = "错误", message = e.message ?: "操作失败")
                    LogUtils.e("ShortVideoViewModel: 关注操作失败 - ${e.message}")
                }

            } catch (e: Exception) {
                showMessageDialog(title = "错误", message = "操作失败: ${e.message}")
                LogUtils.e("ShortVideoViewModel: 关注操作异常", e)
            }
        }
    }

    /**
     * 查询作者关注状态
     *
     * @param authorId 作者ID
     * @return 关注状态 - 0:未关注 2:已关注 6:已互粉，null:查询失败
     */
    suspend fun queryFollowState(authorId: Long): Int? {
        // 先检查缓存
        followStateCache[authorId]?.let { return it }

        // 从数据源查询
        val state = dataSource.queryFollowState(authorId)

        // 更新缓存
        state?.let { followStateCache[authorId] = it }

        return state
    }

    /**
     * 查询视频点赞状态
     *
     * @param aid 视频aid
     * @return 是否已点赞，null:查询失败
     */
    suspend fun queryLikeState(aid: Long): Boolean? {
        // 先检查缓存
        likeStateCache[aid]?.let { return it }

        // 从数据源查询
        val state = dataSource.queryLikeState(aid)

        // 更新缓存
        state?.let { likeStateCache[aid] = it }

        return state
    }

    /**
     * 点赞或取消点赞视频
     *
     * @param aid 视频aid
     * @param currentLikeState 当前点赞状态
     */
    fun toggleLike(aid: Long, currentLikeState: Boolean) {
        launchTask {
            try {
                // 新的点赞状态
                val newLikeState = !currentLikeState

                LogUtils.d("ShortVideoViewModel: ${if (newLikeState) "点赞" else "取消点赞"} 视频 - aid=$aid")

                val result = dataSource.toggleLike(aid, newLikeState)

                result.onSuccess { message ->
                    // 更新缓存的点赞状态
                    likeStateCache[aid] = newLikeState

                    LogUtils.d("ShortVideoViewModel: 点赞操作成功 - $message")
                }.onFailure { e ->
                    // 显示错误消息
                    showMessageDialog(title = "错误", message = e.message ?: "操作失败")
                    LogUtils.e("ShortVideoViewModel: 点赞操作失败 - ${e.message}")
                }

            } catch (e: Exception) {
                showMessageDialog(title = "错误", message = "操作失败: ${e.message}")
                LogUtils.e("ShortVideoViewModel: 点赞操作异常", e)
            }
        }
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }

    /**
     * 重置状态
     */
    private fun resetState() {
        _isInitialized.value = false
        _currentPlayingIndex.value = 0
        _errorMessage.value = null
        videoPoolMap.clear()
        _videoList.value = emptyList()
    }

    /**
     * 添加视频到缓存池
     *
     * @param video 要添加的视频
     * @param atStart 是否添加到开头
     */
    private fun addVideoToPool(video: ShortVideoItem, atStart: Boolean = false) {
        if (atStart) {
            // 添加到开头
            val newMap = LinkedHashMap<Long, ShortVideoItem>()
            newMap[video.aid] = video
            videoPoolMap.forEach { (aid, item) ->
                if (aid != video.aid) {
                    newMap[aid] = item
                }
            }
            videoPoolMap.clear()
            videoPoolMap.putAll(newMap)
        } else {
            // 添加到末尾（去重）
            if (!videoPoolMap.containsKey(video.aid)) {
                videoPoolMap[video.aid] = video
            }
        }

        // 限制缓存池大小
        while (videoPoolMap.size > maxPoolSize) {
            val firstKey = videoPoolMap.keys.first()
            videoPoolMap.remove(firstKey)
        }

        // 更新列表
        _videoList.value = videoPoolMap.values.toList()
    }

    /**
     * 检查是否需要加载更多视频
     *
     * @param currentIndex 当前播放索引
     * @return 是否需要加载更多
     */
    private fun shouldLoadMore(currentIndex: Int): Boolean {
        val pool = _videoList.value
        return pool.size - currentIndex < 5 // 剩余不足5个时加载更多
    }

    /**
     * 批量获取作者头像
     *
     * @param videoList 需要获取头像的视频列表
     */
    private suspend fun fetchAuthorAvatarsForVideos(videoList: List<ShortVideoItem>) {
        // 获取需要加载头像的作者ID（过滤已有头像和已缓存的）
        val missingAvatars = videoList
            .filter { it.authorAvatar.isEmpty() && it.authorId != 0L }
            .map { it.authorId }
            .distinct()
            .filter { !authorAvatarCache.containsKey(it) }

        if (missingAvatars.isEmpty()) {
            // 如果所有头像都已加载，直接更新视频列表
            updateVideosWithCachedAvatars()
            return
        }

        // 从数据源获取头像
        val avatarMap = dataSource.fetchAuthorAvatars(missingAvatars)

        // 更新缓存
        authorAvatarCache.putAll(avatarMap)

        // 更新视频列表中的头像
        updateVideosWithCachedAvatars()
    }

    /**
     * 使用缓存的头像更新视频列表
     */
    private fun updateVideosWithCachedAvatars() {
        val updatedVideos = videoPoolMap.mapValues { (_, video) ->
            var updatedVideo = video

            // 更新头像
            if (video.authorAvatar.isEmpty() && authorAvatarCache.containsKey(video.authorId)) {
                updatedVideo =
                    updatedVideo.copy(authorAvatar = authorAvatarCache[video.authorId] ?: "")
            }

            // 更新粉丝数
            if (video.followerCount == 0 && authorFollowerCountCache.containsKey(video.authorId)) {
                updatedVideo =
                    updatedVideo.copy(followerCount = authorFollowerCountCache[video.authorId] ?: 0)
            }

            updatedVideo
        }

        videoPoolMap.clear()
        videoPoolMap.putAll(updatedVideos)
        _videoList.value = videoPoolMap.values.toList()
    }

    /**
     * 批量获取作者信息（包括头像和粉丝数）
     *
     * @param videoList 需要获取信息的视频列表
     */
    private suspend fun fetchAuthorInfoForVideos(videoList: List<ShortVideoItem>) {
        // 获取需要加载信息的作者ID
        val missingAuthorIds = videoList
            .filter { it.authorId != 0L }
            .map { it.authorId }
            .distinct()
            .filter {
                !authorAvatarCache.containsKey(it) || !authorFollowerCountCache.containsKey(it)
            }

        if (missingAuthorIds.isEmpty()) {
            updateVideosWithCachedAvatars()
            return
        }

        // 从数据源获取头像和粉丝数
        val avatarMap = dataSource.fetchAuthorAvatars(missingAuthorIds)
        val followerMap = dataSource.fetchAuthorFollowerCounts(missingAuthorIds)

        // 更新缓存
        authorAvatarCache.putAll(avatarMap)
        authorFollowerCountCache.putAll(followerMap)

        // 更新视频列表
        updateVideosWithCachedAvatars()
    }

    /**
     * 批量获取视频统计信息
     *
     * @param videoList 需要获取统计信息的视频列表
     */
    private suspend fun fetchVideoStatsForVideos(videoList: List<ShortVideoItem>) {
        // 只获取当前可见及附近几个视频的统计信息，避免一次性请求过多
        val currentIndex = _currentPlayingIndex.value
        val startIndex = maxOf(0, currentIndex - 2)
        val endIndex = minOf(videoList.size - 1, currentIndex + 5)

        val videosToFetch = videoList.subList(startIndex, endIndex + 1)
            .filter { !it.hasCount } // 只获取还没有统计数据的视频

        videosToFetch.forEach { video ->
            try {
                val stats = dataSource.fetchVideoStats(video.aid)
                stats.let { info ->
                    // 更新视频统计信息
                    val updatedVideo = video.copy(
                        likeCount = info.stat.like.toStringCount(),
                        coinCount = info.stat.coin.toStringCount(),
                        favoriteCount = info.stat.favorite.toStringCount(),
                        shareCount = info.stat.share.toStringCount(),
                        commentCount = info.stat.reply.toStringCount(),
                        danmakuCount = info.stat.danmaku.toStringCount(),
                        hasCount = true
                    )
                    // 更新缓存池中的视频
                    videoPoolMap[video.aid] = updatedVideo
                }
            } catch (e: Exception) {
                LogUtils.e("ShortVideoViewModel: 获取视频统计失败 - aid=${video.aid}", e)
            }
        }

        // 更新列表
        _videoList.value = videoPoolMap.values.toList()
    }

    /**
     * 清理资源
     */
    override fun onCleared() {
        super.onCleared()
        videoPoolMap.clear()
        authorAvatarCache.clear()
        _videoList.value = emptyList()
        LogUtils.d("ShortVideoViewModel: 清理资源")
    }
}
