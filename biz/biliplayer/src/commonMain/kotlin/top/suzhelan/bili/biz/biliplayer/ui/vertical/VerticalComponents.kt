package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.biz.biliplayer.ui.dialog.SelectCoinCountDialog
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel

/**
 * 短视频播放器覆盖层。
 *
 * 这里只负责单页状态收集和布局组装，具体 UI 分散到同包下的组件文件中。
 */
@Composable
fun VerticalVideoOverlay(
    page: Int,
    videoPoolData: StateFlow<List<VerticalVideoWrap>>,
    viewModel: VerticalVideoViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // 只在单页 overlay 内收集视频详情，避免 NewVerticaScreen 顶层跟着详情接口一起重组。
    val videoPool by videoPoolData.collectAsStateWithLifecycle()
    val video = videoPool.getOrNull(page) ?: VerticalVideoWrap.empty()
    var sheetTab by remember(video.id) { mutableStateOf<VerticalSheetTab?>(null) }
    var showCoinDialog by remember(video.id) { mutableStateOf(false) }

    if (showCoinDialog) {
        SelectCoinCountDialog(
            onSelect = { count ->
                viewModel.addCoin(videoId = video.id, aid = video.detailsInfo.aid, multiply = count)
            },
            onDismissRequest = { showCoinDialog = false }
        )
    }

    Box(modifier = modifier.fillMaxSize()) {
        VerticalTopBar(
            onlineCountText = video.onlineCountText,
            onBack = onBack,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .statusBarsPadding()
        )
        VerticalActionBar(
            video = video,
            onLikeClick = {
                viewModel.likeVideo(
                    videoId = video.id,
                    aid = video.detailsInfo.aid,
                    like = !video.isLike
                )
            },
            onCommentClick = { sheetTab = VerticalSheetTab.Comment },
            onCoinClick = {
                if (video.coinQuotationCount >= MAX_COIN_COUNT) {
                    viewModel.showMessageDialog("提示", "您已经投过币了,请勿重复投币")
                } else {
                    showCoinDialog = true
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .navigationBarsPadding()
                .padding(end = 12.dp, bottom = 28.dp)
        )
        VerticalInfoPanel(
            video = video,
            onMoreDescriptionClick = { sheetTab = VerticalSheetTab.Introduction },
            onFollowClick = {
                viewModel.followAuthor(
                    videoId = video.id,
                    mid = video.detailsInfo.owner.mid,
                    follow = !video.isFollowingAuthor
                )
            },
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 12.dp, end = 84.dp, bottom = 28.dp)
        )
        sheetTab?.let { tab ->
            VerticalVideoBottomSheet(
                video = video,
                initialTab = tab,
                onDismissRequest = { sheetTab = null }
            )
        }
    }
}

private const val MAX_COIN_COUNT = 2
