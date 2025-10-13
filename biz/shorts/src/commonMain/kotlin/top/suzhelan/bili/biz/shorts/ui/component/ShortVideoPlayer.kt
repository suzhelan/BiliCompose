package top.suzhelan.bili.biz.shorts.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.launch
import org.openani.mediamp.compose.MediampPlayerSurface
import top.suzhelan.bili.biz.biliplayer.api.VideoPlayerApi
import top.suzhelan.bili.biz.shorts.entity.ShortVideoItem
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.shared.common.logger.LogUtils

/**
 * 短视频播放器组件
 * 负责单个视频的播放、暂停和生命周期管理
 * 注意：使用 MediampPlayerSurface 而不是 VideoPlayer 以避免手势冲突
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
    val scope = rememberCoroutineScope()

    // 播放器状态
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isPaused by remember { mutableStateOf(false) }
    var videoData by remember { mutableStateOf<Pair<String, String>?>(null) }
    var playerInitialized by remember { mutableStateOf(false) }

    // 第一步：先加载视频数据（完全模仿横屏播放器的方式）
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

    // 生命周期管理
    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, playerController) {
        val lifecycle = lifecycleOwner.lifecycle
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    playerController?.pause()
                }

                Lifecycle.Event.ON_RESUME -> {
                    if (isCurrentPage && !isPaused) {
                        playerController?.resume()
                    }
                }

                else -> {}
            }
        }
        lifecycle.addObserver(observer)

        onDispose {
            lifecycle.removeObserver(observer)
            scope.launch {
                playerController?.close()
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        // 如果 playerController 为 null，显示加载中
        if (playerController == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
            return@Box
        }

        when {
            isLoading -> {
                // 加载中
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color.White)
                }
            }

            errorMessage != null -> {
                // 错误状态
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
                            text = errorMessage ?: "",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.7f)
                        )
                    }
                }
            }

            videoData != null -> {
                val (videoUrl, audioUrl) = videoData!!

                // 初始化播放器（只执行一次）
                LaunchedEffect(videoUrl, audioUrl) {
                    if (!playerController.isPlaying) {
                        playerController.play(videoUrl, audioUrl)
                        playerInitialized = true
                        LogUtils.d("ShortVideoPlayer: 播放器初始化完成 - ${video.title}")

                        // 立即根据当前页面状态决定是否播放
                        if (isCurrentPage && !isPaused) {
                            playerController.resume()
                            LogUtils.d("ShortVideoPlayer: 开始播放 - ${video.title}")
                        } else {
                            playerController.pause()
                            LogUtils.d("ShortVideoPlayer: 初始暂停 - ${video.title}, isCurrentPage=$isCurrentPage")
                        }
                    }
                }

                // 根据页面状态控制播放（只在播放器初始化完成后才执行）
                LaunchedEffect(isCurrentPage, isPaused, playerInitialized) {
                    if (!playerInitialized) {
                        LogUtils.d("ShortVideoPlayer: 等待播放器初始化完成 - ${video.title}")
                        return@LaunchedEffect
                    }

                    if (isCurrentPage && !isPaused) {
                        playerController.resume()
                        LogUtils.d("ShortVideoPlayer: 恢复播放 - ${video.title}")
                    } else {
                        playerController.pause()
                        LogUtils.d("ShortVideoPlayer: 暂停播放 - ${video.title}, isCurrentPage=$isCurrentPage, isPaused=$isPaused")
                    }
                }

                // 渲染播放器 Surface
                MediampPlayerSurface(
                    playerController.videoPlayer,
                    modifier = Modifier.fillMaxSize()
                )

                // 手势检测层
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectTapGestures(
                                onDoubleTap = {
                                    onDoubleTap()
                                },
                                onTap = {
                                    isPaused = !isPaused
                                    if (isPaused) {
                                        playerController.pause()
                                    } else {
                                        playerController.resume()
                                    }
                                    onSingleTap()
                                }
                            )
                        }
                )

                // 渐变遮罩
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                0f to Color.Transparent,
                                1f to Color.Black.copy(alpha = 0.6f)
                            )
                        )
                )
            }
        }
    }
}
