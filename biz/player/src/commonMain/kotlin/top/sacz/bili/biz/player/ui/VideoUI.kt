package top.sacz.bili.biz.player.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import org.openani.mediamp.compose.MediampPlayerSurface
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.biz.player.controller.rememberPlayerSyncController
import top.sacz.bili.biz.player.model.PlayerParams
import top.sacz.bili.biz.player.ui.loading.BiliVideoLoadingIndicator
import top.sacz.bili.biz.player.ui.progress.PlayerProgressSlider
import top.sacz.bili.biz.player.ui.progress.rememberPlayerProgressSliderState
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel

@Composable
fun VideoPlayer(playerParams: PlayerParams, viewModel: VideoPlayerViewModel = viewModel()) {
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
private fun VideoPlayer(controller: PlayerSyncController) = Box(
    modifier = Modifier.fillMaxWidth().height(230.dp)
) {
    MediampPlayerSurface(
        controller.videoPlayer,
        Modifier.fillMaxSize()
    )
    // 总时长
    val totalDurationMillis by controller.videoPlayer.mediaProperties.collectAsState()
    // 当前播放进度
    val currentPositionMillis by controller.videoPlayer.currentPositionMillis.collectAsState()
    // 添加一个用于跟踪Slider位置的状态
    val progressSliderState = rememberPlayerProgressSliderState(
        currentPositionMillis = {
            currentPositionMillis
        },
        totalDurationMillis = {
            totalDurationMillis?.durationMillis ?: 0L
        },
        onProView = {
        },
        onFinished = {
            controller.seekTo(it)
        }
    )


    //加载指示器
    BiliVideoLoadingIndicator(
        modifier = Modifier.align(Alignment.Center),
        mediampPlayer = controller.videoPlayer
    )

    PlayerProgressSlider(
        modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter),
        state = progressSliderState,
    )

}