package top.sacz.bili.biz.recvids.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import bilicompose.biz.recvids.generated.resources.Res
import bilicompose.biz.recvids.generated.resources.more
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.biz.recvids.entity.PopularItem
import top.sacz.bili.shared.common.ui.shimmerEffect
import top.sacz.bili.shared.common.util.TimeUtils
import top.sacz.bili.shared.common.util.formatPlayCount

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
            .clickable {

            }
    ) {
        val (coverImage, durationText, titleText, reasonText, upNameText, extraText, moreIc) = createRefs()

        AsyncImage(
            model = popularItem.pic,
            contentDescription = popularItem.title,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.width(165.dp)
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
                    start.linkTo(coverImage.end, margin = 10.dp)
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
                        bottom.linkTo(upNameText.top, 3.dp)
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
                    bottom.linkTo(extraText.top)
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

        Icon(
            imageVector = Icons.Default.MoreVert,
            contentDescription = stringResource(Res.string.more),
            tint = Color.Gray,
            modifier = Modifier
                .size(20.dp)
                .constrainAs(moreIc) {
                    end.linkTo(parent.end, (-10).dp)
                    bottom.linkTo(parent.bottom)
                }
        )
    }
}