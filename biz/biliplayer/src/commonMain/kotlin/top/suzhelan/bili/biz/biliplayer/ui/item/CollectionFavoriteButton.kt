package top.suzhelan.bili.biz.biliplayer.ui.item

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.biz.biliplayer.viewmodel.CollectionFavoriteState

@Composable
internal fun CollectionFavoriteButton(
    state: CollectionFavoriteState,
    onClick: () -> Unit,
    iconOnly: Boolean = false,
) {
    val failed = state.isFavorite == null && state.error != null
    val label = when {
        state.isSaving -> "提交中"
        state.isLoading -> "加载中"
        failed -> "重试"
        state.isFavorite == true -> "已收藏"
        else -> "收藏合集"
    }
    val buttonModifier = Modifier.semantics {
        contentDescription = when {
            state.isLoading || state.isSaving -> label
            failed -> "合集收藏状态加载失败，点击重试"
            state.isFavorite == true -> "已收藏合集，点击取消收藏"
            else -> label
        }
    }
    val icon: @Composable () -> Unit = {
        if (state.isLoading || state.isSaving) {
            CircularProgressIndicator(
                modifier = Modifier.size(if (iconOnly) 24.dp else 18.dp),
                strokeWidth = 2.dp,
            )
        } else {
            Icon(
                imageVector = when {
                    failed -> Icons.Outlined.Refresh
                    state.isFavorite == true -> Icons.Filled.Star
                    else -> Icons.Outlined.StarOutline
                },
                contentDescription = null,
                modifier = Modifier.size(if (iconOnly) 28.dp else 18.dp),
                tint = when {
                    !iconOnly -> LocalContentColor.current
                    state.isFavorite == true -> MaterialTheme.colorScheme.primary
                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                },
            )
        }
    }
    if (iconOnly) {
        IconButton(
            onClick = onClick,
            enabled = !state.isLoading && !state.isSaving,
            modifier = buttonModifier.size(48.dp),
        ) { icon() }
    } else {
        TextButton(
            onClick = onClick,
            enabled = !state.isLoading && !state.isSaving,
            modifier = buttonModifier,
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                icon()
                Text(label, maxLines = 1)
            }
        }
    }
}
