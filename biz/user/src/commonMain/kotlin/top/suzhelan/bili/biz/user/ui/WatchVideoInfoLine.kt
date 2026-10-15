package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils

class WatchVideoProgress(progress: Int, duration: Int) {
    private val totalSeconds = duration.coerceAtLeast(0)
    private val isComplete = progress == -1
    private val watchedSeconds = if (isComplete) totalSeconds else {
        progress.coerceAtLeast(0)
            .let { if (totalSeconds > 0) it.coerceAtMost(totalSeconds) else it }
    }
    val fraction: Float? = when {
        isComplete -> 1f
        totalSeconds > 0 && progress >= 0 -> watchedSeconds.toFloat() / totalSeconds
        else -> null
    }
    val timeLabel: String = when {
        totalSeconds > 0 -> "${TimeUtils.formatSecondToTime(watchedSeconds)} / ${
            TimeUtils.formatSecondToTime(
                totalSeconds
            )
        }"

        isComplete -> "已看完"
        else -> "${TimeUtils.formatSecondToTime(watchedSeconds)} / --:--"
    }
    val percentLabel: String = when {
        isComplete -> "已看 100%"
        fraction == null -> "观看进度未知"
        else -> {
            // 保留一位小数并向下取整，避免尚未看完时显示 100%。
            val tenths = watchedSeconds.toLong() * 1000 / totalSeconds
            val percent =
                if (tenths % 10 == 0L) "${tenths / 10}" else "${tenths / 10}.${tenths % 10}"
            "已看 $percent%"
        }
    }
}

@Composable
fun WatchVideoCover(
    cover: String,
    title: String,
    progress: WatchVideoProgress,
    modifier: Modifier = Modifier,
    isUnavailable: Boolean = false,
) {
    Box(modifier) {
        AsyncImage(
            model = cover,
            contentDescription = title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
        )
        Text(
            text = if (isUnavailable) "视频已失效" else progress.timeLabel,
            color = Color.White,
            fontSize = 10.sp,
            lineHeight = 12.sp,
            maxLines = 1,
            modifier = Modifier.align(Alignment.BottomEnd)
                .padding(end = 4.dp, bottom = 4.dp)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(4.dp))
                .padding(horizontal = 3.dp, vertical = 1.dp),
        )
    }
}

@Composable
fun WatchVideoMetadata(
    author: String,
    progress: WatchVideoProgress,
    viewAt: Long,
    pubdate: Long,
    isUnavailable: Boolean = false,
) {
    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
        if (author.isNotBlank()) WatchVideoMetadataLine(author)
        WatchVideoMetadataLine(if (isUnavailable) "无法播放" else progress.percentLabel)
        if (viewAt > 0) WatchVideoMetadataLine("上次观看：${TimeUtils.formatTimeAgo(viewAt)}")
        if (pubdate > 0) WatchVideoMetadataLine("发布：${TimeUtils.formatTimeAgo(pubdate)}")
    }
}

@Composable
private fun WatchVideoMetadataLine(text: String) {
    Text(
        text = text,
        color = TipColor,
        fontSize = 12.sp,
        lineHeight = 13.sp,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}
