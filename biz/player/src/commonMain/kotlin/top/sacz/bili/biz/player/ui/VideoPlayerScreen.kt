package top.sacz.bili.biz.player.ui

import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.HttpJsonDecoder
import top.sacz.bili.biz.player.controller.PlayerSyncController
import top.sacz.bili.biz.player.model.SmallCoverV2Item
import top.sacz.bili.biz.player.player.AudioPlayerUI
import top.sacz.bili.biz.player.player.VideoPlayerUI
import top.sacz.bili.biz.player.player.getAudioPlayerHost
import top.sacz.bili.biz.player.player.getVideoPlayerHost
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel

class VideoPlayerScreen(val body: String) : Screen {
    override val key: ScreenKey
        get() = "/video"

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
        when (val video = data) {
            is BiliResponse.Loading -> {
                //加载指示器
                CircularProgressIndicator()
            }

            is BiliResponse.Error -> {}
            is BiliResponse.Success -> {
                val allVideo = video.data.dash.video
                val maxVideoUrl = allVideo.maxByOrNull { it.id }
                val audio = video.data.dash.audio
                val maxAudioUrl = audio.maxByOrNull { it.id }
                val controller = remember { PlayerSyncController() }

                val audioPlayerHost =
                    remember { getAudioPlayerHost(maxAudioUrl!!.baseUrl, controller) }

                val videoPlayerHost =
                    remember { getVideoPlayerHost(maxVideoUrl!!.baseUrl, controller) }

                VideoPlayerUI(videoPlayerHost)
                AudioPlayerUI(audioPlayerHost)
            }
        }
    }
}

