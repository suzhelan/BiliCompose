package top.suzhelan.bili.comment.viewmodel

import top.suzhelan.bili.comment.api.CommentApi
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.shared.common.base.BaseViewModel

class CommentViewModel : BaseViewModel() {
    private val api = CommentApi()
    fun getCommentList(oid: String, type: CommentSourceType) {
        launchTask {
            val response = api.getCommentList(
                oid = oid,
                type = type,
                page = 1,
                pageSize = 10
            )
            println(response)
        }
    }
}