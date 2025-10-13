package top.suzhelan.bili.biz.shorts.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import org.openani.mediamp.compose.MediampPlayerSurface
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 短视频播放器组件
 *
 * 负责单个视频的播放、暂停和生命周期管理
 * 使用MediampPlayerSurface渲染，避免手势冲突
 *
 *
 * @param video 视频数据
 * @param isCurrentPage 是否为当前显示页
 * @param modifier Modifier
 * @param onSingleTap 单击回调
 * @param onDoubleTap 双击回调
 * @param playerController 播放器控制器
 *
 */
@Composable
fun ShortVideoPlayer(
    video: ShortVideoItem,
    isCurrentPage: Boolean,
    modifier: Modifier = Modifier,
    onSingleTap: () -> Unit = {},
    onDoubleTap: () -> Unit = {},
    playerController: PlayerSyncController? = null
) {

    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPaused by remember { mutableStateOf(false) }
    var videoData by remember { mutableStateOf<Pair<String, String>?>(null) }
    var playerInitialized by remember { mutableStateOf(false) }

    LaunchedEffect(video.aid) {
        isLoading = true
        errorMessage = null
        videoData = null
        playerInitialized = false

        try {
            val api = VideoPlayerApi()
            val response = api.getPlayerInfo(
                avid = video.aid,
                cid = video.cid,
                qn = 64
            )

            if (response.code == 0) {
                val videoUrl = response.data.dash.video.firstOrNull()?.baseUrl
                val audioUrl = response.data.dash.audio?.firstOrNull()?.baseUrl

                if (videoUrl != null && audioUrl != null) {
                    videoData = Pair(videoUrl, audioUrl)
                    LogUtils.d("ShortVideoPlayer: 视频数据加载成功 - ${video.title}")
                } else {
                    errorMessage = "视频地址获取失败"
                    LogUtils.e("ShortVideoPlayer: 视频地址为空")
                }
            } else {
                errorMessage = "视频加载失败: ${response.message}"
                LogUtils.e("ShortVideoPlayer: API返回错误 - ${response.message}")
            }
        } catch (e: Exception) {
            errorMessage = "视频加载失败: ${e.message}"
            LogUtils.e("ShortVideoPlayer: 加载异常", e)
        } finally {
            isLoading = false
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, playerController) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerController?.pause()
                    LogUtils.d("ShortVideoPlayer: 生命周期暂停 - ${video.title}")
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isCurrentPage && !isPaused) {
                        playerController?.resume()
                        LogUtils.d("ShortVideoPlayer: 生命周期恢复 - ${video.title}")
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            // 不在这里关闭播放器，由 ShortVideoScreen 统一管理
            // scope.launch { playerController?.close() }
            LogUtils.d("ShortVideoPlayer: 组件销毁 - ${video.title}")
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 播放器为空时显示加载中
        if (playerController == null) {
            LoadingIndicator()
            return@Box
        }

        when {
            isLoading -> LoadingIndicator()

            errorMessage != null -> ErrorContent(errorMessage!!)

            videoData != null -> {
                val (videoUrl, audioUrl) = videoData!!

                // 初始化播放器
                InitializePlayer(
                    videoUrl = videoUrl,
                    audioUrl = audioUrl,
                    playerController = playerController,
                    isCurrentPage = isCurrentPage,
                    isPaused = isPaused,
                    onInitialized = { playerInitialized = true }
                )

                // 根据页面状态控制播放
                ControlPlayback(
                    playerController = playerController,
                    isCurrentPage = isCurrentPage,
                    isPaused = isPaused,
                    playerInitialized = playerInitialized,
                    videoTitle = video.title
                )

                // 渲染播放器Surface
                MediampPlayerSurface(
                    playerController.videoPlayer,
                    modifier = Modifier.fillMaxSize()
                )


                // 手势检测层
                GestureLayer(
                    onTap = {
                        isPaused = !isPaused
                        if (isPaused) {
                            playerController.pause()
                        } else {
                            playerController.resume()
                        }
                        onSingleTap()
                    },
                    onDoubleTap = onDoubleTap
                )
            }
        }
    }
}

/**
 * 加载指示器
 */
@Composable
private fun LoadingIndicator() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(color = Color.White)
    }
}

/**
 * 错误内容
 */
@Composable
private fun ErrorContent(message: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "播放失败",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodySmall,
                color = Color.White.copy(alpha = 0.7f)
            )
        }
    }
}

/**
 * 初始化播放器
 */
@Composable
private fun InitializePlayer(
    videoUrl: String,
    audioUrl: String,
    playerController: PlayerSyncController,
    isCurrentPage: Boolean,
    isPaused: Boolean,
    onInitialized: () -> Unit
) {
    LaunchedEffect(videoUrl, audioUrl) {
        if (!playerController.isPlaying) {
            playerController.play(videoUrl, audioUrl)
            onInitialized()
            LogUtils.d("ShortVideoPlayer: 播放器初始化完成")

            // 立即根据当前页面状态决定是否播放
            if (isCurrentPage && !isPaused) {
                playerController.resume()
                LogUtils.d("ShortVideoPlayer: 开始播放")
            } else {
                playerController.pause()
                LogUtils.d("ShortVideoPlayer: 初始暂停, isCurrentPage=$isCurrentPage")
            }
        }
    }
}

/**
 * 控制播放状态
 */
@Composable
private fun ControlPlayback(
    playerController: PlayerSyncController,
    isCurrentPage: Boolean,
    isPaused: Boolean,
    playerInitialized: Boolean,
    videoTitle: String
) {
    LaunchedEffect(isCurrentPage, isPaused, playerInitialized) {
        if (!playerInitialized) {
            LogUtils.d("ShortVideoPlayer: 等待播放器初始化完成 - $videoTitle")
            return@LaunchedEffect
        }

        if (isCurrentPage && !isPaused) {
            playerController.resume()
            LogUtils.d("ShortVideoPlayer: 恢复播放 - $videoTitle")
        } else {
            playerController.pause()
            LogUtils.d("ShortVideoPlayer: 暂停播放 - $videoTitle, isCurrentPage=$isCurrentPage, isPaused=$isPaused")
        }
    }
}

/**
 * 手势检测层
 */
@Composable
private fun GestureLayer(
    onTap: () -> Unit,
    onDoubleTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { onDoubleTap() },
                    onTap = { onTap() }
                )
            }
    )
}
