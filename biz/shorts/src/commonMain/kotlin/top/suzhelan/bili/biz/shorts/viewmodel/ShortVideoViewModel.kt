package top.suzhelan.bili.biz.shorts.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.jsonPrimitive
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.recvids.api.FeedApi
import top.suzhelan.bili.biz.recvids.config.BaseCoverSerializer
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.recvids.entity.targetCardType
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.shared.common.base.BaseViewModel
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 短视频ViewModel
 * 负责管理短视频列表、播放状态和数据加载
 */
class ShortVideoViewModel : BaseViewModel() {

    private val feedApi = FeedApi()
    private val userApi = UserApi()

    // 视频列表 - 直接在 ViewModel 中管理，不使用全局单例
    private val _videoList = MutableStateFlow<List<ShortVideoItem>>(emptyList())
    val videoList: StateFlow<List<ShortVideoItem>> = _videoList.asStateFlow()

    // 作者头像缓存 - 避免重复请求
    private val authorAvatarCache = mutableMapOf<Long, String>()

    // 当前播放索引
    private val _currentPlayingIndex = MutableStateFlow(0)
    val currentPlayingIndex: StateFlow<Int> = _currentPlayingIndex.asStateFlow()

    // 加载状态
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 错误信息
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    // 会话ID - 每次初始化时生成新的，确保每次进入都是全新状态
    private val _sessionId = MutableStateFlow("")
    val sessionId: StateFlow<String> = _sessionId.asStateFlow()

    // 是否已初始化
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    // 视频缓存池大小限制
    private val maxPoolSize = 50

    // 使用 LinkedHashMap 保持视频顺序
    private val videoPoolMap = LinkedHashMap<Long, ShortVideoItem>()

    /**
     * 初始化会话 - 每次进入 Screen 时调用
     */
    fun initSession(initialAid: Long, initialVideo: SmallCoverV2Item?) {
        // 生成新的会话ID - 使用随机数确保唯一性
        _sessionId.value = "${initialAid}-${(0..Long.MAX_VALUE).random()}"

        // 重置所有状态
        _isInitialized.value = false
        _currentPlayingIndex.value = 0
        _errorMessage.value = null
        videoPoolMap.clear()
        _videoList.value = emptyList()

        LogUtils.d("ShortVideoViewModel: 初始化新会话 sessionId=${_sessionId.value}")

        // 如果有初始视频，添加到列表（不再检查竖屏标签）
        if (initialVideo != null) {
            val shortVideo = ShortVideoItem.fromSmallCoverV2Item(initialVideo)
            addVideoToPool(shortVideo, atStart = true)
            _isInitialized.value = true

            // 立即加载更多视频
            loadMoreVideos()

            // 异步加载初始视频的作者头像
            launchTask {
                fetchAuthorAvatars(listOf(shortVideo))
            }
        } else {
            // 没有初始视频，直接加载
            loadMoreVideos()
        }
    }

    /**
     * 添加视频到缓存池
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
     * 批量添加视频 - 不再筛选竖屏标签，添加所有视频
     */
    private fun addVideosFromFeed(items: List<SmallCoverV2Item>) {
        // 直接转换所有视频，不再筛选竖屏标签
        items.forEach { item ->
            val video = ShortVideoItem.fromSmallCoverV2Item(item)
            addVideoToPool(video, atStart = false)
        }
    }

    /**
     * 更新当前播放索引
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
     * 检查是否需要加载更多视频
     */
    private fun shouldLoadMore(currentIndex: Int): Boolean {
        val pool = _videoList.value
        return pool.size - currentIndex < 5 // 剩余不足5个时加载更多
    }

    /**
     * 批量获取作者头像
     */
    private suspend fun fetchAuthorAvatars(videoList: List<ShortVideoItem>) {
        // 获取需要加载头像的作者ID
        val missingAvatars = videoList
            .filter { it.authorAvatar.isEmpty() && it.authorId != 0L }
            .map { it.authorId }
            .distinct()
            .filter { !authorAvatarCache.containsKey(it) }

        if (missingAvatars.isEmpty()) return

        try {
            val avatarMap = mutableMapOf<Long, String>()

            // 并发获取头像
            missingAvatars.chunked(5).forEach { chunk ->
                val results = chunk.mapNotNull { authorId ->
                    if (authorId == 0L) return@mapNotNull null

                    val response = userApi.getUserInfo(authorId, isWithPhoto = true)
                    if (response.isSuccess()) {
                        authorId to response.getOrThrow().card.face
                    } else {
                        LogUtils.w("获取用户头像失败: authorId=$authorId")
                        null
                    }
                }
                avatarMap.putAll(results)
            }

            // 更新缓存
            authorAvatarCache.putAll(avatarMap)

            // 更新视频列表
            val updatedVideos = _videoList.value.map { video ->
                if (video.authorAvatar.isEmpty() && avatarMap.containsKey(video.authorId)) {
                    video.copy(authorAvatar = avatarMap[video.authorId] ?: "")
                } else {
                    video
                }
            }

            _videoList.value = updatedVideos
            LogUtils.d("成功获取 ${avatarMap.size} 个作者头像")

        } catch (e: Exception) {
            LogUtils.e("批量获取作者头像失败", e)
        }
    }

    /**
     * 从推荐API加载更多视频
     */
    fun loadMoreVideos() {
        if (_isLoading.value) return

        launchTask {
            _isLoading.value = true
            _errorMessage.value = null

            try {
                // 先加载视频列表
                val response = feedApi.getFeed()

                if (response.isSuccess()) {
                    val data = response.getOrThrow()

                    val items = data.items
                        .filter {
                            it["card_type"]?.jsonPrimitive?.content == SmallCoverV2Item.targetCardType
                        }
                        .mapNotNull {
                            runCatching {
                                HttpJsonDecoder.decodeFromJsonElement(BaseCoverSerializer, it)
                            }.onFailure { e ->
                                LogUtils.e("视频解析失败", e)
                            }.getOrNull()
                        }
                        .filterIsInstance<SmallCoverV2Item>()

                    // 添加所有解析成功的视频
                    addVideosFromFeed(items)

                    LogUtils.d("短视频加载成功: 本次加载了 ${items.size} 个视频，缓存池共有 ${_videoList.value.size} 个视频")

                    // 如果没有初始化且现在有视频了，标记为已初始化
                    if (!_isInitialized.value && _videoList.value.isNotEmpty()) {
                        _isInitialized.value = true
                    }

                    if (_videoList.value.isEmpty()) {
                        _errorMessage.value = "暂时没有视频，请稍后再试"
                        LogUtils.w("短视频加载完成但没有找到视频")
                    } else {
                        // 异步加载作者头像
                        fetchAuthorAvatars(_videoList.value)
                    }
                } else {
                    _errorMessage.value = "加载失败: ${response.message}"
                    LogUtils.e("短视频加载失败: ${response.code} ${response.message}")
                }
            } catch (e: Exception) {
                _errorMessage.value = "网络错误: ${e.message}"
                LogUtils.e("短视频加载异常", e)
            } finally {
                _isLoading.value = false
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
     * 清理资源 - ViewModel 被销毁时调用
     */
    override fun onCleared() {
        super.onCleared()
        videoPoolMap.clear()
        _videoList.value = emptyList()
        LogUtils.d("ShortVideoViewModel: 清理资源")
    }
}
