package top.sacz.bili.biz.player.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import chaintech.videoplayer.ui.video.VideoPlayerComposable
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.HttpJsonDecoder
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.biz.player.model.SmallCoverV2Item
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel
import top.sacz.bili.shared.common.logger.Logger

class VideoPlayerScreen(private val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val viewModel: VideoPlayerViewModel = viewModel()
        val body: SmallCoverV2Item = HttpJsonDecoder.decodeFromString(body)
        val data by viewModel.video.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.getVideoInfo(
                avid = body.playerArgs.aid.toString(),
                cid = body.playerArgs.cid.toString(),
            )
        }
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = {
                        Text(text = "视频播放页")
                    }
                )
            }
        ) { paddingValues ->
            when (val video = data) {
                is BiliResponse.Loading -> {
                    //加载指示器
                    CircularProgressIndicator(modifier = Modifier.fillMaxSize().padding(paddingValues))
                }
                is BiliResponse.Error -> {}
                is BiliResponse.Success -> {
                    val allVideo = video.data.dash.video
                    val maxVideoUrl = allVideo.maxByOrNull { it.id }
                    val audio = video.data.dash.audio
                    val maxAudioUrl = audio.maxByOrNull { it.id }
                    val controller = remember {
                        PlayerSyncController(
                            videoUrl = maxVideoUrl!!.baseUrl,
                            audioUrl = maxAudioUrl!!.baseUrl
                        )
                    }
                    Logger.d("Url", "${maxVideoUrl!!.baseUrl} ${maxAudioUrl!!.baseUrl}")
                    VideoPlayerComposable(
                        playerHost = controller.videoPlayerHost,
                        modifier = Modifier.fillMaxWidth()
                            .padding(paddingValues)
                            .fillMaxHeight(0.3f)
                    )
                    VideoPlayerComposable(
                        playerHost = controller.audioPlayerHost,
                        modifier = Modifier.size(0.dp)
                    )
                    //音频播放器不会带header，所以这里用VideoPlayerComposable来显示音频
//                    AudioPlayer(playerHost = controller.audioPlayerHost)
                }
            }
        }

    }
}

@Composable
private fun PlayerScreen() {

}
