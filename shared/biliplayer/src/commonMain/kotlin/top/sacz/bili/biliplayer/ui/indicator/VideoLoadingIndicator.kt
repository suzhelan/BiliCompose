package top.sacz.bili.biliplayer.ui.indicator

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.flowOf
import org.openani.mediamp.ExperimentalMediampApi
import org.openani.mediamp.MediampPlayer
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.features.Buffering

/**
 * 视频指示器
 */
@OptIn(ExperimentalMediampApi::class)
@Composable
fun BiliVideoLoadingIndicator(
    mediampPlayer: MediampPlayer,
    modifier: Modifier = Modifier
) {

    val buffering = mediampPlayer.features[Buffering]
    val isBuffering by (buffering?.isBuffering
        ?: remember { flowOf(false) }).collectAsStateWithLifecycle(false)
    val state by mediampPlayer.playbackState.collectAsStateWithLifecycle()

    if (isBuffering ||
        state == PlaybackState.PAUSED_BUFFERING || // 如果不加这个, 就会有一段时间资源名字还没显示出来, 也没显示缓冲中
        state == PlaybackState.ERROR
    ) {
        VideoLoadingIndicator(
            modifier = modifier,
            state = state
        )
    }
}

@Composable
private fun VideoLoadingIndicator(
    modifier: Modifier = Modifier,
    state: PlaybackState
) {
    when (state) {
        PlaybackState.ERROR -> {
            Indicator(
                modifier,
                showProgress = false,
                text = {
                    Text(text = "播放出错 请重试或换条视频")
                })
        }

        else -> {
            Indicator(
                modifier,
                showProgress = true,
                text = {
                    Text(text = "缓冲中...")
                })
        }
    }
}

/**
 * 缓冲指示器
 */
@Composable
private fun Indicator(
    modifier: Modifier = Modifier,
    showProgress: Boolean = true,
    color: Color = MaterialTheme.colorScheme.primary,
    text: @Composable () -> Unit = {}
) {
    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        if (showProgress) {
            CircularProgressIndicator(Modifier.size(50.dp), strokeWidth = 5.dp)
        }
        Row(Modifier.padding(top = 8.dp)) {
            val mergedStyle = LocalTextStyle.current.merge(MaterialTheme.typography.labelLarge)
            CompositionLocalProvider(
                LocalTextStyle provides mergedStyle,
                LocalContentColor provides color, content = text,
            )
        }
    }
}