package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.biliplayer.entity.PlayerArgsItem
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.player.controller.PlayerSyncController
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
            val maxAudioUrl = audio?.maxBy { it.id }
            val playerController = rememberPlayerSyncController()
            //当前播放进度
            playerController.play(maxVideoUrl.baseUrl, maxAudioUrl?.baseUrl ?: "")
            VideoPlayer(
                controller = playerController,
                modifier = Modifier.height(230.dp).fillMaxWidth()
            )
            ProgressReport(playerParams, video, vm, playerController)
        }
        onError { code, msg, cause ->
            Text(text = "获取视频失败: $msg")
        }
    }
}

/**
 * 不要把这个放在上面 容易导致局部重组 Compose智能重组单位是方法 这样重组也只会重组这个方法
 */
@Composable
private fun ProgressReport(
    playerParams: PlayerParams,
    argsItem: PlayerArgsItem,
    vm: VideoPlayerViewModel,
    playerSyncController: PlayerSyncController
) {
    val totalDurationMillis by playerSyncController.totalDurationMillis.collectAsState()
    val currentPositionMillis by playerSyncController.currentPositionMillis.collectAsState()
    //转换为秒 秒级变化才重组
    val currentPositionSeconds by derivedStateOf {
        currentPositionMillis / 1000
    }
    LaunchedEffect(totalDurationMillis) {
        if (
            totalDurationMillis > 0//视频时长已加载
            && argsItem.lastPlayTime > 0//必须有上次观看的记录
        ) {
            playerSyncController.seekTo(argsItem.lastPlayTime)
        }
    }
    LaunchedEffect(currentPositionSeconds) {
        //至少观看1秒才上报,不然会把上次观看记录直接顶掉
        if (currentPositionSeconds > 0) {
            vm.reportViewingProgress(
                aid = playerParams.avid!!,
                cid = playerParams.cid,
                seconds = currentPositionSeconds
            )
        }
    }
}