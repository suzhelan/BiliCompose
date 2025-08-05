package top.sacz.bili.biz.user.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import kotlinx.coroutines.flow.MutableStateFlow
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.api.isSuccess
import top.sacz.bili.biz.user.api.RelationApi
import top.sacz.bili.biz.user.data.FollowListDataSource
import top.sacz.bili.biz.user.entity.RelationTags
import top.sacz.bili.biz.user.util.RelationUtils
import top.sacz.bili.shared.common.base.BaseViewModel

class FollowListViewModel : BaseViewModel() {

    private val api = RelationApi()

    private val _tags = mutableStateListOf<RelationTags>()
    val tags: List<RelationTags> = _tags

    /**
     * 查询分组
     */
    fun queryTags() = launchTask {
        val resultTags = api.queryTags().data
        _tags.clear()
        _tags.add(
            0,
            RelationTags(
                count = 0,
                name = "全部",
                tagid = -20,
                tip = "All"
            )
        )
        _tags.addAll(resultTags)
    }


    val followList = mutableStateMapOf<Long, BiliResponse<*>>()


    /**
     * 取消关注
     */
    fun cancelFollow(mid: Long, onUserUpdate: (Int) -> Unit) = launchTask {
        followList[mid] = BiliResponse.Loading
        val result = api.modify(mid, 2)
        if (result.code == 0) {
            //只更新关系
            val relation = api.queryRelation(mid).data
            onUserUpdate(relation.attribute)
        } else {
            showMessageDialog(message = RelationUtils.getToastByModifyResult(result))
        }
        followList[mid] = result
    }

    /**
     * 关注用户
     */
    fun addFollow(mid: Long, onUserUpdate: (Int) -> Unit) = launchTask {
        followList[mid] = BiliResponse.Loading
        val result = api.modify(mid, 1)
        if (result.code == 0) {
            //只更新关系
            val relation = api.queryRelation(mid).data
            onUserUpdate(relation.attribute)
        } else {
            showMessageDialog(message = RelationUtils.getToastByModifyResult(result))
        }
        followList[mid] = result
    }

    fun getFollowListFlow(tagId: Int) = Pager(
        config = PagingConfig(
            pageSize = 10,
            prefetchDistance = 5,//提前多少页开始预加载
            enablePlaceholders = true
        ),
        pagingSourceFactory = {
            FollowListDataSource(tagId)
        }
    ).flow.cachedIn(viewModelScope)


    //是否展示设置分组的对话框
    val isShowSettingTagsDialog = MutableStateFlow(false)
    val tagSettingsMid = MutableStateFlow(0L)
    fun openSettingTagsDialog(targetMid: Long) {
        tagSettingsMid.value = targetMid
        isShowSettingTagsDialog.value = true
        getUserInTags(targetMid)
    }

    fun closeSettingTagsDialog() {
        isShowSettingTagsDialog.value = false
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

    fun openCreateTagDialog() {
        isShowCreateTagDialog.value = true
    }

    fun closeCreateTagDialog() {
        isShowCreateTagDialog.value = false
    }

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
}