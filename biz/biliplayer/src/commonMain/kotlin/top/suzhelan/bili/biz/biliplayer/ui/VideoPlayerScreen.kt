package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.ui.controller.rememberPlayerController
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.ScreenSizeCalculation
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.isPhone
import top.suzhelan.bili.shared.navigation.CollectionPlaybackSource
import top.suzhelan.bili.shared.navigation.NavigationPageEffect


@Composable
fun VideoPlayerScreen(
    playerParams: PlayerParams,
    collectionSource: CollectionPlaybackSource? = null,
) {
    val context = BiliLocalContext.current
    val viewModel = viewModel { VideoPlayerViewModel(context) }
    NavigationPageEffect(viewModel::setPlaybackPageActive)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    LaunchedEffect(viewModel) { viewModel.initializePlayer(playerParams, collectionSource) }
    DisposableEffect(viewModel, lifecycle) {
        viewModel.setPlaybackForeground(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        val observer = LifecycleEventObserver { _, _ ->
            viewModel.setPlaybackForeground(lifecycle.currentState.isAtLeast(Lifecycle.State.RESUMED))
        }
        lifecycle.addObserver(observer)
        onDispose {
            viewModel.setPlaybackForeground(false)
            lifecycle.removeObserver(observer)
        }
    }
    AppScaffold {
        DialogHandler(viewModel)
        PlayerUI(
            viewModel = viewModel
        )
    }
}


@Composable
private fun PlayerUI(
    viewModel: VideoPlayerViewModel,
) {
    val preparedParams by viewModel.preparedPlayerParams.collectAsStateWithLifecycle()
    val fixParam = when (val state = preparedParams) {
        is BiliResponse.Success -> state.data
        is BiliResponse.Error -> {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (state.code == -404) "视频信息不存在或暂不可用"
                    else "获取视频信息失败：${state.msg}"
                )
                Button(onClick = viewModel::retryPlayer) {
                    Text("重试")
                }
            }
            return
        }

        else -> {
            LoadingIndicator(text = "获取视频信息中...")
            return
        }
    }
    val controller = rememberPlayerController(viewModel)
    ScreenSizeCalculation(modifier = Modifier.fillMaxSize()) { screenType ->
        if (screenType.isPhone()) {
            viewModel.controller.isFillMaxSize = false
            Column(modifier = Modifier.fillMaxSize()) {
                MediaUI(fixParam, viewModel)
                if (!controller.isFullScreen) {
                    key(fixParam.aid, fixParam.cid) { VideoInfoUI(fixParam, viewModel) }
                }
            }
        } else {
            viewModel.controller.isFillMaxSize = true
            Row(modifier = Modifier.fillMaxSize()) {
                MediaUI(fixParam, viewModel, modifier = Modifier.weight(2f))
                if (!controller.isFullScreen) {
                    key(fixParam.aid, fixParam.cid) {
                        VideoInfoUI(fixParam, viewModel, modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
