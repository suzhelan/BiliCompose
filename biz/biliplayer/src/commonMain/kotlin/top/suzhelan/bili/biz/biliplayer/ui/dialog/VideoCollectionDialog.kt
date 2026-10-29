package top.suzhelan.bili.biz.biliplayer.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection
import top.suzhelan.bili.biz.biliplayer.ui.item.CollectionVideoItem
import top.suzhelan.bili.shared.common.ui.theme.TipColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoCollectionDialog(
    collection: VideoCollection,
    currentAid: Long,
    currentPositionMillis: StateFlow<Long>,
    accountId: Long?,
    loadWatchProgress: suspend (aid: Long, cid: Long) -> Long?,
    onDismissRequest: () -> Unit,
    onVideoClick: (VideoCollection.Episode) -> Unit,
    favoriteAction: @Composable () -> Unit,
    sortOrder: CollectionSortOrder,
    onSortOrderChange: (CollectionSortOrder) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val sortedEpisodes = remember(collection, sortOrder) {
        collection.sections.map { sortOrder.sortEpisodes(it.episodes) }
    }
    val currentVideoIndex = remember(collection, sortedEpisodes, currentAid) {
        var itemIndex = 0
        collection.sections.forEachIndexed { sectionIndex, section ->
            val episodes = sortedEpisodes[sectionIndex]
            if (section.title.isNotBlank() && episodes.isNotEmpty()) itemIndex++
            val episodeIndex = episodes.indexOfFirst { it.value.aid == currentAid }
            if (episodeIndex >= 0) return@remember itemIndex + episodeIndex
            itemIndex += episodes.size
        }
        0
    }
    // 首次布局就显示当前视频，避免先展示第一条再跳转；索引包含分部标题。
    val listState = rememberLazyListState(initialFirstVisibleItemIndex = currentVideoIndex)
    val scope = rememberCoroutineScope()
    val showScrollToTop by remember {
        derivedStateOf {
            listState.canScrollBackward
        }
    }
    ModalBottomSheet(
        sheetState = sheetState,
        onDismissRequest = onDismissRequest,
    ) {
        Column(modifier = Modifier.fillMaxWidth().fillMaxHeight(0.6f)) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = collection.title.ifBlank { "视频合集" },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f),
                )
                if (showScrollToTop) {
                    TextButton(
                        onClick = { scope.launch { listState.animateScrollToItem(0) } },
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                        ),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                Icons.Outlined.ArrowUpward,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp),
                            )
                            Text("回顶", maxLines = 1)
                        }
                    }
                }
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Outlined.Close, contentDescription = "关闭合集列表")
                }
            }
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                favoriteAction()
                CollectionSortPopup(
                    sortOrder = sortOrder,
                    onSortOrderChange = {
                        onSortOrderChange(it)
                        listState.requestScrollToItem(0)
                    },
                )
            }
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (collection.sections.all { it.episodes.isEmpty() }) {
                    item { Text("暂无合集视频", color = TipColor) }
                }
                collection.sections.forEachIndexed { sectionIndex, section ->
                    if (section.title.isNotBlank() && section.episodes.isNotEmpty()) {
                        item(key = "section-$sectionIndex") {
                            Text(
                                text = section.title,
                                style = MaterialTheme.typography.labelLarge,
                                color = TipColor,
                                modifier = Modifier.padding(vertical = 4.dp),
                            )
                        }
                    }
                    itemsIndexed(
                        items = sortedEpisodes[sectionIndex],
                        key = { _, entry -> "$sectionIndex-${entry.index}-${entry.value.aid}" },
                    ) { _, entry ->
                        val episode = entry.value
                        CollectionVideoItem(
                            episode = episode,
                            isCurrent = episode.aid == currentAid,
                            currentPositionMillis = currentPositionMillis,
                            accountId = accountId,
                            loadWatchProgress = loadWatchProgress,
                            onClick = { onVideoClick(episode) },
                        )
                    }
                }
            }
        }
    }
}

