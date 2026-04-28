package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Comment
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.shared.common.util.toStringCount

/**
 * 右侧底部操作栏：点赞、评论、投币、收藏、分享。
 */
@Composable
internal fun VerticalActionBar(
    video: VerticalVideoWrap,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onCoinClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        VerticalActionItem(
            icon = Icons.Outlined.ThumbUp,
            countText = video.detailsInfo.stat.like.countTextOrBlank(),
            isSelected = video.isLike,
            onClick = onLikeClick
        )
        VerticalActionItem(
            icon = Icons.AutoMirrored.Outlined.Comment,
            countText = video.detailsInfo.stat.reply.countTextOrBlank(),
            onClick = onCommentClick
        )
        VerticalActionItem(
            icon = Icons.Outlined.Toll,
            countText = (video.detailsInfo.stat.coin + video.coinQuotationCount).countTextOrBlank(),
            isSelected = video.coinQuotationCount > 0,
            onClick = onCoinClick
        )
        VerticalActionItem(
            icon = Icons.Outlined.StarOutline,
            countText = video.detailsInfo.stat.favorite.countTextOrBlank(),
            isSelected = video.isFavorite,
            onClick = {}
        )
        VerticalActionItem(
            icon = Icons.Outlined.Share,
            countText = video.detailsInfo.stat.share.countTextOrBlank(),
            onClick = {}
        )
    }
}

@Composable
private fun VerticalActionItem(
    icon: ImageVector,
    countText: String,
    isSelected: Boolean = false,
    onClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .width(52.dp)
            .clickable(onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(alpha = 0.22f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color(0xFFFF6699) else Color.White,
                modifier = Modifier.size(28.dp)
            )
        }
        Text(
            // 未拿到数量前保留空字符串位置，避免图标组在数据回来时跳动。
            text = countText,
            color = Color.White,
            fontSize = 12.sp,
            maxLines = 1,
            modifier = Modifier.height(18.dp)
        )
    }
}

private fun Int.countTextOrBlank(): String = if (this > 0) toStringCount() else ""
