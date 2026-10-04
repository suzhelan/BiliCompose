package top.suzhelan.bili.biz.user.viewmodel

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.biz.user.api.FavoriteApi
import top.suzhelan.bili.biz.user.entity.FavoriteFolder
import top.suzhelan.bili.biz.user.entity.FavoriteResource
import top.suzhelan.bili.shared.common.base.BaseViewModel

data class FavoriteFoldersUiState(
    val created: List<FavoriteFolder> = emptyList(),
    val collected: List<FavoriteFolder> = emptyList(),
    val collectedCount: Int = 0,
    val collectedPage: Int = 1,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isMutating: Boolean = false,
    val error: String? = null,
)

class FavoriteFoldersViewModel : BaseViewModel() {
    private val api = FavoriteApi()
    private val _state = MutableStateFlow(FavoriteFoldersUiState())
    val state = _state.asStateFlow()
    private var mid: Long = 0

    fun load(mid: Long, force: Boolean = false) = launchTask {
        if (!force && this@FavoriteFoldersViewModel.mid == mid && _state.value.created.isNotEmpty()) return@launchTask
        this@FavoriteFoldersViewModel.mid = mid
        _state.value = FavoriteFoldersUiState(isLoading = true)
        try {
            val created = api.getCreatedFolders(mid)
            val collected = api.getCollectedFolders(mid)
            if (created.code != 0) error(created.message)
            if (collected.code != 0) error(collected.message)
            _state.value = FavoriteFoldersUiState(
                created = created.data?.list.orEmpty(),
                collected = collected.data?.list.orEmpty(),
                collectedCount = collected.data?.count ?: 0,
            )
        } catch (exception: Exception) {
            _state.value = FavoriteFoldersUiState(error = exception.message ?: "收藏夹加载失败")
        }
    }

    fun loadMoreCollected() = launchTask {
        val current = _state.value
        if (mid == 0L || current.isLoadingMore || current.collected.size >= current.collectedCount) return@launchTask
        _state.value = current.copy(isLoadingMore = true)
        try {
            val nextPage = current.collectedPage + 1
            val response = api.getCollectedFolders(mid, page = nextPage)
            if (response.code != 0) error(response.message)
            _state.value = current.copy(
                collected = current.collected + response.data?.list.orEmpty(),
                collectedCount = response.data?.count ?: current.collectedCount,
                collectedPage = nextPage,
                isLoadingMore = false,
            )
        } catch (exception: Exception) {
            _state.value = current.copy(
                isLoadingMore = false,
                error = exception.message ?: "更多收藏夹加载失败",
            )
        }
    }

    fun createFolder(title: String, intro: String, isPrivate: Boolean, onSuccess: () -> Unit) =
        launchTask {
            if (_state.value.isMutating || mid == 0L) return@launchTask
            _state.value = _state.value.copy(isMutating = true, error = null)
            try {
                val response = api.createFolder(title, intro, isPrivate)
                if (response.code != 0) error(response.message)
                _state.value = _state.value.copy(
                    created = _state.value.created + response.data,
                    isMutating = false,
                )
                onSuccess()
            } catch (exception: Exception) {
                _state.value = _state.value.copy(
                    isMutating = false,
                    error = exception.message ?: "创建收藏夹失败",
                )
            }
        }
}

data class FavoriteDetailUiState(
    val folder: FavoriteFolder? = null,
    val resources: List<FavoriteResource> = emptyList(),
    val query: String = "",
    val order: String = "mtime",
    val page: Int = 1,
    val hasMore: Boolean = false,
    val isLoading: Boolean = false,
    val isLoadingMore: Boolean = false,
    val isMutating: Boolean = false,
    val isDeleted: Boolean = false,
    val error: String? = null,
)

class FavoriteDetailViewModel : BaseViewModel() {
    private val api = FavoriteApi()
    private val _state = MutableStateFlow(FavoriteDetailUiState())
    val state = _state.asStateFlow()
    private var mediaId: Long = 0

    fun load(
        mediaId: Long,
        query: String = _state.value.query,
        order: String = _state.value.order,
    ) = launchTask {
        reloadResources(mediaId, query, order)
    }

    private suspend fun reloadResources(
        mediaId: Long,
        query: String = _state.value.query,
        order: String = _state.value.order,
        hiddenResourceKeys: Set<String> = emptySet(),
    ): Boolean {
        val previous = _state.value
        this@FavoriteDetailViewModel.mediaId = mediaId
        _state.value =
            _state.value.copy(isLoading = true, error = null, query = query, order = order)
        try {
            val response = api.getFolderResources(mediaId, keyword = query, order = order)
            if (response.code != 0) error(response.message)
            val serverResources = response.data.medias.orEmpty()
            val containsStaleResources =
                serverResources.any { resourceKey(it) in hiddenResourceKeys }
            _state.value = FavoriteDetailUiState(
                // 清理接口与列表接口短时间内可能不同步，不能让旧响应重新插回已移除项。
                folder = if (containsStaleResources) {
                    response.data.info.copy(
                        mediaCount = previous.folder?.mediaCount ?: response.data.info.mediaCount
                    )
                } else {
                    response.data.info
                },
                resources = serverResources.filterNot { resourceKey(it) in hiddenResourceKeys },
                query = query,
                order = order,
                hasMore = response.data.hasMore,
                isMutating = _state.value.isMutating,
            )
            return containsStaleResources
        } catch (exception: Exception) {
            _state.value = _state.value.copy(
                isLoading = false,
                error = exception.message ?: "收藏夹加载失败",
            )
            return false
        }
    }

    fun loadMore() = launchTask {
        val current = _state.value
        if (!current.hasMore || current.isLoadingMore || mediaId == 0L) return@launchTask
        _state.value = current.copy(isLoadingMore = true)
        try {
            val next = current.page + 1
            val response = api.getFolderResources(
                mediaId = mediaId,
                page = next,
                keyword = current.query,
                order = current.order,
            )
            if (response.code != 0) error(response.message)
            _state.value = current.copy(
                folder = response.data.info,
                resources = current.resources + response.data.medias.orEmpty(),
                page = next,
                hasMore = response.data.hasMore,
                isLoadingMore = false,
            )
        } catch (exception: Exception) {
            _state.value = current.copy(
                isLoadingMore = false,
                error = exception.message ?: "更多视频加载失败",
            )
        }
    }

    fun edit(title: String, intro: String, isPrivate: Boolean, onSuccess: () -> Unit) = mutate {
        val response = api.editFolder(mediaId, title, intro, isPrivate)
        if (response.code != 0) error(response.message)
        reloadResources(mediaId)
        onSuccess()
    }

    fun cleanInvalid(onSuccess: () -> Unit) = mutate {
        val response = api.cleanInvalidResources(mediaId)
        if (response.code != 0) error(response.message)

        // 先移除当前页失效项，让界面立即反映成功的清理操作。
        val current = _state.value
        val removedResources = current.resources.filter { it.isInvalid }
        val removedResourceKeys = removedResources.mapTo(mutableSetOf()) { resourceKey(it) }
        val removedCount = removedResources.size
        _state.value = current.copy(
            resources = current.resources.filterNot { it.isInvalid },
            folder = current.folder?.copy(
                mediaCount = (current.folder.mediaCount - removedCount).coerceAtLeast(0),
            ),
        )
        // 第一次读到旧快照时保持乐观结果，并在服务端同步后自动再拉一次。
        val firstReloadWasStale = reloadResources(
            mediaId,
            current.query,
            current.order,
            removedResourceKeys,
        )
        if (firstReloadWasStale) {
            delay(800)
            reloadResources(mediaId, current.query, current.order, removedResourceKeys)
        }
        onSuccess()
    }

    private fun resourceKey(resource: FavoriteResource): String = "${resource.type}-${resource.id}"

    fun delete(onSuccess: () -> Unit) = mutate {
        val response = api.deleteFolder(mediaId)
        if (response.code != 0) error(response.message)
        _state.value = _state.value.copy(isDeleted = true)
        onSuccess()
    }

    private fun mutate(block: suspend () -> Unit) = launchTask {
        if (_state.value.isMutating) return@launchTask
        _state.value = _state.value.copy(isMutating = true, error = null)
        try {
            block()
        } catch (exception: Exception) {
            _state.value = _state.value.copy(error = exception.message ?: "操作失败")
        } finally {
            _state.value = _state.value.copy(isMutating = false)
        }
    }
}

data class FavoritePickerUiState(
    val folders: List<FavoriteFolder> = emptyList(),
    val selectedIds: Set<Long> = emptySet(),
    val initialIds: Set<Long> = emptySet(),
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
)

class FavoritePickerViewModel : BaseViewModel() {
    private val api = FavoriteApi()
    private val _state = MutableStateFlow(FavoritePickerUiState())
    val state = _state.asStateFlow()
    private var aid: Long = 0

    fun load(aid: Long) = launchTask {
        this@FavoritePickerViewModel.aid = aid
        _state.value = FavoritePickerUiState(isLoading = true)
        try {
            val response = api.getCreatedFolders(
                upMid = top.suzhelan.bili.shared.auth.config.LoginMapper.getMid(),
                resourceId = aid,
            )
            if (response.code != 0) error(response.message)
            val folders = response.data?.list.orEmpty()
            val selected = folders.filter { it.favoriteState == 1 }.mapTo(mutableSetOf()) { it.id }
            _state.value = FavoritePickerUiState(
                folders = folders,
                selectedIds = selected,
                initialIds = selected,
            )
        } catch (exception: Exception) {
            _state.value = FavoritePickerUiState(error = exception.message ?: "收藏夹加载失败")
        }
    }

    fun toggle(mediaId: Long) {
        val selected = _state.value.selectedIds.toMutableSet()
        if (!selected.add(mediaId)) selected.remove(mediaId)
        _state.value = _state.value.copy(selectedIds = selected)
    }

    fun createFolder(title: String, intro: String, isPrivate: Boolean) = launchTask {
        if (title.isBlank()) {
            _state.value = _state.value.copy(error = "收藏夹名称不能为空")
            return@launchTask
        }
        _state.value = _state.value.copy(isSaving = true, error = null)
        try {
            val response = api.createFolder(title, intro, isPrivate)
            if (response.code != 0) error(response.message)
            val folder = response.data
            _state.value = _state.value.copy(
                folders = _state.value.folders + folder,
                selectedIds = _state.value.selectedIds + folder.id,
                isSaving = false,
            )
        } catch (exception: Exception) {
            _state.value = _state.value.copy(
                isSaving = false,
                error = exception.message ?: "创建收藏夹失败",
            )
        }
    }

    fun save(onSuccess: (Boolean) -> Unit) = launchTask {
        val current = _state.value
        if (current.isSaving) return@launchTask
        _state.value = current.copy(isSaving = true, error = null)
        try {
            val additions = current.selectedIds - current.initialIds
            val deletions = current.initialIds - current.selectedIds
            if (additions.isEmpty() && deletions.isEmpty()) {
                _state.value = current.copy(isSaving = false)
                onSuccess(current.selectedIds.isNotEmpty())
                return@launchTask
            }
            val response = api.updateVideoFolders(
                aid = aid,
                addMediaIds = additions,
                deleteMediaIds = deletions,
            )
            if (response.code != 0) error(response.message)
            _state.value = current.copy(
                initialIds = current.selectedIds,
                isSaving = false,
            )
            onSuccess(current.selectedIds.isNotEmpty())
        } catch (exception: Exception) {
            _state.value = current.copy(
                isSaving = false,
                error = exception.message ?: "收藏操作失败",
            )
        }
    }
}
