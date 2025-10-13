package top.suzhelan.bili.biz.shorts.ui.component

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem

/**
 * 短视频侧边操作栏组件
 * 包含头像、点赞、评论、分享等交互
 */
@OptIn(ExperimentalAnimationApi::class)
@Composable
fun ShortVideoSideActions(
    video: ShortVideoItem,
    modifier: Modifier = Modifier,
    onClickAuthor: (Long) -> Unit = {},
    onClickLike: () -> Unit = {},
    onClickComment: () -> Unit = {},
    onClickShare: () -> Unit = {}
) {
    var isLiked by remember { mutableStateOf(false) }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // 作者头像
        Box(
            modifier = Modifier
                .size(50.dp)
                .clickable { onClickAuthor(video.authorId) }
        ) {
            AsyncImage(
                model = video.cover,
                contentDescription = video.author,
                modifier = Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentScale = ContentScale.Crop
            )

            // 关注按钮
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "关注",
                modifier = Modifier
                    .size(18.dp)
                    .align(Alignment.BottomCenter)
                    .offset(y = 8.dp)
                    .background(MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(2.dp),
                tint = Color.White
            )
        }

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
 * 显示标题、作者等信息
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

/**
 * 旋转音乐图标
 */
@Composable
fun RotatingMusicIcon(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition()
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(8000, easing = LinearEasing)
        )
    )

    Box(
        modifier = modifier
            .size(50.dp)
            .rotate(rotation)
            .background(Color.White.copy(alpha = 0.2f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "音乐",
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}

