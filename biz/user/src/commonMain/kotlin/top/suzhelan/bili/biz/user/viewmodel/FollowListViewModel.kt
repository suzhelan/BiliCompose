package top.suzhelan.bili.biz.user.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableStateSetOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.RelationApi.Companion.LIST_PAGE_SIZE
import top.suzhelan.bili.biz.user.data.FollowListDataSource
import top.suzhelan.bili.biz.user.entity.RelationListUser
import top.suzhelan.bili.biz.user.entity.RelationTags
import top.suzhelan.bili.biz.user.util.RelationUtils
import top.suzhelan.bili.shared.common.base.BaseViewModel

class FollowListViewModel(
    private val mid: Long? = null,
    private val fans: Boolean = false,
) : BaseViewModel() {

    private val api = RelationApi()
    private val relationLookupPermits = Semaphore(4)
    private val followListFlows = mutableMapOf<Int, Flow<PagingData<RelationListUser>>>()
    private val listRefreshVersions = mutableMapOf<Int, Int>()
    private var hasResumed = false
    var refreshVersion by mutableIntStateOf(0)
        private set

    var tags by mutableStateOf<List<RelationTags>>(emptyList())
        private set
    val tagsError = MutableStateFlow<String?>(null)

    fun onScreenResumed() {
        if (hasResumed) refreshVersion++
        hasResumed = true
    }

    fun refreshListIfNeeded(tagId: Int, refresh: () -> Unit) {
        if (listRefreshVersions[tagId] == refreshVersion) return
        listRefreshVersions[tagId] = refreshVersion
        refresh()
    }

    /**
     * 查询分组
     */
    fun queryTags() = launchTask {
        tagsError.value = null
        val resultTags = try {
            api.queryTags().data
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            tagsError.value = e.message ?: "关注分组加载失败"
            return@launchTask
        }
        tags = listOf(
            RelationTags(
                count = 0,
                name = "全部",
                tagid = -20,
                tip = "All"
            )
        ) + resultTags
    }


    private val pendingRelationActions = mutableStateSetOf<Long>()

    fun isRelationActionRunning(mid: Long) = mid in pendingRelationActions

    /**
     * 取消关注
     */
    fun cancelFollow(mid: Long, onUserUpdate: (Int?) -> Unit) = modifyFollow(mid, 2, onUserUpdate)

    /**
     * 关注用户
     */
    fun addFollow(mid: Long, onUserUpdate: (Int?) -> Unit) = modifyFollow(mid, 1, onUserUpdate)

    fun retryRelation(mid: Long, onUserUpdate: (Int) -> Unit) =
        runRelationAction(mid) {
            onUserUpdate(api.queryRelation(mid).data.attribute)
        }

    suspend fun resolveMissingRelation(item: RelationListUser) {
        if (!item.needsRelationLookup) return
        val attribute = try {
            relationLookupPermits.withPermit {
                if (!item.needsRelationLookup) return
                api.queryRelation(item.user.mid).data.attribute
            }
        } catch (e: CancellationException) {
            throw e
        } catch (_: Exception) {
            null
        }
        // 用户操作已返回时，后台补查的结果可能早于本次操作。
        if (!item.needsRelationLookup) return
        if (attribute != null) item.attribute = attribute
        item.needsRelationLookup = false
    }

    private fun modifyFollow(mid: Long, action: Int, onUserUpdate: (Int?) -> Unit) =
        runRelationAction(mid) {
            val result = api.modify(mid, action)
            if (result.code != 0) {
                showMessageDialog(message = RelationUtils.getToastByModifyResult(result))
                return@runRelationAction
            }
            // 修改已成功，查询失败时让用户重试查询，避免继续显示并执行旧操作。
            onUserUpdate(null)
            onUserUpdate(api.queryRelation(mid).data.attribute)
        }

    private fun runRelationAction(
        mid: Long,
        action: suspend () -> Unit,
    ) = launchTask {
        if (!pendingRelationActions.add(mid)) return@launchTask
        try {
            action()
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            showMessageDialog(message = ApiException.build(e).message)
        } finally {
            pendingRelationActions.remove(mid)
        }
    }

    fun getFollowListFlow(tagId: Int): Flow<PagingData<RelationListUser>> =
        followListFlows.getOrPut(tagId) {
            listRefreshVersions[tagId] = refreshVersion
            Pager(
                config = PagingConfig(
                    pageSize = LIST_PAGE_SIZE,
                    initialLoadSize = LIST_PAGE_SIZE,
                    prefetchDistance = 5,
                    enablePlaceholders = false
                ),
                pagingSourceFactory = {
                    FollowListDataSource(tagId, mid, fans, api)
                }
            ).flow.cachedIn(viewModelScope)
        }


    //是否展示设置分组的对话框
    val isShowSettingTagsDialog = MutableStateFlow(false)
    val tagSettingsMid = MutableStateFlow(0L)
    fun showSettingTagsDialog(targetMid: Long) {
        tagSettingsMid.value = targetMid
        isShowSettingTagsDialog.value = true
        getUserInTags(targetMid)
    }


    //分组选中状态
    private val _tagsCheckedMap = mutableStateMapOf<Int, Boolean>()
    val tagsCheckedMap: Map<Int, Boolean> = _tagsCheckedMap
    fun updateTagsCheckedMap(tagId: Int, checked: Boolean) {
        _tagsCheckedMap[tagId] = checked
    }

    //用户所在分组
    val userInTags = MutableStateFlow<BiliResponse<Map<Int, String>>>(BiliResponse.Loading)
    private fun getUserInTags(mid: Long) = launchTask {
        _tagsCheckedMap.clear()
        tags.forEach { tags ->
            _tagsCheckedMap[tags.tagid] = false
        }
        userInTags.value = BiliResponse.Loading
        userInTags.value = apiCall {
            api.queryUserInTags(mid)
        }
        if (userInTags.value.isSuccess()) {
            val inTags = userInTags.value as BiliResponse.Success<Map<Int, String>>
            // 先重置所有为false
            _tagsCheckedMap.keys.forEach { tagId ->
                _tagsCheckedMap[tagId] = false
            }
            // 再将用户所在分组设为true
            for (tagId in inTags.data.keys) {
                _tagsCheckedMap[tagId] = true
            }
        }
    }

    val isShowCreateTagDialog = MutableStateFlow(false)


    fun createTag(tagName: String, onSuccess: () -> Unit) = launchTask {
        setShowLoading()
        val result = api.createTag(tagName)
        if (result.code == 0) {
            onSuccess()
            dismissDialog()
        } else {
            showMessageDialog(message = result.message)
        }
    }.invokeOnCompletion { e ->
        e?.let {
            showMessageDialog(message = it.message ?: "")
        }
    }

    val isShowRenameTagDialog = MutableStateFlow(false)
    val renameTag = MutableStateFlow(RelationTags(0, "", 0, ""))
    fun showRenameTagDialog(tag: RelationTags) {
        renameTag.value = tag
        isShowRenameTagDialog.value = true
    }

    /**
     * 重命名分组
     */
    fun renameTag(tagId: Int, tagName: String, onSuccess: () -> Unit) = launchTask {
        setShowLoading()
        val result = api.renameTag(tagId, tagName)
        if (result.code == 0) {
            onSuccess()
            dismissDialog()
        } else {
            showMessageDialog(message = result.message)
        }
    }.invokeOnCompletion { e ->
        e?.let {
            showMessageDialog(message = it.message ?: "")
        }
    }


    val isShowDeleteTagDialog = MutableStateFlow(false)
    val deleteTagId = MutableStateFlow(0)

    /**
     * 判断该分组是否有用户
     */
    fun hasUserInTag(tagId: Int, onSuccess: (haUser: Boolean) -> Unit) = launchTask {
        val tag = api.queryFollowList(tagId = tagId)
        deleteTagId.value = tagId
        if (tag.code == 0) {
            val tag = tag.data
            onSuccess(tag.isNotEmpty())
        } else {
            showMessageDialog(message = tag.message)
        }
    }

    fun deleteTag(tagId: Int) = launchTask {
        val result = api.deleteTag(tagId)
        if (result.code == 0) {
            queryTags()
        } else {
            showMessageDialog(message = result.message)
        }
    }.invokeOnCompletion { e ->
        e?.let {
            showMessageDialog(message = it.message ?: "")
        }
    }

    /**
     * 保存弹窗中的用户分组信息
     * 其实可以用 [RelationApi.moveUserToTag] 即可一次完成请求 但是实在太难用
     */
    fun saveUserInTagInfo(onSuccess: () -> Unit) = launchTask {
        //从已选择的tagId筛选出移动目标的tagId
        val targetTagIds = tagsCheckedMap.filter {
            it.value
        }.map { it.key }
        //先把用户从别的分组移动到默认分组
        api.deleteUserFromTag(tagSettingsMid.value).apply {
            if (code == 0) {
                onSuccess()
            } else {
                showMessageDialog(message = message)
            }
        }
        //再把用户添加到目标分组
        if (targetTagIds.isNotEmpty()) {
            api.addUserToTag(listOf(tagSettingsMid.value), targetTagIds).apply {
                if (code == 0) {
                    onSuccess()
                } else {
                    showMessageDialog(message = message)
                }
            }
        }
    }.invokeOnCompletion {
        it?.let {
            showMessageDialog(message = it.message ?: "")
        }
    }
}
