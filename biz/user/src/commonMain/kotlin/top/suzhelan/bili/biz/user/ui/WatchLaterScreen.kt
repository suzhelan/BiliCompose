package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.WatchLaterVideo
import top.suzhelan.bili.biz.user.viewmodel.WatchLaterViewModel
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatchLaterScreen() {
    val navigator = LocalNavigation.currentOrThrow
    val viewModel = viewModel { WatchLaterViewModel() }
    val state by viewModel.state.collectAsStateWithLifecycle()
    var videoToRemove by remember { mutableStateOf<WatchLaterVideo?>(null) }
    // 从播放器返回后重新读取观看进度和列表。
    LifecycleResumeEffect(Unit) {
        viewModel.load()
        onPauseOrDispose { }
    }
    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text("稍后再看") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "返回")
                    }
                },
                actions = {
                    IconButton(
                        enabled = !state.isLoading && state.removingAid == null,
                        onClick = { viewModel.load() },
                    ) {
                        Icon(Icons.Outlined.Refresh, contentDescription = "刷新稍后再看")
                    }
                },
            )
        },
    ) {
        DialogHandler(viewModel)
        videoToRemove?.let { video ->
            AlertDialog(
                onDismissRequest = { videoToRemove = null },
                title = { Text("移除稍后再看") },
                text = { Text("确定将“${video.title.ifBlank { "视频已失效" }}”从稍后再看中移除吗？") },
                confirmButton = {
                    TextButton(
                        enabled = !state.isLoading && state.removingAid == null,
                        onClick = {
                            videoToRemove = null
                            viewModel.remove(video.aid)
                        },
                    ) {
                        Text("删除")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { videoToRemove = null }) {
                        Text("取消")
                    }
                },
            )
        }
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            if (state.isLoading) item { LoadingIndicator() }
            state.error?.let { message ->
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(message, color = TipColor)
                        TextButton(enabled = !state.isLoading, onClick = { viewModel.load() }) {
                            Text("重试")
                        }
                    }
                }
            }
            if (!state.isLoading && state.error == null && state.videos.isEmpty()) {
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(36.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("暂无稍后再看的视频")
                        Text("在播放页点击“稍后再看”即可添加", color = TipColor, fontSize = 12.sp)
                    }
                }
            }
            if (state.videos.isNotEmpty()) {
                item { Text("共 ${state.videos.size} 个视频", color = TipColor, fontSize = 12.sp) }
            }
            items(state.videos, key = { it.aid }) { video ->
                WatchLaterVideoCard(
                    video = video,
                    isRemoving = state.removingAid == video.aid,
                    canRemove = !state.isLoading && state.removingAid == null,
                    onRemove = { videoToRemove = video },
                    onPlay = {
                        navigator.push(
                            SharedScreen.VideoPlayer(
                                aid = video.aid,
                                cid = video.cid.takeIf { it > 0 },
                            )
                        )
                    },
                )
            }
        }
    }
}

@Composable
private fun WatchLaterVideoCard(
    video: WatchLaterVideo,
    isRemoving: Boolean,
    canRemove: Boolean,
    onRemove: () -> Unit,
    onPlay: () -> Unit,
) {
    Card(Modifier.fillMaxWidth().height(112.dp)) {
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Row(
                Modifier.weight(1f).fillMaxHeight()
                    .clickable(enabled = !video.isUnavailable && !isRemoving, onClick = onPlay),
            ) {
                Box(Modifier.width(140.dp).fillMaxHeight()) {
                    AsyncImage(
                        model = video.pic,
                        contentDescription = video.title,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop,
                    )
                    Text(
                        text = if (video.isUnavailable) "视频已失效" else TimeUtils.formatSecondToTime(
                            video.duration
                        ),
                        color = Color.White,
                        fontSize = 11.sp,
                        modifier = Modifier.align(Alignment.BottomEnd)
                            .background(Color.Black.copy(alpha = 0.6f)).padding(4.dp),
                    )
                }
                Column(Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
                    Text(
                        text = video.title.ifBlank { "视频已失效" },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        fontWeight = FontWeight.Medium,
                        fontSize = 14.sp,
                    )
                    Spacer(Modifier.weight(1f))
                    Text(
                        video.owner.name,
                        color = TipColor,
                        fontSize = 12.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = when {
                            video.isUnavailable -> "无法播放"
                            video.progress == -1 -> "已看完"
                            video.progress > 0 -> "已观看 ${TimeUtils.formatSecondToTime(video.progress)}"
                            else -> "尚未观看"
                        },
                        color = TipColor,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            IconButton(
                enabled = canRemove,
                onClick = onRemove,
                colors = IconButtonDefaults.iconButtonColors(
                    contentColor = MaterialTheme.colorScheme.error,
                ),
            ) {
                if (isRemoving) {
                    CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
                } else {
                    Icon(Icons.Outlined.Delete, contentDescription = "移除稍后再看：${video.title}")
                }
            }
        }
    }
}
