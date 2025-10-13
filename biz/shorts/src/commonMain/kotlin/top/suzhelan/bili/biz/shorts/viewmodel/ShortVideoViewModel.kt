package top.suzhelan.bili.biz.shorts.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.shorts.data.ShortVideoDataSource
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.shared.common.base.BaseViewModel
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 短视频ViewModel
 *
 * 负责管理短视频列表的状态、播放控制和数据加载
 * 采用MVVM架构，通过DataSource层获取数据
 *
 * ## 功能职责
 * - 管理视频列表和播放状态
 * - 控制视频缓存池大小
 * - 处理会话生命周期
 * - 协调数据加载和UI更新
 *
 * @author suzhelan
 */
class ShortVideoViewModel : BaseViewModel() {

    private val dataSource = ShortVideoDataSource()

    // ==================== 状态管理 ====================

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

    // ==================== 私有属性 ====================

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

    // ==================== 公共方法 ====================

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

            // 异步加载初始视频的作者头像
            launchTask {
                fetchAuthorAvatarsForVideos(listOf(shortVideo))
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
     * 清除错误信息
     */
    fun clearError() {
        _errorMessage.value = null
    }

    // ==================== 私有方法 ====================

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
            if (video.authorAvatar.isEmpty() && authorAvatarCache.containsKey(video.authorId)) {
                video.copy(authorAvatar = authorAvatarCache[video.authorId] ?: "")
            } else {
                video
            }
        }

        videoPoolMap.clear()
        videoPoolMap.putAll(updatedVideos)
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
