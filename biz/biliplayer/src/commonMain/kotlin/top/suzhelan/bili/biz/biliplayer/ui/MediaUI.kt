package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import top.suzhelan.bili.api.registerStatusListener
import top.suzhelan.bili.biz.biliplayer.entity.NextPlayback
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.controller.rememberPlayerController
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.ui.VideoPlayer
import top.suzhelan.bili.player.ui.indicator.VideoLoadingIndicator
import top.suzhelan.bili.shared.navigation.BiliBackHandler
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun MediaUI(playerParams: PlayerParams, vm: VideoPlayerViewModel, modifier: Modifier = Modifier) {
    val navigator = LocalNavigation.currentOrThrow
    val videoUrlData by vm.videoUrlData.collectAsStateWithLifecycle()
    LaunchedEffect(playerParams) {
        vm.getPlayerUrl(
            avid = playerParams.aid,
            bvid = playerParams.bvid,
            epid = playerParams.epid,
            seasonId = playerParams.seasonId,
            cid = playerParams.cid!!,
            qn = playerParams.qn
        )
    }
    //获取质量最好的音频
    val playerController = rememberPlayerController(vm)
    val isFullScreen = playerController.isFullScreen
    val isFillMaxSize = playerController.isFillMaxSize
    playerController.onBack = {
        if (isFullScreen) {
            playerController.reversalFullScreen()
        } else {
            navigator.pop()
        }
    }
    videoUrlData.registerStatusListener {
        onLoading {
            //加载中
            Box(
                modifier = if (isFullScreen || isFillMaxSize) modifier.fillMaxSize()
                else modifier.fillMaxWidth().aspectRatio(16f / 9f)
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                VideoLoadingIndicator(
                    text = {
                        Text(text = "获取视频信息...")
                    }
                )
            }
        }
        onSuccess {
            vm.doPlayer(playerController)
            val nextPlayback by vm.nextPlayback.collectAsStateWithLifecycle()
            val playbackFailed by playerController.playbackFailed.collectAsStateWithLifecycle()
            Box(
                modifier = if (isFullScreen || isFillMaxSize) modifier.fillMaxSize()
                else modifier.fillMaxWidth().aspectRatio(16f / 9f)
            ) {
                VideoPlayer(controller = playerController, modifier = Modifier.fillMaxSize())
                if (playbackFailed) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.9f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        Text("视频播放失败", color = Color.White)
                        Button(onClick = vm::retryPlayer) { Text("重试") }
                    }
                }
                nextPlayback?.let { next ->
                    NextPlaybackOverlay(next, vm::playNextNow, vm::cancelNextPlayback)
                }
            }
            ProgressReport(playerParams, vm, playerController)
            BiliBackHandler(isFullScreen) {
                playerController.reversalFullScreen()
            }
            AutoPaused(playerController)
        }
        onError { code, msg, cause ->
            Box(
                modifier = if (isFullScreen || isFillMaxSize) modifier.fillMaxSize()
                else modifier.fillMaxWidth().aspectRatio(16f / 9f),
                contentAlignment = Alignment.Center,
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "获取视频失败: $msg")
                    Button(onClick = vm::retryPlayer) { Text("重试") }
                }
            }
        }
    }
}

@Composable
private fun NextPlaybackOverlay(
    next: NextPlayback,
    onPlayNow: () -> Unit,
    onCancel: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.9f))
            .clickable(interactionSource = null, indication = null, onClick = {})
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            AsyncImage(
                model = next.cover,
                contentDescription = "下一条视频封面",
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 112.dp, height = 63.dp),
            )
            Column(modifier = Modifier.weight(1f)) {
                Text("${next.seconds}秒后播放下一条视频", color = Color.White)
                Text(
                    next.title, color = Color.White,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2, overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            TextButton(onClick = onCancel) { Text("取消", color = Color.White) }
            Button(onClick = onPlayNow) { Text("立即播放") }
        }
    }
}

/**
 * 监听生命周期自动暂停播放
 */
@Composable
private fun AutoPaused(playerSyncController: PlayerSyncController) {
    var pausedVideo by rememberSaveable { mutableStateOf(true) }
    val lifecycleOwner by rememberUpdatedState(LocalLifecycleOwner.current)
    DisposableEffect(lifecycleOwner) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_STOP -> {
                    if (playerSyncController.isPlaying) {
                        pausedVideo = true
                        playerSyncController.pause()
                    } else {
                        pausedVideo = false // 明确表示不是自动暂停
                    }
                }

                Lifecycle.Event.ON_START -> {
                    if (pausedVideo) {
                        playerSyncController.resume()
                        pausedVideo = false
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)
        onDispose {
            lifecycle.removeObserver(observer)
        }
    }
}

/**
 * 不要把这个放在上面 容易导致局部重组 Compose智能重组单位是方法 这样重组也只会重组这个方法
 * 上报观看进度
 */
@Composable
private fun ProgressReport(
    playerParams: PlayerParams,
    vm: VideoPlayerViewModel,
    playerSyncController: PlayerSyncController
) {
    // 完成态和毫秒进度统一换算，确保最后不足一秒也能上报。
    val currentPositionSeconds by playerSyncController.currentPositionSeconds.collectAsStateWithLifecycle()

    LaunchedEffect(currentPositionSeconds) {
        //至少观看1秒才上报,不然会把上次观看记录直接顶掉
        if (currentPositionSeconds > 0) {
            vm.reportViewingProgress(
                aid = playerParams.aid!!,
                cid = playerParams.cid!!,
                seconds = currentPositionSeconds
            )
        }
    }
}
