package top.suzhelan.bili.biz.biliplayer.ui.item

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import kotlinx.coroutines.flow.StateFlow
import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection
import top.suzhelan.bili.shared.common.ext.format
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils

@Composable
fun CollectionVideoItem(
    episode: VideoCollection.Episode,
    isCurrent: Boolean,
    currentPositionMillis: StateFlow<Long>,
    accountId: Long?,
    loadWatchProgress: suspend (aid: Long, cid: Long) -> Long?,
    onClick: () -> Unit,
) {
    val positionMillis = if (isCurrent) {
        val currentPosition by currentPositionMillis.collectAsStateWithLifecycle()
        currentPosition
    } else {
        val historicalPosition by produceState<Long?>(
            initialValue = null,
            key1 = accountId,
            key2 = episode.aid,
            key3 = episode.cid,
        ) {
            value = null
            if (accountId != null) value = loadWatchProgress(episode.aid, episode.cid)
        }
        historicalPosition
    }
    val archive = episode.arc
    val duration = archive?.duration?.coerceAtLeast(0) ?: 0
    val watchedSeconds = when {
        positionMillis == null || positionMillis == 0L -> null
        positionMillis < 0 -> duration
        duration > 0 -> (positionMillis / 1000).coerceAtMost(duration.toLong()).toInt()
        else -> null
    }
    val primaryColor = ColorPrimary
    val coverHeight = 76.dp
    Row(
        modifier = Modifier.fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .then(
                if (isCurrent) Modifier.background(MaterialTheme.colorScheme.primaryContainer)
                else Modifier
            )
            .clickable(enabled = episode.aid > 0 || episode.bvid.isNotBlank(), onClick = onClick)
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(
            modifier = Modifier.size(width = 116.dp, height = coverHeight)
                .clip(RoundedCornerShape(6.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
        ) {
            AsyncImage(
                model = archive?.pic,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            if (isCurrent) {
                Text(
                    text = "正在播放",
                    color = Color.White,
                    fontSize = 10.sp,
                    lineHeight = 12.sp,
                    modifier = Modifier.align(Alignment.TopStart).padding(4.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(primaryColor)
                        .padding(horizontal = 4.dp, vertical = 2.dp),
                )
            }
            Text(
                text = buildString {
                    if (watchedSeconds != null && watchedSeconds > 0) {
                        append("${TimeUtils.formatSecondToTime(watchedSeconds)} / ")
                    }
                    append(if (duration > 0) TimeUtils.formatSecondToTime(duration) else "--:--")
                },
                color = Color.White,
                fontSize = 10.sp,
                lineHeight = 12.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.align(Alignment.BottomEnd)
                    .padding(start = 4.dp, end = 4.dp, bottom = 6.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(Color.Black.copy(alpha = 0.55f))
                    .padding(horizontal = 4.dp, vertical = 2.dp)
                    .semantics {
                        contentDescription = buildString {
                            if (watchedSeconds != null && watchedSeconds > 0) {
                                append("已看 ${TimeUtils.formatSecondToTime(watchedSeconds)}，")
                            }
                            append("总时长 ${if (duration > 0) TimeUtils.formatSecondToTime(duration) else "未知"}")
                        }
                    },
            )
            if (watchedSeconds != null && watchedSeconds > 0 && duration > 0) {
                val progress = (watchedSeconds.toFloat() / duration).coerceIn(0f, 1f)
                Canvas(
                    modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth().height(3.dp)
                ) {
                    drawRect(color = primaryColor.copy(alpha = 0.25f))
                    drawRect(color = primaryColor, size = Size(size.width * progress, size.height))
                }
            }
        }
        Column(
            modifier = Modifier.weight(1f).height(coverHeight),
        ) {
            Text(
                text = episode.title.ifBlank { "视频" },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                fontSize = 14.sp,
                lineHeight = 17.sp,
                color = if (isCurrent) primaryColor else MaterialTheme.colorScheme.onSurface,
            )
            Spacer(modifier = Modifier.weight(1f))
            Text(
                text = if (archive != null && archive.pubdate > 0) {
                    TimeUtils.formatTimeAgo(archive.pubdate)
                } else "发布时间未知",
                color = TipColor,
                fontSize = 11.sp,
                lineHeight = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    Icons.Outlined.SmartDisplay,
                    "观看数",
                    tint = TipColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    archive?.stat?.view?.collectionCount() ?: "--",
                    color = TipColor,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                )
                Icon(
                    Icons.Outlined.Subtitles,
                    "弹幕数",
                    tint = TipColor,
                    modifier = Modifier.size(13.dp)
                )
                Text(
                    archive?.stat?.danmaku?.collectionCount() ?: "--",
                    color = TipColor,
                    fontSize = 11.sp,
                    lineHeight = 14.sp,
                )
            }
        }
    }
}

private fun Long.collectionCount(): String = when {
    this >= 100_000_000 -> "%.1f亿".format(toDouble() / 100_000_000)
    this >= 10_000 -> "%.1f万".format(toDouble() / 10_000)
    else -> toString()
}
