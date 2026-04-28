package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 左侧底部信息区：博主信息、关注按钮、标题和简介预览。
 */
@Composable
internal fun VerticalInfoPanel(
    video: VerticalVideoWrap,
    onMoreDescriptionClick: () -> Unit,
    onFollowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        AuthorInfoRow(video = video, onFollowClick = onFollowClick)
        Spacer(modifier = Modifier.height(10.dp))
        VideoDescriptionPreview(
            title = video.detailsInfo.title,
            desc = video.detailsInfo.desc,
            onMoreClick = onMoreDescriptionClick
        )
    }
}

@Composable
private fun AuthorInfoRow(
    video: VerticalVideoWrap,
    onFollowClick: () -> Unit,
) {
    val navigation = LocalNavigation.currentOrThrow
    val owner = video.detailsInfo.owner
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                if (owner.mid != 0L) {
                    navigation.push(SharedScreen.UserProfile(owner.mid))
                }
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = owner.face,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(42.dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.18f))
        )
        Column(
            modifier = Modifier
                .padding(horizontal = 10.dp)
        ) {
            Text(
                text = owner.name,
                color = Color.White,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = video.authorFollowerCount?.let { "${it.toStringCount()}粉丝" } ?: "",
                color = Color.White.copy(alpha = 0.78f),
                fontSize = 12.sp,
                maxLines = 1
            )
        }
        FollowButton(
            isFollowing = video.isFollowingAuthor,
            isLoading = video.isAuthorActionLoading,
            onClick = onFollowClick
        )
    }
}

@Composable
private fun FollowButton(
    isFollowing: Boolean,
    isLoading: Boolean,
    onClick: () -> Unit,
) {
    if (isLoading) {
        CircularProgressIndicator(
            color = Color.White,
            strokeWidth = 2.dp,
            modifier = Modifier.size(28.dp)
        )
        return
    }
    if (isFollowing) {
        FilledTonalButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 6.dp),
            modifier = Modifier.size(width = 78.dp, height = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Check,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(text = "已关注", fontSize = 12.sp)
        }
    } else {
        FilledTonalButton(
            onClick = onClick,
            contentPadding = PaddingValues(horizontal = 6.dp),
            modifier = Modifier.size(width = 70.dp, height = 32.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.Add,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
            )
            Text(text = "关注", fontSize = 12.sp)
        }
    }
}

@Composable
private fun VideoDescriptionPreview(
    title: String,
    desc: String,
    onMoreClick: () -> Unit,
) {
    if (title.isBlank() && desc.isBlank()) {
        return
    }
    Column(modifier = Modifier.fillMaxWidth().clickable{
        onMoreClick()
    }) {
        Text(
            text = title,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
        if (desc.isNotBlank()) {
            val isLongDesc = desc.length > DESCRIPTION_PREVIEW_LENGTH
            val preview = if (isLongDesc) {
                "${desc.take(DESCRIPTION_PREVIEW_LENGTH)}...更多"
            } else {
                desc
            }
            Text(
                text = preview,
                color = Color.White.copy(alpha = 0.86f),
                fontSize = 13.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private const val DESCRIPTION_PREVIEW_LENGTH = 32
