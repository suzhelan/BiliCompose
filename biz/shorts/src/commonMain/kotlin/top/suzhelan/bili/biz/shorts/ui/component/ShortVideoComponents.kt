package top.suzhelan.bili.biz.shorts.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.keyframes
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.biz.shorts.ui.icons.ShortVideoIcons
import top.suzhelan.bili.shared.common.util.toStringCount


private val IconColor = Color(0xfffffcfc) // 白灰色

/**
 * 短视频侧边操作栏组件
 *
 * 提供点赞、评论、投币、收藏、分享等交互按钮
 * 采用竖向排列，位于屏幕右侧
 *
 * @param video 视频数据
 * @param modifier Modifier
 * @param followState 关注状态 (0:未关注 2:已关注 6:已互粉)
 * @param likeState 点赞状态 (true:已点赞 false:未点赞)
 * @param onClickAuthor 点击作者回调
 * @param onClickFollow 点击关注按钮回调
 * @param onClickLike 点击点赞回调
 * @param onClickComment 点击评论回调
 * @param onClickCoin 点击投币回调
 * @param onClickCollection 点击收藏回调
 * @param onClickShare 点击分享回调
 *
 * @author suzhelan
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShortVideoSideActions(
    video: ShortVideoItem,
    modifier: Modifier = Modifier,
    followState: Int = 0,
    likeState: Boolean = false,
    onClickAuthor: (Long) -> Unit = {},
    onClickFollow: (Long, Int) -> Unit = { _, _ -> },
    onClickLike: (Long, Boolean) -> Unit = { _, _ -> },
    onClickComment: () -> Unit = {},
    onClickCoin: () -> Unit = {},
    onClickCollection: () -> Unit = {},
    onClickShare: () -> Unit = {}
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 点赞按钮
        AnimatedLikeButton(
            isLiked = likeState,
            count = video.likeCount,
            onLikedClicked = {
                onClickLike(video.aid, likeState)
            }
        )

        // 评论按钮
        SvgActionButton(
            icon = ShortVideoIcons.comment,
            count = video.commentCount,
            contentDescription = "评论",
            onClick = onClickComment
        )

        // 投币按钮
        SvgActionButton(
            icon = ShortVideoIcons.coin,
            count = video.coinCount,
            contentDescription = "投币",
            onClick = onClickCoin
        )

        // 收藏按钮
        SvgActionButton(
            icon = ShortVideoIcons.collection,
            count = video.favoriteCount,
            contentDescription = "收藏",
            onClick = onClickCollection
        )

        // 转发按钮
        SvgActionButton(
            icon = ShortVideoIcons.forward,
            count = video.shareCount,
            contentDescription = "转发",
            onClick = onClickShare
        )
    }
}

/**
 * 动画点赞按钮
 *
 * 点击时带有放大缩小的动画效果
 *
 * @param isLiked 是否已点赞
 * @param count 点赞数量文本
 * @param onLikedClicked 点击回调
 */
@Composable
private fun AnimatedLikeButton(
    isLiked: Boolean,
    count: String,
    onLikedClicked: () -> Unit
) {
    val iconSize by animateDpAsState(
        targetValue = if (isLiked) 32.dp else 30.dp,
        animationSpec = keyframes {
            durationMillis = 400
            24.dp.at(50)
            38.dp.at(190)
            30.dp.at(400)
        }
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) { onLikedClicked() },
            contentAlignment = Alignment.Center
        ) {
            // 黑色阴影
            Icon(
                painter = painterResource(ShortVideoIcons.favorite),
                contentDescription = null,
                modifier = Modifier
                    .size(iconSize)
                    .offset(x = 1.dp, y = 1.dp),
                tint = Color.Black.copy(alpha = 0.1f)
            )
            // 主图标
            Icon(
                painter = painterResource(ShortVideoIcons.favorite),
                contentDescription = "点赞",
                modifier = Modifier.size(iconSize),
                tint = if (isLiked) MaterialTheme.colorScheme.primary else IconColor
            )
        }

        if (count.isNotEmpty()) {
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White
            )
        }
    }
}

/**
 * SVG图标操作按钮
 *
 * @param icon SVG图标资源
 * @param count 数量文本
 * @param contentDescription 内容描述
 * @param onClick 点击回调
 */
@Composable
private fun SvgActionButton(
    icon: DrawableResource,
    count: String,
    contentDescription: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Box(
            modifier = Modifier.size(30.dp),
            contentAlignment = Alignment.Center
        ) {
            // 黑色阴影
            Icon(
                painter = painterResource(icon),
                contentDescription = null,
                modifier = Modifier
                    .size(30.dp)
                    .offset(x = 1.dp, y = 1.dp)
                    .clickable { onClick() },
                tint = Color.Black.copy(alpha = 0.1f)
            )
            // 主图标
            Icon(
                painter = painterResource(icon),
                contentDescription = contentDescription,
                modifier = Modifier
                    .size(30.dp)
                    .clickable { onClick() },
                tint = IconColor
            )
        }

        if (count.isNotEmpty()) {
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall.copy(
                    shadow = Shadow(
                        color = Color.Black.copy(alpha = 0.8f),
                        offset = Offset(1f, 1f),
                        blurRadius = 4f
                    )
                ),
                color = Color.White
            )
        }
    }
}

/**
 * 短视频底部信息组件
 *
 * 显示视频标题、作者头像、作者名称、关注按钮和粉丝数
 * 位于屏幕底部左侧
 *
 * @param video 视频数据
 * @param modifier Modifier
 * @param followState 关注状态 (0:未关注 2:已关注 6:已互粉)
 * @param onClickAuthor 点击作者回调
 * @param onClickFollow 点击关注按钮回调
 */
@Composable
fun ShortVideoBottomInfo(
    video: ShortVideoItem,
    modifier: Modifier = Modifier,
    followState: Int = 0,
    onClickAuthor: (Long) -> Unit = {},
    onClickFollow: (Long, Int) -> Unit = { _, _ -> }
) {
    // 判断是否已关注 (2:已关注 6:已互粉)
    val isFollowed = followState in listOf(2, 6)

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // 作者信息：头像在左边，右边是关注按钮和粉丝数的列
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 作者头像
            AsyncImage(
                model = video.authorAvatar,
                contentDescription = video.author,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .clickable { onClickAuthor(video.authorId) },
                contentScale = ContentScale.Crop
            )

            // 右侧区域：关注按钮和粉丝数在上，作者名在下
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // 关注按钮和粉丝数在同一行
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 关注按钮
                    FollowButton(
                        isFollowed = isFollowed,
                        onClick = { onClickFollow(video.authorId, followState) }
                    )

                    // 粉丝数
                    if (video.followerCount > 0) {
                        Text(
                            text = "${video.followerCount.toStringCount()}粉丝",
                            style = MaterialTheme.typography.labelSmall.copy(
                                shadow = Shadow(
                                    color = Color.Black.copy(alpha = 0.8f),
                                    offset = Offset(1f, 1f),
                                    blurRadius = 4f
                                )
                            ),
                            color = Color.White.copy(alpha = 0.9f),
                            fontSize = 11.sp
                        )
                    }
                }

                // 作者名称
                Text(
                    text = "@${video.author}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        shadow = Shadow(
                            color = Color.Black.copy(alpha = 0.8f),
                            offset = Offset(1f, 1f),
                            blurRadius = 4f
                        )
                    ),
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onClickAuthor(video.authorId) }
                )
            }
        }

        // 视频标题
        Text(
            text = video.title,
            style = MaterialTheme.typography.bodySmall.copy(
                shadow = Shadow(
                    color = Color.Black.copy(alpha = 0.8f),
                    offset = Offset(1f, 1f),
                    blurRadius = 4f
                )
            ),
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.85f)
        )
    }
}

/**
 * 关注按钮
 *
 * 未关注时高亮显示"关注"，已关注时灰色显示"已关注"
 *
 * @param isFollowed 是否已关注
 * @param onClick 点击回调
 */
@Composable
private fun FollowButton(
    isFollowed: Boolean,
    onClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = if (isFollowed) {
            Color.White.copy(alpha = 0.2f)
        } else {
            MaterialTheme.colorScheme.primary
        },
        modifier = Modifier.height(28.dp)
    ) {
        Box(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = if (isFollowed) "已关注" else "关注",
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = if (isFollowed) {
                    Color.White.copy(alpha = 0.7f)
                } else {
                    Color.White
                },
                fontSize = 12.sp
            )
        }
    }
}
