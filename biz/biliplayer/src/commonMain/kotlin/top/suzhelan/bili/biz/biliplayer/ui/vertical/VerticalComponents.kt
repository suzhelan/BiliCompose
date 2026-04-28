package top.suzhelan.bili.biz.biliplayer.ui.vertical

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import top.suzhelan.bili.biz.biliplayer.entity.VerticalVideoWrap
import top.suzhelan.bili.biz.biliplayer.ui.dialog.SelectCoinCountDialog
import top.suzhelan.bili.biz.biliplayer.viewmodel.VerticalVideoViewModel
import top.suzhelan.bili.player.controller.PlayerSyncController
import top.suzhelan.bili.player.ui.progress.rememberPlayerProgressSliderState

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
    bottomReservedHeight: Dp = 0.dp,
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
                .padding(end = 12.dp, bottom = 28.dp + bottomReservedHeight)
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
                .padding(start = 12.dp, end = 84.dp, bottom = 28.dp + bottomReservedHeight)
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

/**
 * 竖屏页底部预留区域高度。
 *
 * 页面 overlay 和底部进度条共用同一个值，避免信息区和操作区与底部预留 Row 重叠。
 */
internal val VerticalBottomReservedHeight = 80.dp

/**
 * 竖屏页底部预留区域。
 *
 * 进度条放在 80dp 预留 Row 上方：默认显示迷你滑轨，用户点击或拖动后展开为完整滑轨，
 * 停止操作 3 秒后自动收回，避免长期遮挡视频信息。
 */
@Composable
internal fun VerticalBottomProgressArea(
    controller: PlayerSyncController,
    modifier: Modifier = Modifier,
    onPreview: (positionMillis: Long, totalDurationMillis: Long) -> Unit,
    onPreviewFinished: () -> Unit,
) {
    val totalDurationMillis by controller.totalDurationMillis.collectAsStateWithLifecycle()
    val currentPositionMillis by controller.currentPositionMillis.collectAsStateWithLifecycle()
    val latestTotalDurationMillis by rememberUpdatedState(totalDurationMillis)
    val latestCurrentPositionMillis by rememberUpdatedState(currentPositionMillis)
    var isExpanded by remember { mutableStateOf(false) }
    var interactionVersion by remember { mutableStateOf(0) }

    fun markUserInteraction() {
        isExpanded = true
        interactionVersion += 1
    }

    val progressSliderState = rememberPlayerProgressSliderState(
        currentPositionMillis = { latestCurrentPositionMillis },
        totalDurationMillis = { latestTotalDurationMillis },
        onPreView = {
            markUserInteraction()
            onPreview(it, latestTotalDurationMillis)
        },
        onFinished = {
            controller.seekTo(it)
            markUserInteraction()
            onPreviewFinished()
        }
    )

    LaunchedEffect(isExpanded, interactionVersion, progressSliderState.isPreviewing) {
        if (!isExpanded || progressSliderState.isPreviewing) return@LaunchedEffect
        delay(ProgressCollapseDelayMillis)
        if (!progressSliderState.isPreviewing) {
            isExpanded = false
        }
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        VerticalAdaptiveProgressSlider(
            state = progressSliderState,
            isExpanded = isExpanded,
            onUserInteraction = ::markUserInteraction,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(VerticalBottomReservedHeight)
        ) {
            // 预留给后续底部操作区，目前不承载具体内容。
            FilledTonalButton( onClick = {
            }){
                Text(text = "占位中 正在添加功能")
            }
        }
    }
}

private const val ProgressCollapseDelayMillis = 3_000L
