package top.suzhelan.bili.biz.biliplayer.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection

@Composable
internal fun VideoCollectionCard(
    collection: VideoCollection,
    currentAid: Long,
    isPlaying: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val episodes = remember(collection) { collection.sections.flatMap { it.episodes } }
    val currentIndex = episodes.indexOfFirst { it.aid == currentAid }
    val totalCount = maxOf(collection.episodeCount, episodes.size)
    Card(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = "合集 · ${collection.title.ifBlank { "视频合集" }}",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            PlayingWaveIcon(isPlaying = isPlaying)
            Text(text = "${if (currentIndex >= 0) currentIndex + 1 else "-"}/$totalCount")
        }
    }
}
