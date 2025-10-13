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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Comment
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem

/**
 * 短视频侧边操作栏组件
 *
 * 提供作者头像、点赞、评论、分享等交互按钮
 * 采用竖向排列，位于屏幕右侧
 *
 * @param video 视频数据
 * @param modifier Modifier
 * @param followState 关注状态 (0:未关注 2:已关注 6:已互粉)
 * @param onClickAuthor 点击作者回调
 * @param onClickFollow 点击关注按钮回调
 * @param onClickLike 点击点赞回调
 * @param onClickComment 点击评论回调
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
    onClickAuthor: (Long) -> Unit = {},
    onClickFollow: (Long, Int) -> Unit = { _, _ -> },
    onClickLike: () -> Unit = {},
    onClickComment: () -> Unit = {},
    onClickShare: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }

    // 判断是否已关注 (2:已关注 6:已互粉)
    val isFollowed = followState in listOf(2, 6)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 作者头像
        AuthorAvatar(
            avatarUrl = video.authorAvatar,
            authorName = video.author,
            authorId = video.authorId,
            isFollowed = isFollowed,
            onClick = { onClickAuthor(video.authorId) },
            onClickFollow = { onClickFollow(video.authorId, followState) }
        )

        Spacer(modifier = Modifier.height(8.dp))

        // 点赞按钮
        AnimatedLikeButton(
            isLiked = isLiked,
            count = video.playCount,
            onLikedClicked = {
                isLiked = !isLiked
                onClickLike()
            }
        )

        // 评论按钮
        ActionButton(
            icon = Icons.Default.Comment,
            count = video.danmakuCount,
            onClick = onClickComment
        )

        // 分享按钮
        ActionButton(
            icon = Icons.Default.Share,
            count = "",
            onClick = onClickShare
        )
    }
}

/**
 * 作者头像组件
 *
 * @param avatarUrl 头像URL
 * @param authorName 作者名称
 * @param authorId 作者ID
 * @param isFollowed 是否已关注
 * @param onClick 点击头像回调
 * @param onClickFollow 点击关注按钮回调
 */
@Composable
private fun AuthorAvatar(
    avatarUrl: String,
    authorName: String,
    authorId: Long,
    isFollowed: Boolean,
    onClick: () -> Unit,
    onClickFollow: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .clickable { onClick() }
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = authorName,
            modifier = Modifier
                .fillMaxSize()
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentScale = ContentScale.Crop
        )

        // 关注按钮 - 已关注时不显示
        if (!isFollowed) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "关注",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(2.dp)
                    .clickable { onClickFollow() },
                tint = Color.White
            )
        }
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
            Icon(
                imageVector = if (isLiked) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                contentDescription = "点赞",
                modifier = Modifier.size(iconSize),
                tint = if (isLiked) Color.Red else Color.White
            )
        }

        Text(
            text = count,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White
        )
    }
}

/**
 * 通用操作按钮
 *
 * @param icon 图标
 * @param count 数量文本
 * @param onClick 点击回调
 */
@Composable
private fun ActionButton(
    icon: ImageVector,
    count: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier
                .size(30.dp)
                .clickable { onClick() },
            tint = Color.White
        )

        if (count.isNotEmpty()) {
            Text(
                text = count,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White
            )
        }
    }
}

/**
 * 短视频底部信息组件
 *
 * 显示视频标题和作者名称
 * 位于屏幕底部左侧
 *
 * @param video 视频数据
 * @param modifier Modifier
 * @param onClickAuthor 点击作者回调
 *
 * @author suzhelan
 */
@Composable
fun ShortVideoBottomInfo(
    video: ShortVideoItem,
    modifier: Modifier = Modifier,
    onClickAuthor: (Long) -> Unit = {}
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        // 作者名称
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.clickable { onClickAuthor(video.authorId) }
        ) {
            Text(
                text = "@${video.author}",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        // 视频标题
        Text(
            text = video.title,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(0.8f)
        )
    }
}
