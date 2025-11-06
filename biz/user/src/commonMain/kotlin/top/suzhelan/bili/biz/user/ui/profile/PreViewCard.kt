package top.suzhelan.bili.biz.user.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
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
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils

@Composable
fun VideoPreViewCard(
    item: UserSpace.Archive.Item
) = ElevatedCard(
    modifier = Modifier.wrapContentSize()
        .padding(5.dp),
    elevation = CardDefaults.cardElevation(
        defaultElevation = 5.dp
    ),
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
    ) {
        //视频封面
        val (cover, quota, title, time, mask) = createRefs()
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
                    bottom.linkTo(cover.bottom)
                    start.linkTo(cover.start)
                    end.linkTo(cover.end)
                }
        )
        //播放数 弹幕数
        Text(
            text = "播放:${item.play} 弹幕:${item.danmaku}",
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(quota) {
                    bottom.linkTo(cover.bottom)
                    start.linkTo(cover.start)
                },
            color = Color.White,
            fontSize = 10.sp
        )
        //视频时长
        Text(
            text = TimeUtils.formatSecondToTime(item.duration),
            fontSize = 10.sp,
            color = Color.White,
            modifier = Modifier
                .padding(2.dp)
                .constrainAs(time) {
                    bottom.linkTo(cover.bottom)
                    end.linkTo(cover.end)
                }
        )
        //标题栏 最多只展示一行
        Text(
            text = item.title,
            maxLines = 1,
            fontSize = 12.sp,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .fillMaxWidth()
                .padding(5.dp)
                .constrainAs(title) {
                    top.linkTo(cover.bottom)
                    start.linkTo(cover.start)
                }
        )
    }
}

@Composable
fun <T> ProfileVideoPreView(
    title: String = "视频",
    items: List<T>,
    itemContent: @Composable (item: T) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
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
            Text(text = "查看更多 >", color = TipColor)
        }
        Spacer(modifier = Modifier.height(5.dp))
        //网格布局
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 130.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(items.size) { index ->
                itemContent(items[index])
            }
        }

    }
}