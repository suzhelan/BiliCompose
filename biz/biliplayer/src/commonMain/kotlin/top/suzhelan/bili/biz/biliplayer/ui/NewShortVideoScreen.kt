package top.suzhelan.bili.biz.biliplayer.ui

import androidx.compose.runtime.Composable
import top.suzhelan.bili.biz.biliplayer.entity.PlayerParams
import top.suzhelan.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.suzhelan.bili.player.platform.BiliLocalContext
import top.suzhelan.bili.shared.common.ui.CommonComposeUI


@Composable
fun NewShortVideoScreen(intent: PlayerParams) {
    //使用PlayerViewModel
    CommonComposeUI(viewModel = VideoPlayerViewModel(BiliLocalContext.current)){ vm ->
        //首先一个约束布局

    }
}