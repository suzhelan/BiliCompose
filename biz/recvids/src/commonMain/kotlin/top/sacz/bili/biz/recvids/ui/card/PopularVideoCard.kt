package top.sacz.bili.biz.recvids.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
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
import top.sacz.bili.biz.recvids.model.PopularItem
import top.sacz.bili.shared.common.ui.shimmerEffect
import top.sacz.bili.shared.common.util.TimeUtils
import top.sacz.bili.shared.common.util.TimeUtils.formatPlayCount

@Composable
fun PopularLoadingCard() {
    Box(modifier = Modifier.fillMaxWidth().height(110.dp).shimmerEffect()) {}
}

@Composable
fun PopularCoverCard(popularItem: PopularItem) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .padding(vertical = 6.dp, horizontal = 12.dp)
    ) {
        val (coverImage, durationText, titleText, reasonText, upNameText, extraText) = createRefs()

        AsyncImage(
            model = popularItem.pic,
            contentDescription = popularItem.title,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.width(170.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(8.dp))
                .constrainAs(coverImage) {
                    start.linkTo(parent.start)
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                }
        )
        // 视频时长
        Text(
            text = TimeUtils.formatMinutesToTime(popularItem.duration),
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
            text = popularItem.title,
            fontSize = 14.sp,
            minLines = 2,
            maxLines = 2,
            lineHeight = 16.sp,
            style = MaterialTheme.typography.titleSmall,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .constrainAs(titleText) {
                    start.linkTo(coverImage.end, margin = 12.dp)
                    top.linkTo(parent.top, 5.dp)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                }
        )

        // 推荐理由
        if (popularItem.rcmdReason.content.isNotEmpty()) {
            Text(
                text = popularItem.rcmdReason.content,
                color = Color.White,
                fontSize = 12.sp,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier
                    .background(
                        color = Color(0xFFFF6699),
                        shape = RoundedCornerShape(4.dp)
                    )
                    .padding(horizontal = 2.dp, vertical = 1.dp)
                    .constrainAs(reasonText) {
                        start.linkTo(titleText.start)
                        top.linkTo(titleText.bottom, margin = 4.dp)
                    }
            )
        }
        // UP主名称
        Text(
            text = popularItem.owner.name,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(upNameText) {
                    start.linkTo(titleText.start)
                    bottom.linkTo(extraText.top, (-5).dp)
                }
        )

        Text(
            text = "${popularItem.stat.view.formatPlayCount()}观看 • ${
                TimeUtils.formatTimeAgo(
                    popularItem.ctime.toLong()
                )
            }",
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 12.sp,
            color = Color.Gray,
            modifier = Modifier
                .constrainAs(extraText) {
                    start.linkTo(titleText.start)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}