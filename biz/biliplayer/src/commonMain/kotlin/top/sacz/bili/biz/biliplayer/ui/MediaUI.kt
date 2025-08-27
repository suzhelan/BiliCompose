package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.player.ui.VideoPlayerUI


@Composable
fun MediaUI(playerParams: PlayerParams, vm: VideoPlayerViewModel) {
    val videoUrlData by vm.videoUrlData.collectAsState()
    LaunchedEffect(Unit) {
        vm.getPlayerUrl(
            avid = playerParams.avid,
            bvid = playerParams.bvid,
            epid = playerParams.epid,
            seasonId = playerParams.seasonId,
            cid = playerParams.cid,
            qn = playerParams.qn
        )
    }

    videoUrlData.registerStatusListener {
        onSuccess { video ->
            //获取质量最好的音频
            val allVideo = video.dash.video
            val audio = video.dash.audio
            val maxVideoUrl = allVideo.maxBy { it.id }
            val maxAudioUrl = audio.maxBy { it.id }
            VideoPlayerUI(
                videoUrl = maxVideoUrl.baseUrl,
                audioUrl = maxAudioUrl.baseUrl
            )
        }
        onError { code, msg, cause ->
            Text(text = "获取视频失败: $msg")
        }
    }
}