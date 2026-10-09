package top.suzhelan.bili.biz.user.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.ConstraintLayoutScope
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.common.util.toStringCount

/**
 * 基础预览卡片布局
 */
@Composable
private fun PreViewConstraintCard(
    modifier: Modifier = Modifier,
    content: @Composable ConstraintLayoutScope.() -> Unit
) = ElevatedCard(
    modifier = modifier.fillMaxWidth()
        .padding(5.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 5.dp
    ),
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    ) {
        content()
    }
}

/**
 * 收藏夹卡片
 */
@Composable
fun FavouritePreviewCard(
    item: UserSpace.Favourite2.Item,
    onClick: () -> Unit = {},
) = PreViewConstraintCard(modifier = Modifier.clickable(onClick = onClick)) {
    //封面
    val (cover, title) = createRefs()
    AsyncImage(
        model = item.cover,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .constrainAs(cover) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )
    //标题
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(5.dp)
            .constrainAs(title) {
                top.linkTo(cover.bottom)
                start.linkTo(cover.start)
                end.linkTo(cover.end)
            }
    ) {
        //标题
        Text(
            text = item.title,
            maxLines = 1,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
        )
        //提示信息
        Text(
            text = "${item.count}个收藏 - ${if (item.isPrivate) "私密" else "公开"}",
            fontSize = 10.sp,
            color = TipColor,
        )
    }
}

/**
 * 视频卡片
 * @param cover 封面
 * @param title 标题
 * @param playCount 播放数
 * @param duration 时长
 * @param danmaku 弹幕数
 *
 */
@Composable
fun VideoPreViewCard(
    cover: String,
    title: String,
    playCount: Int,
    duration: Int,
    danmaku: Int,
    onClick: () -> Unit
) = PreViewConstraintCard(
    modifier = Modifier
        .clickable {
            onClick()
        }
) {
    //视频封面
    val (coverRef, quota, titleRef, durationRef, mask) = createRefs()
    AsyncImage(
        model = cover,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .constrainAs(coverRef) {
                top.linkTo(parent.top)
                start.linkTo(parent.start)
                end.linkTo(parent.end)
            }
    )
    //用来做底部渐黑的效果
    Box(
        modifier = Modifier.fillMaxWidth()
            .height(30.dp)
            .background(
                Brush.verticalGradient(
                    0f to Color.Transparent,
                    1f to Color.Black.copy(alpha = 0.4f)
                )
            ).constrainAs(mask) {
                bottom.linkTo(coverRef.bottom)
                start.linkTo(coverRef.start)
                end.linkTo(coverRef.end)
            }
    )
    //播放数 弹幕数
    Text(
        text = "播放:${playCount.toStringCount()} 弹幕:${danmaku.toStringCount()}",
        modifier = Modifier
            .padding(2.dp)
            .constrainAs(quota) {
                bottom.linkTo(coverRef.bottom)
                start.linkTo(coverRef.start)
            },
        color = Color.White,
        fontSize = 10.sp
    )
    //视频时长
    Text(
        text = TimeUtils.formatSecondToTime(duration),
        fontSize = 10.sp,
        color = Color.White,
        modifier = Modifier
            .padding(2.dp)
            .constrainAs(durationRef) {
                bottom.linkTo(coverRef.bottom)
                end.linkTo(coverRef.end)
            }
    )
    //标题栏 最多只展示一行
    Text(
        text = title,
        maxLines = 1,
        fontSize = 12.sp,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .constrainAs(titleRef) {
                top.linkTo(coverRef.bottom)
                start.linkTo(coverRef.start)
            }
    )
}


@Composable
fun <T> ProfileVideoPreView(
    title: String = "视频",
    items: List<T>,
    moreClick : () -> Unit = {},
    itemContent: @Composable (item: T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
            .padding(vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(
                horizontal = 10.dp
            ),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = title)
            //填充剩余
            Spacer(
                modifier = Modifier.weight(1f)
            )
            Text(text = "查看更多 >", color = TipColor, modifier = Modifier.clickable {
                moreClick()
            })
        }
        Spacer(modifier = Modifier.height(5.dp))
        /*
         * This preview is hosted by the profile page's vertical scroll
         * container. A regular Column/Row grid has an intrinsic height, so the
         * page remains the only vertical scroll owner.
         */
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            val columnCount = if (maxWidth >= 520.dp) 3 else 2
            val previewItems = items.take(columnCount * 2)

            Column(verticalArrangement = Arrangement.spacedBy(5.dp)) {
                previewItems.chunked(columnCount).forEach { rowItems ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        rowItems.forEach { item ->
                            Box(modifier = Modifier.weight(1f)) {
                                itemContent(item)
                            }
                        }
                        // Preserve the grid alignment for an incomplete last row.
                        repeat(columnCount - rowItems.size) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }
}
