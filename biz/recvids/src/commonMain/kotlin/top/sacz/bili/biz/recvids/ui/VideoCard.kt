package top.sacz.bili.biz.home.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.rounded.Error
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import top.sacz.bili.biz.recvids.model.Video

@Composable
fun EmptyCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {

    }
}

@Composable
fun VideoCard(video: Video) {
    //首先就是一个圆角卡片背景
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable {
                //点击事件
                //跳转到视频详情页
            }
    ) {
        //上半部分 封面
        Box(modifier = Modifier.fillMaxWidth().height(150.dp)) {
            //封面图像
            SubcomposeAsyncImage(
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
                model = video.cover,
                error = { Icon(Icons.Rounded.Error, contentDescription = null) },
                contentDescription = video.title
            )
            //用来做底部渐黑的效果
            Box(
                modifier = Modifier.fillMaxWidth().height(40.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.5f)
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth().height(20.dp)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //图标变白色
                Icon(
                    imageVector = Icons.Outlined.SmartDisplay,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White,
                )
                Text(
                    text = video.coverLeftText1,
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White)
                )

                //间距
                Spacer(Modifier.width(10.dp))

                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White,
                )
                Text(
                    text = video.coverLeftText2,
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White)
                )
                //视频时长位于最右侧
                Text(
                    text = video.coverRightText,
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
                )
            }
        }
    }
}