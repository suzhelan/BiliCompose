package top.sacz.bili.biz.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Replay
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import org.openani.mediamp.PlaybackState
import org.openani.mediamp.compose.MediampPlayerSurface
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.biz.player.controller.rememberPlayerSyncController
import top.sacz.bili.biz.player.model.PlayerParams
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel

@Composable
fun VideoPlayer(playerParams: PlayerParams, viewModel: VideoPlayerViewModel = viewModel())  {
    val data by viewModel.video.collectAsState()
    LaunchedEffect(playerParams) {
        viewModel.getPlayerInfo(
            avid = playerParams.avid,
            bvid = playerParams.bvid,
            epid = playerParams.epid,
            seasonId = playerParams.seasonId,
            cid = playerParams.cid,
            qn = playerParams.qn
        )
    }
    when (val video = data) {
        is BiliResponse.Success -> {
            //获取质量最好的链接
            val allVideo = video.data.dash.video
            val maxVideoUrl = allVideo.maxByOrNull { it.id }
            val audio = video.data.dash.audio
            val maxAudioUrl = audio.maxByOrNull { it.id }
            val controller = rememberPlayerSyncController()
            controller.play(maxVideoUrl!!.baseUrl, maxAudioUrl!!.baseUrl)
            VideoPlayer(controller)
        }
        is BiliResponse.Error -> {
            Text(text = "加载失败 ${(data as BiliResponse.Error).cause}")
        }
        is BiliResponse.Loading -> {
            CircularProgressIndicator(
                modifier = Modifier.size(100.dp)
            )
        }
        else -> {
        }
    }

}

@Composable
private fun VideoPlayer(controller: PlayerSyncController)= Box(
    modifier = Modifier.fillMaxWidth().height(230.dp)
) {
    MediampPlayerSurface(
        controller.videoPlayer,
        Modifier.fillMaxSize()
    )
    val playbackState by controller.videoPlayer.playbackState.collectAsState()

    val progress by combine(
        controller.videoPlayer.mediaProperties.filterNotNull(),
        controller.videoPlayer.currentPositionMillis
    ) { properties, duration ->
        if (properties.durationMillis == 0L) {
            return@combine 0f
        }
        (duration.toFloat() / properties.durationMillis.toFloat()).coerceIn(0f, 1f)
    }.collectAsState(0f)

    //播放进度条
    LinearProgressIndicator(
        progress = {
            progress
        },
        modifier = Modifier.fillMaxWidth().align(Alignment.BottomCenter),
    )
    // 添加一个透明的点击层
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
            .clickable {
                when (playbackState) {
                    PlaybackState.PAUSED -> {
                        controller.resume()
                    }

                    else -> {
                        controller.pause()
                    }
                }
            }
    )

    when (playbackState) {
        PlaybackState.READY -> {
            //准备状态
            BufferIndicator(
                modifier = Modifier.size(100.dp).align(Alignment.Center)
            )
        }

        PlaybackState.PAUSED -> {
            //用户主动暂停状态
            if (progress == 1f) {
                Icon(
                    imageVector = Icons.Outlined.Replay,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(100.dp).align(Alignment.Center)
                        .clickable {
                            controller.seekTo(0)
                        }
                )
            } else {
                Icon(
                    imageVector = Icons.Outlined.PlayArrow,
                    tint = MaterialTheme.colorScheme.primary,
                    contentDescription = null,
                    modifier = Modifier.size(100.dp).align(Alignment.Center)
                )
            }
        }

        PlaybackState.PLAYING -> {
            //播放中
            Text(
                text = "播放中...",
                color = MaterialTheme.colorScheme.primary,
                style = MaterialTheme.typography.displaySmall,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        PlaybackState.PAUSED_BUFFERING -> {
            //因为缓冲导致的暂停,缓冲好了就恢复
            BufferIndicator(
                modifier = Modifier.size(100.dp).align(Alignment.Center)
            )
            Text("加载中 ...")
        }

        PlaybackState.FINISHED -> {
            //播放完成 显示重播
            Icon(
                imageVector = Icons.Outlined.Replay,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(100.dp).align(Alignment.Center)
                    .clickable {
                        controller.seekTo(0)
                    }
            )
        }

        PlaybackState.ERROR -> {
            //错误
        }
    }
}

@Composable
private fun BufferIndicator(modifier: Modifier) {
    CircularProgressIndicator(
        strokeWidth = 10.dp,
        modifier = modifier.size(100.dp)
    )
}