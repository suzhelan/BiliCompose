package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.longOrNull
import top.suzhelan.bili.biz.user.entity.SubmissionVideo
import top.suzhelan.bili.biz.user.entity.WatchHistoryPage
import top.suzhelan.bili.biz.user.entity.secureImageUrl
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

fun SubmissionVideo.watchProgress(history: WatchHistoryPage.Item?): WatchVideoProgress? {
    if (history != null) return WatchVideoProgress(
        history.progress,
        history.duration.takeIf { it > 0 } ?: durationSeconds)
    val percent = playbackPosition?.takeIf { it == -1 || it > 0 } ?: return null
    return WatchVideoProgress.fromPercentage(percent, durationSeconds)
}

@Composable
fun SubmissionVideoCard(
    video: SubmissionVideo,
    history: WatchHistoryPage.Item?,
    showWatchState: Boolean,
    isLastWatched: Boolean,
) {
    val navigation = LocalNavigation.currentOrThrow
    val progress = if (showWatchState) video.watchProgress(history) else null
    val lessonUrl = video.jumpUrl.takeIf {
        video.isLessonVideo != 0 && (it.startsWith("https://") || it.startsWith("http://"))
    }
    val isUnavailable = !video.canPlay && lessonUrl == null
    Card(
        Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(enabled = !isUnavailable) {
                if (lessonUrl != null) navigation.push(SharedScreen.WebView(lessonUrl))
                else navigation.push(
                    SharedScreen.VideoPlayer(
                        aid = video.aid.takeIf { it > 0 },
                        bvid = video.bvid.takeIf { it.isNotBlank() },
                    )
                )
            },
    ) {
        Row(Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
            WatchVideoCover(
                cover = video.cover.secureImageUrl(), title = video.title, progress = progress,
                modifier = Modifier.width(148.dp).fillMaxHeight(),
                isUnavailable = isUnavailable,
                durationLabel = video.length.ifBlank {
                    if (video.durationSeconds > 0) TimeUtils.formatSecondToTime(video.durationSeconds) else "--:--"
                },
                isLastWatched = showWatchState && (isLastWatched || video.cursorAttr?.isLastWatched == true),
            )
            Column(Modifier.weight(1f).fillMaxHeight().heightIn(min = 102.dp).padding(8.dp)) {
                Text(
                    video.title, fontSize = 14.sp, fontWeight = FontWeight.Medium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
                val badges = buildList {
                    if (video.isChargingArc) add("充电专属")
                    if (video.ugcPay != 0) add("付费")
                    if (video.isUnionVideo != 0) add("合作")
                    if (video.isLivePlayback != 0) add("直播回放")
                    if (video.isLessonVideo != 0) add("课堂")
                }
                if (badges.isNotEmpty()) Text(
                    badges.joinToString(" · "), color = MaterialTheme.colorScheme.primary,
                    fontSize = 11.sp, maxLines = 1, overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.weight(1f))
                WatchVideoMetadata(
                    author = video.author.ifBlank { history?.authorName.orEmpty() },
                    progress = progress,
                    viewAt = if (showWatchState) history?.viewAt ?: 0 else 0,
                    pubdate = video.created.takeIf { it > 0 } ?: history?.pubdate ?: 0,
                    isUnavailable = isUnavailable,
                )
                FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    if (!video.hideClick) SubmissionStatistic(
                        Icons.Outlined.SmartDisplay, "播放数", video.play,
                    )
                    SubmissionStatistic(Icons.Outlined.Subtitles, "弹幕数", video.danmaku)
                    if (video.comments != null) SubmissionStatistic(
                        Icons.Outlined.ChatBubbleOutline, "评论数", video.comments,
                    )
                }
            }
        }
    }
}

@Composable
private fun SubmissionStatistic(icon: ImageVector, label: String, value: JsonPrimitive?) {
    val number = value?.longOrNull
    val text = when {
        number != null -> number.takeIf { it >= 0 }?.toStringCount() ?: "--"
        else -> value?.contentOrNull?.takeIf { it.isNotBlank() } ?: "--"
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Icon(icon, contentDescription = label, Modifier.size(13.dp), tint = TipColor)
        Text(text, color = TipColor, fontSize = 11.sp, lineHeight = 14.sp, maxLines = 1)
    }
}
