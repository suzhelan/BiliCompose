package top.suzhelan.bili.biz.biliplayer.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.biz.biliplayer.entity.VideoCollection

@Composable
fun VideoCollectionCard(
    collection: VideoCollection,
    currentAid: Long,
    isPlaying: Boolean,
    onClick: () -> Unit,
    favoriteAction: @Composable () -> Unit,
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
            modifier = Modifier.fillMaxWidth()
                .padding(start = 12.dp, end = 4.dp, top = 4.dp, bottom = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = "合集 · ${collection.title.ifBlank { "视频合集" }}",
                modifier = Modifier.weight(1f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            PlayingWaveIcon(isPlaying = isPlaying)
            Text(
                text = "${if (currentIndex >= 0) currentIndex + 1 else "-"}/$totalCount",
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
            )
            Icon(
                imageVector = Icons.Outlined.KeyboardArrowDown,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            favoriteAction()
        }
    }
}
