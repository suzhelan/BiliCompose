package top.sacz.bili.biz.biliplayer.ui.item

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import top.sacz.bili.biz.biliplayer.entity.RecommendedVideoByVideo
import top.sacz.bili.shared.common.util.TimeUtils
import top.sacz.bili.shared.common.util.formatPlayCount

@Composable
fun SimpleVideoCard(
    item: RecommendedVideoByVideo.Item,
    onClick: () -> Unit
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .height(80.dp)
            .clickable {
                onClick()
            }
    ) {
        val (coverImage, durationText, titleText, upNameText, extraText, moreIc) = createRefs()
        //封面
        AsyncImage(
            model = item.cover,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(130.dp)
                .fillMaxHeight()
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(coverImage) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        // 视频时长
        Text(
            text = TimeUtils.formatMinutesToTime(item.duration),
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier
                .clip(RoundedCornerShape(4.dp))
                .background(Color.Black.copy(alpha = 0.3f))
                .padding(horizontal = 2.dp, vertical = 0.dp)
                .constrainAs(durationText) {
                    end.linkTo(coverImage.end, margin = 4.dp)
                    bottom.linkTo(coverImage.bottom, margin = 4.dp)
                }
        )

        // 标题
        Text(
            text = item.title,
            fontSize = 14.sp,
            minLines = 2,
            maxLines = 2,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.titleSmall,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(titleText) {
                    start.linkTo(coverImage.end, margin = 10.dp)
                    top.linkTo(coverImage.top, 2.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        // UP主名称
        Text(
            text = item.owner.name,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(upNameText) {
                    start.linkTo(titleText.start)
                    bottom.linkTo(extraText.top)
                }
        )

        Text(
            text = "${item.stat.view.formatPlayCount()}观看 • ${
                TimeUtils.formatTimeAgo(
                    item.pubdate.toLong()
                )
            }",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(extraText) {
                    start.linkTo(titleText.start)
                    bottom.linkTo(coverImage.bottom)
                }
        )

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = "more",
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .constrainAs(moreIc) {
                    end.linkTo(parent.end, 5.dp)
                    bottom.linkTo(coverImage.bottom)
                }
        )
    }
}