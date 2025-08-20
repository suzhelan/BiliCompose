package top.sacz.bili.biliplayer.ui.bottombar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.sacz.bili.biliplayer.controller.PlayerSyncController
import top.sacz.bili.biliplayer.ui.progress.PlayerProgressSlider
import top.sacz.bili.biliplayer.ui.progress.PlayerProgressSliderState

@Composable
fun PlayerBottomBar(
    progressSliderState: PlayerProgressSliderState,
    controller: PlayerSyncController
) = Row(
    modifier = Modifier.fillMaxWidth()
        .background(
            Brush.verticalGradient(
                0f to Color.Transparent,
                1f to Color.Black.copy(alpha = 0.4f)
            )
        ),
    verticalAlignment = Alignment.CenterVertically
) {
    val playbackState by controller.playbackState.collectAsStateWithLifecycle()
    val isPlaying by derivedStateOf {
        playbackState.isPlaying
    }
    //最左侧 播放/暂时按钮
    IconButton(
        onClick = {
            if (isPlaying) {
                controller.pause()
            } else {
                controller.resume()
            }
        }
    ) {
        Icon(
            imageVector = if (isPlaying) {
                Icons.Outlined.Pause
            } else {
                Icons.Outlined.PlayArrow
            },
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp),
            contentDescription = null
        )
    }
    //中间 进度条
    PlayerProgressSlider(
        modifier = Modifier.weight(1f),
        state = progressSliderState,
    )
}