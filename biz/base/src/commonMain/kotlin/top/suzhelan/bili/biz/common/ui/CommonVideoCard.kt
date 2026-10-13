package top.suzhelan.bili.biz.common.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils


/**
 * 常用视频卡片布局 通常是垂直单列列表使用 该文件仅作示例模板UI 展示其他UI可供参考的风格
 *
 * @param cover 封面图片的URL
 * @param title 视频标题
 * @param createTime 创建时间
 * @param playTime 上次播放时长
 * @param duration 视频时长
 * @param danmakuCount 弹幕数量
 * @param viewContent 观看数量格式化后的字符串,例如'45.3万'
 */
@Composable
fun CommonVideoCard(
    cover: String,
    title: String,
    createTime: Long,
    playTime: Long,
    duration: Long,
    danmakuCount: Long,
    viewContent: String,
    onClick: () -> Unit = {},
) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
            .clickable {
                onClick()
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            // 左部分 封面
            Box(modifier = Modifier.width(140.dp).fillMaxHeight()) {
                // 封面图像
                AsyncImage(
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize(),
                    model = "${cover}@10q.webp",
                    contentDescription = title,
                )
                // 用来做底部渐黑的效果
                Box(
                    modifier = Modifier.fillMaxWidth().height(30.dp)
                        .align(Alignment.BottomStart)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.4f)
                            )
                        )
                )
                // 视频时长位于最右侧,底部
                Text(
                    text = TimeUtils.formatSecondToTime(duration.toInt()),
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier.align(Alignment.BottomEnd)
                )
            }
            //右部分
            Column(
                modifier = Modifier.fillMaxWidth()
                    .fillMaxHeight()
                    .padding(4.dp)
            ) {
                //标题
                Text(
                    text = title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.fillMaxWidth()
                )
                //空隙填充
                Spacer(modifier = Modifier.weight(1f))
                //视频发布时间
                Text(
                    text = TimeUtils.formatTimeAgo(createTime),
                    color = TipColor,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    //水平对齐
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // 播放数
                    Icon(
                        imageVector = Icons.Default.PlayArrow,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = TipColor
                    )
                    Text(
                        text = viewContent,
                        fontSize = 11.sp,
                        color = TipColor
                    )

                    // 间距
                    Spacer(Modifier.width(5.dp))

                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
                        contentDescription = null,
                        modifier = Modifier.size(15.dp),
                        tint = TipColor
                    )
                    // 弹幕数
                    Text(
                        text = "$danmakuCount",
                        fontSize = 11.sp,
                        color = TipColor
                    )
                }
            }
        }

    }
}