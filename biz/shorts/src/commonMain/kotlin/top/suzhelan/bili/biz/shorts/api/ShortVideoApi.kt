package top.suzhelan.bili.biz.shorts.api

import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.recvids.api.FeedApi
import top.suzhelan.bili.biz.recvids.entity.ShortVideoData
import top.suzhelan.bili.biz.user.api.UserApi
import top.suzhelan.bili.biz.user.entity.UserCard

/**
 * 短视频API接口
 */
class ShortVideoApi {

    private val feedApi = FeedApi()
    private val userApi = UserApi()

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
}
