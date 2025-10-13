package top.suzhelan.bili.biz.shorts.api

import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.recvids.api.FeedApi
import top.suzhelan.bili.biz.recvids.entity.ShortVideoData
import top.suzhelan.bili.biz.user.api.RelationApi
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.Relation
import top.suzhelan.bili.biz.user.entity.UserCard

/**
 * 短视频API接口
 */
class ShortVideoApi {

    private val feedApi = FeedApi()
    private val userApi = UserApi()
    private val relationApi = RelationApi()

    /**
     * 获取推荐视频流
     *
     * @return 视频推荐流响应
     */
    suspend fun getFeedVideos(): BiliResponse.Success<ShortVideoData> {
        return feedApi.getFeed()
    }

    /**
     * 获取用户信息（包含头像）
     *
     * @param uid 用户ID
     * @return 用户信息响应
     */
    suspend fun getUserInfo(uid: Long): BiliResponse.Success<UserCard> {
        return userApi.getUserInfo(uid, isWithPhoto = true)
    }

    /**
     * 关注/取消关注用户
     *
     * @param mid 用户ID
     * @param isFollow true为关注，false为取消关注
     * @return 操作结果
     */
    suspend fun modifyRelation(mid: Long, isFollow: Boolean): BiliResponse.SuccessOrNull<Nothing> {
        val action = if (isFollow) 1 else 2
        return relationApi.modify(mid, action)
    }

    /**
     * 查询用户关注状态
     *
     * @param mid 用户ID
     * @return 关注关系
     */
    suspend fun queryRelation(mid: Long): BiliResponse.Success<Relation> {
        return relationApi.queryRelation(mid)
    }
}
