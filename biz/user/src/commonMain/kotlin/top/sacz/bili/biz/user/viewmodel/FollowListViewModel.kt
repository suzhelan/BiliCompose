package top.sacz.bili.biz.user.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.user.api.RelationApi
import top.sacz.bili.biz.user.data.FollowListDataSource
import top.sacz.bili.biz.user.entity.RelationTags
import top.sacz.bili.biz.user.util.ModifyUtils
import top.sacz.bili.shared.common.base.BaseViewModel

class FollowListViewModel : BaseViewModel() {

    private val api = RelationApi()

    private val _tags = mutableStateListOf<RelationTags>()
    val tags: List<RelationTags> = _tags

    fun queryTags() = launchTask {
        val resultTags = api.queryTags().data
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
            showMessage(message = ModifyUtils.getToastByModifyResult(result))
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
            showMessage(message = ModifyUtils.getToastByModifyResult(result))
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

}