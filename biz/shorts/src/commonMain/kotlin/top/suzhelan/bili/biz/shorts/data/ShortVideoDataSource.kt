package top.suzhelan.bili.biz.shorts.data

import kotlinx.serialization.json.jsonPrimitive
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.biz.recvids.config.BaseCoverSerializer
import top.suzhelan.bili.biz.recvids.entity.SmallCoverV2Item
import top.suzhelan.bili.biz.recvids.entity.targetCardType
import top.suzhelan.bili.biz.shorts.api.ShortVideoApi
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 短视频数据源
 */
class ShortVideoDataSource {

    private val api = ShortVideoApi()

    /**
     * 加载视频数据结果
     */
    sealed class LoadResult {
        /**
         * 加载成功
         * @param videos 视频列表
         */
        data class Success(val videos: List<ShortVideoItem>) : LoadResult()

        /**
         * 加载失败
         * @param message 错误信息
         * @param exception 异常对象（可选）
         */
        data class Error(val message: String, val exception: Throwable? = null) : LoadResult()
    }

    /**
     * 从推荐流加载视频
     *
     * 从B站推荐API获取视频数据，过滤并转换为短视频模型
     *
     * @return 加载结果
     */
    suspend fun loadVideos(): LoadResult {
        return try {
            val response = api.getFeedVideos()

            // BiliResponse.Success 总是成功的
            val data = response.data

            // 解析视频列表
            val items = data.items
                .filter {
                    it["card_type"]?.jsonPrimitive?.content == SmallCoverV2Item.targetCardType
                }
                .mapNotNull {
                    runCatching {
                        HttpJsonDecoder.decodeFromJsonElement(BaseCoverSerializer, it)
                    }.onFailure { e ->
                        LogUtils.e("ShortVideoDataSource: 视频解析失败", e)
                    }.getOrNull()
                }
                .filterIsInstance<SmallCoverV2Item>()

            // 转换为短视频模型
            val videos = items.map { ShortVideoItem.fromSmallCoverV2Item(it) }

            LogUtils.d("ShortVideoDataSource: 成功加载 ${videos.size} 个视频")
            LoadResult.Success(videos)

        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 加载异常", e)
            LoadResult.Error("网络错误: ${e.message}", e)
        }
    }

    /**
     * 批量获取作者头像
     *
     * @param authorIds 作者ID列表
     * @return 作者ID到头像URL的映射
     */
    suspend fun fetchAuthorAvatars(authorIds: List<Long>): Map<Long, String> {
        val avatarMap = mutableMapOf<Long, String>()

        try {
            // 分批获取，避免一次性请求过多
            authorIds.chunked(5).forEach { chunk ->
                chunk.forEach { authorId ->
                    if (authorId == 0L) return@forEach

                    try {
                        val response = api.getUserInfo(authorId)
                        val avatar = response.data.card.face
                        avatarMap[authorId] = avatar
                    } catch (e: Exception) {
                        LogUtils.e("ShortVideoDataSource: 获取用户头像异常 - authorId=$authorId", e)
                    }
                }
            }

            LogUtils.d("ShortVideoDataSource: 成功获取 ${avatarMap.size} 个作者头像")

        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 批量获取作者头像失败", e)
        }

        return avatarMap
    }

    /**
     * 关注/取消关注用户
     *
     * @param mid 用户ID
     * @param isFollow true为关注，false为取消关注
     * @return 操作结果消息
     */
    suspend fun modifyFollow(mid: Long, isFollow: Boolean): Result<String> {
        return try {
            val response = api.modifyRelation(mid, isFollow)
            val message =
                top.suzhelan.bili.biz.user.util.RelationUtils.getToastByModifyResult(response)

            if (response.code == 0) {
                Result.success(message)
            } else {
                Result.failure(Exception(message))
            }
        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 关注操作失败", e)
            Result.failure(e)
        }
    }

    /**
     * 查询用户关注状态
     *
     * @param mid 用户ID
     * @return 关注状态 - 0:未关注 2:已关注 6:已互粉
     */
    suspend fun queryFollowState(mid: Long): Int? {
        return try {
            val response = api.queryRelation(mid)
            response.data.attribute
        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 查询关注状态失败", e)
            null
        }
    }

    /**
     * 获取视频统计信息
     *
     * 包含点赞数、投币数、收藏数、转发数等
     *
     * @param aid 视频aid
     * @return 视频统计信息的Map，key为统计类型，value为格式化后的数字字符串
     */
    suspend fun fetchVideoStats(aid: Long): Map<String, String>? {
        return try {
            val response = api.getVideoDetails(aid)
            val stat = response.data.stat

            mapOf(
                "like" to stat.like.toString(),
                "coin" to stat.coin.toString(),
                "favorite" to stat.favorite.toString(),
                "share" to stat.share.toString()
            )
        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 获取视频统计信息失败 - aid=$aid", e)
            null
        }
    }

    /**
     * 查询视频是否已点赞
     *
     * @param aid 视频aid
     * @return 是否已点赞，null表示查询失败
     */
    suspend fun queryLikeState(aid: Long): Boolean? {
        return try {
            val response = api.isLike(aid)
            response.data
        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 查询点赞状态失败 - aid=$aid", e)
            null
        }
    }

    /**
     * 点赞或取消点赞视频
     *
     * @param aid 视频aid
     * @param isLike true为点赞，false为取消点赞
     * @return 操作结果
     */
    suspend fun toggleLike(aid: Long, isLike: Boolean): Result<String> {
        return try {
            val response = api.like(aid, isLike)
            if (response.code == 0) {
                Result.success(if (isLike) "点赞成功" else "取消点赞")
            } else {
                Result.failure(Exception(response.message))
            }
        } catch (e: Exception) {
            LogUtils.e("ShortVideoDataSource: 点赞操作失败 - aid=$aid", e)
            Result.failure(e)
        }
    }
}
