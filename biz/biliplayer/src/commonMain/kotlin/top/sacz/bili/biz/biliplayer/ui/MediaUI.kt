package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.player.controller.rememberPlayerSyncController
import top.sacz.bili.player.ui.VideoPlayer


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
            val playerController = rememberPlayerSyncController()
            playerController.play(maxVideoUrl.baseUrl, maxAudioUrl.baseUrl)
            VideoPlayer(
                controller = playerController,
                modifier = Modifier.height(230.dp).fillMaxWidth()
            )
        }
        onError { code, msg, cause ->
            Text(text = "获取视频失败: $msg")
        }
    }
}