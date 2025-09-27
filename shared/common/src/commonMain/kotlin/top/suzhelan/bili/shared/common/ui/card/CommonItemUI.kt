package top.suzhelan.bili.shared.common.ui.card

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import top.suzhelan.bili.shared.common.ui.shimmerEffect

/**
 * 骨架屏item
 */
@Composable
fun LoadingDecorativeCard(modifier: Modifier) {
    Box(modifier = modifier.fillMaxWidth().shimmerEffect()) {}
}