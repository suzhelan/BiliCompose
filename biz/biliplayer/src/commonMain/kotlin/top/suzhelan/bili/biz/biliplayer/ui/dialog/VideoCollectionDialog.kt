package top.suzhelan.bili.biz.biliplayer.ui.dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.StateFlow
import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection
import top.suzhelan.bili.biz.biliplayer.ui.item.CollectionVideoItem
import top.suzhelan.bili.shared.common.ui.theme.TipColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun VideoCollectionDialog(
    collection: VideoCollection,
    currentAid: Long,
    currentPositionMillis: StateFlow<Long>,
    accountId: Long?,
    loadWatchProgress: suspend (aid: Long, cid: Long) -> Long?,
    onDismissRequest: () -> Unit,
    onVideoClick: (VideoCollection.Episode) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
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
                IconButton(onClick = onDismissRequest) {
                    Icon(Icons.Outlined.Close, contentDescription = "关闭合集列表")
                }
            }
            LazyColumn(
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
                        items = section.episodes,
                        key = { index, episode -> "$sectionIndex-$index-${episode.aid}" },
                    ) { _, episode ->
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

