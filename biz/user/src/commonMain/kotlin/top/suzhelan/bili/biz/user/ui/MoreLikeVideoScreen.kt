package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.LikeVideo
import top.suzhelan.bili.biz.user.viewmodel.MoreLikeViewModel
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 更多点赞的视频
 */
@Composable
fun MoreLikeVideoScreen(
    mid : Long
) {
    val viewModel = viewModel {
        MoreLikeViewModel()
    }
    val lazyPagingItems = viewModel.getMoreLikeVideoFlow(mid).collectAsLazyPagingItems()
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey {
                it.uri
            }
        ) { index ->
            val likeVideo = lazyPagingItems[index] ?: return@items
            LikeVideoItem(likeVideo)
        }
        when (lazyPagingItems.loadState.append) {
            is LoadState.Loading -> {
                item {
                    LoadingIndicator()
                }
            }

            is LoadState.NotLoading -> {
                item {
                    Text(
                        text = "没有更多了",
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        textAlign = TextAlign.Center,
                        color = TipColor
                    )
                }
            }

            else -> {
            }
        }
    }
}

@Composable
private fun LikeVideoItem(video: LikeVideo) {
    val navigator = LocalNavigation.currentOrThrow

    // 首先就是一个圆角卡片背景
    ElevatedCard(
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .padding(8.dp)
            .clickable {
                // 跳转到视频播放页面
                // 从uri中提取aid参数
                val aid = extractAidFromUri(video.uri)
                if (aid != null) {
                    navigator.push(SharedScreen.VideoPlayer(aid = aid, cid = 0))
                }
            }
    ) {
        // 上半部分 封面
        Box(modifier = Modifier.fillMaxWidth().height(145.dp)) {
            // 封面图像
            AsyncImage(
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
                model = "${video.cover}@10q.webp",
                contentDescription = video.title,
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

            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(20.dp)
                    .padding(horizontal = 5.dp)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 播放数
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = Color.White,
                )
                Text(
                    text = video.viewContent,
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White)
                )

                // 间距
                Spacer(Modifier.width(5.dp))

                // 弹幕数
                Text(
                    text = "${video.danmaku}弹幕",
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White)
                )
                
                // 视频时长位于最右侧
                Text(
                    text = TimeUtils.formatSecondToTime(video.duration),
                    fontSize = 11.sp,
                    style = TextStyle(color = Color.White),
                    modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
                )
            }
        }
        
        // 下半部分 各种文字信息
        // 视频标题
        VideoInfoBar(video)
    }
}

@Composable
private fun VideoInfoBar(video: LikeVideo) {
    Spacer(Modifier.height(10.dp))
    // 标题
    Text(
        text = video.title,
        maxLines = 2,
        minLines = 2,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
    )
    
    // 最底下的up主等信息
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = video.author,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 12.sp
        )
        
        Spacer(Modifier.width(5.dp))
        
        Text(
            text = video.publishTimeText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.Gray,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 12.sp
        )
    }
}

private fun extractAidFromUri(uri: String): Long? {
    // 从URI中提取aid参数
    // URI格式类似: bilibili://video/624351941?player_width=1080&player_height=1920&player_rotate=0
    val regex = Regex("bilibili://video/(\\d+)")
    val matchResult = regex.find(uri)
    return matchResult?.groupValues?.get(1)?.toLongOrNull()
}