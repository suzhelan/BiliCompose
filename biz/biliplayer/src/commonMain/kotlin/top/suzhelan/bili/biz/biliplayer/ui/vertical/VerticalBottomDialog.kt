package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.biz.biliplayer.entity.VideoInfo
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.CommentContent
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.common.util.toStringCount

/**
 * 竖屏视频底部弹窗，简介和评论共用同一个 BottomSheet。
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VerticalVideoBottomSheet(
    video: VerticalVideoWrap,
    initialTab: VerticalSheetTab,
    onDismissRequest: () -> Unit,
) {
    val tabs = VerticalSheetTab.entries
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val pagerState = rememberPagerState(
        initialPage = initialTab.ordinal,
        pageCount = { tabs.size }
    )
    val scope = rememberCoroutineScope()

    LaunchedEffect(initialTab) {
        pagerState.scrollToPage(initialTab.ordinal)
    }

    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
                .fillMaxHeight(0.6f)
        ) {
            SecondaryTabRow(
                selectedTabIndex = pagerState.currentPage,
                containerColor = Color.Transparent
            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = {
                            scope.launch { pagerState.animateScrollToPage(index) }
                        },
                        text = { Text(tab.title) }
                    )
                }
            }
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxWidth().weight(1f)
            ) { page ->
                when (tabs[page]) {
                    VerticalSheetTab.Introduction -> IntroductionSheetPage(video = video)
                    VerticalSheetTab.Comment -> CommentContent(
                        oid = video.detailsInfo.aid.toString(),
                        type = CommentSourceType.Video
                    )
                }
            }
        }

    }
}

@Composable
private fun IntroductionSheetPage(video: VerticalVideoWrap) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = video.detailsInfo.title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
        item {
            Text(
                text = video.detailsInfo.desc.ifBlank { "暂无简介" },
                fontSize = 14.sp,
                lineHeight = 20.sp
            )
        }
        item {
            VideoMetaInfo(video.detailsInfo, video.onlineCountText)
        }
        if (video.videoTags.isNotEmpty()) {
            item {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    video.videoTags.forEach { tag ->
                        SuggestionChip(
                            onClick = {},
                            label = { Text(tag.tagName) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun VideoMetaInfo(videoInfo: VideoInfo, onlineCountText: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.04f))
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        MetaText("播放", videoInfo.stat.view.toStringCount())
        MetaText("弹幕", videoInfo.stat.danmaku.toStringCount())
        MetaText("发布时间", TimeUtils.formatTime(videoInfo.pubdate.toLong()))
        MetaText("正在看", onlineCountText.ifBlank { "-" })
        MetaText("AID", videoInfo.aid.toString())
        MetaText("BV号", videoInfo.bvid)
        MetaText("CID", videoInfo.cid.toString())
    }
}

@Composable
private fun MetaText(label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(text = label, fontSize = 13.sp, color = Color.Gray, modifier = Modifier.width(72.dp))
        Text(text = value, fontSize = 13.sp, modifier = Modifier.weight(1f))
    }
}

internal enum class VerticalSheetTab(val title: String) {
    Introduction("简介"),
    Comment("评论")
}
