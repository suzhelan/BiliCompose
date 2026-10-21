package top.suzhelan.bili.biz.search.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import bilicompose.biz.search.generated.resources.Res
import bilicompose.biz.search.generated.resources.search_followers
import bilicompose.biz.search.generated.resources.search_uploads
import bilicompose.biz.search.generated.resources.search_views
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import top.suzhelan.bili.biz.search.entity.SearchUser
import top.suzhelan.bili.biz.search.entity.SearchVideo
import top.suzhelan.bili.shared.common.util.TimeUtils

@Composable
fun HighlightedSearchText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    maxLines: Int = 2,
) {
    val parts = remember(text) { searchTextParts(text) }
    val primary = MaterialTheme.colorScheme.primary
    Text(
        text = buildAnnotatedString {
            parts.forEach { part ->
                if (part.highlighted) withStyle(SpanStyle(color = primary)) { append(part.text) }
                else append(part.text)
            }
        },
        modifier = modifier, style = style, maxLines = maxLines, overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun SearchVideoCard(video: SearchVideo, onClick: () -> Unit, onAuthorClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(Modifier.fillMaxWidth().height(100.dp)) {
            Box(
                Modifier.width(160.dp).fillMaxHeight()
                    .background(MaterialTheme.colorScheme.surfaceContainerHighest)
            ) {
                AsyncImage(
                    model = searchImageUrl(video.cover), contentDescription = null,
                    contentScale = ContentScale.Crop, modifier = Modifier.fillMaxSize(),
                )
                if (video.duration.isNotBlank()) Surface(
                    modifier = Modifier.align(Alignment.BottomEnd).padding(4.dp),
                    shape = MaterialTheme.shapes.extraSmall,
                    color = MaterialTheme.colorScheme.scrim.copy(alpha = 0.7f),
                    contentColor = androidx.compose.ui.graphics.Color.White,
                ) {
                    Text(
                        searchDuration(video.duration),
                        Modifier.padding(horizontal = 4.dp, vertical = 2.dp),
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            Column(Modifier.weight(1f).fillMaxHeight().padding(10.dp)) {
                HighlightedSearchText(video.title, style = MaterialTheme.typography.titleSmall)
                Spacer(Modifier.weight(1f))
                Text(
                    plainSearchText(video.author),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable(enabled = video.mid > 0, onClick = onAuthorClick)
                        .padding(vertical = 2.dp),
                )
                Text(
                    text = stringResource(Res.string.search_views, searchCount(video.plays)) +
                            if (video.publishedAt > 0) " · ${TimeUtils.formatTimeAgo(video.publishedAt)}" else "",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun SearchUserCard(user: SearchUser, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier.size(64.dp).clip(CircleShape)
                    .background(MaterialTheme.colorScheme.secondaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Outlined.Person,
                    null,
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
                AsyncImage(
                    model = searchImageUrl(user.avatar),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
            }
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                HighlightedSearchText(
                    user.name,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 1
                )
                Text(
                    "${
                        stringResource(
                            Res.string.search_followers,
                            searchCount(user.fans)
                        )
                    } · ${stringResource(Res.string.search_uploads, searchCount(user.videos))}",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (user.signature.isNotBlank()) Text(
                    plainSearchText(user.signature),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
