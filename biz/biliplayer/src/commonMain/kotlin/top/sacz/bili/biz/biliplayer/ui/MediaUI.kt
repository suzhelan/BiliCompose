package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.biliplayer.entity.PlayerArgsItem
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.player.controller.PlayerSyncController
import top.sacz.bili.player.ui.VideoPlayer
import top.sacz.bili.shared.navigation.BiliBackHandler
import top.sacz.bili.shared.navigation.LocalNavigation
import top.sacz.bili.shared.navigation.currentOrThrow


@Composable
fun MediaUI(playerParams: PlayerParams, vm: VideoPlayerViewModel) {
    val navigator = LocalNavigation.currentOrThrow
    val videoUrlData by vm.videoUrlData.collectAsStateWithLifecycle()
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
            val playerController = remember {
                vm.controller
            }
            val isFullScreen = playerController.isFullScreen
            val isFillMaxSize = playerController.isFillMaxSize
            playerController.onBack = {
                if (isFullScreen) {
                    playerController.reversalFullScreen()
                } else {
                    navigator.pop()
                }
            }
            vm.doPlayer(playerController)
            VideoPlayer(
                controller = playerController,
                modifier = if (isFullScreen || isFillMaxSize) Modifier.fillMaxSize()
                else Modifier.fillMaxWidth().aspectRatio(16f / 9f)
            )
            ProgressReport(playerParams, video, vm, playerController)
            BiliBackHandler(isFullScreen) {
                playerController.reversalFullScreen()
            }
            AutoPaused(playerController)
        }
        onError { code, msg, cause ->
            Text(text = "获取视频失败: $msg")
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
 * 上报和回滚观看进度
 */
@Composable
private fun ProgressReport(
    playerParams: PlayerParams,
    argsItem: PlayerArgsItem,
    vm: VideoPlayerViewModel,
    playerSyncController: PlayerSyncController
) {
    val totalDurationMillis by playerSyncController.totalDurationMillis.collectAsStateWithLifecycle()
    val currentPositionMillis by playerSyncController.currentPositionMillis.collectAsStateWithLifecycle()
    //转换为秒 秒级变化才重组
    val currentPositionSeconds by derivedStateOf {
        currentPositionMillis / 1000
    }

    LaunchedEffect(totalDurationMillis) {
        if (totalDurationMillis == 0L) {
            return@LaunchedEffect
        }
        if (argsItem.lastPlayTime > 0//必须有上次观看的记录
            && argsItem.lastPlayTime < totalDurationMillis - 500//如果已经看完
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