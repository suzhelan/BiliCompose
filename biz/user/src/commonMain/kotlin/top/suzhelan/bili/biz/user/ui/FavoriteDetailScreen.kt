package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.CleaningServices
import androidx.compose.material.icons.outlined.DeleteOutline
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.FavoriteFolder
import top.suzhelan.bili.biz.user.entity.FavoriteResource
import top.suzhelan.bili.biz.user.viewmodel.FavoriteDetailViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.TimeUtils
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FavoriteDetailScreen(mediaId: Long) {
    val navigator = LocalNavigation.currentOrThrow
    val vm = viewModel { FavoriteDetailViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    var showEdit by remember { mutableStateOf(false) }
    var showDelete by remember { mutableStateOf(false) }
    var showClean by remember { mutableStateOf(false) }
    var resourceToDelete by remember(mediaId) { mutableStateOf<FavoriteResource?>(null) }
    var query by remember { mutableStateOf("") }
    LaunchedEffect(mediaId) { vm.load(mediaId) }

    val isOwner = state.folder?.mid?.let { owner ->
        LoginMapper.isLogin() && runCatching { LoginMapper.getMid() }.getOrNull() == owner
    } == true
    val isBusy = state.isLoading || state.isLoadingMore || state.isMutating

    AppScaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.folder?.title ?: "收藏夹") },
                navigationIcon = {
                    IconButton(onClick = navigator::pop) {
                        Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "返回")
                    }
                },
                actions = {
                    if (isOwner) {
                        IconButton(enabled = !isBusy, onClick = { showClean = true }) {
                            Icon(
                                Icons.Outlined.CleaningServices,
                                contentDescription = "清除失效内容"
                            )
                        }
                        IconButton(enabled = !isBusy, onClick = { showEdit = true }) {
                            Icon(Icons.Outlined.Edit, contentDescription = "编辑收藏夹")
                        }
                        IconButton(
                            enabled = !isBusy && state.folder?.isDefault == false,
                            onClick = { showDelete = true },
                        ) { Icon(Icons.Outlined.DeleteOutline, contentDescription = "删除收藏夹") }
                    }
                },
            )
        },
    ) {
        when {
            state.isLoading && state.folder == null -> Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }

            state.error != null && state.folder == null -> FavoriteDetailMessage(
                state.error ?: "加载失败"
            ) {
                vm.load(mediaId)
            }

            else -> LazyColumn(Modifier.fillMaxSize()) {
                state.folder?.let { folder -> item { FavoriteFolderHeader(folder) } }
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        TextField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = { Text("搜索收藏夹内视频") },
                            singleLine = true,
                            modifier = Modifier.weight(1f),
                        )
                        IconButton(
                            enabled = !isBusy,
                            onClick = { vm.load(mediaId, query = query) }) {
                            Icon(Icons.Outlined.Search, contentDescription = "搜索")
                        }
                    }
                }
                item {
                    Row(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        listOf(
                            "mtime" to "收藏时间",
                            "view" to "播放量",
                            "pubtime" to "投稿时间"
                        ).forEach { (value, label) ->
                            FilterChip(
                                selected = state.order == value,
                                enabled = !isBusy,
                                onClick = { vm.load(mediaId, query = state.query, order = value) },
                                label = { Text(label) },
                            )
                        }
                    }
                }
                if (state.resources.isEmpty() && !state.isLoading) {
                    item { FavoriteDetailMessage(if (state.query.isBlank()) "收藏夹还是空的" else "没有匹配的视频") }
                }
                items(state.resources, key = { "${it.type}-${it.id}" }) { resource ->
                    FavoriteResourceCard(
                        resource = resource,
                        canRemove = isOwner,
                        removingEnabled = !isBusy,
                        onRemove = { resourceToDelete = resource },
                    )
                }
                if (state.hasMore) item {
                    Button(
                        enabled = !isBusy,
                        onClick = vm::loadMore,
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                    ) { Text(if (state.isLoadingMore) "加载中…" else "加载更多") }
                }
                state.error?.let { error ->
                    item {
                        Text(
                            error,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier.padding(16.dp)
                        )
                    }
                }
            }
        }
    }

    state.folder?.let { folder ->
        if (showEdit) EditFavoriteFolderDialog(
            folder = folder,
            saving = state.isMutating,
            onDismiss = { showEdit = false },
            onSave = { title, intro, private ->
                vm.edit(title, intro, private) {
                    showEdit = false
                }
            },
        )
    }
    if (showClean) ConfirmFavoriteAction(
        title = "清除失效内容",
        message = "确定一键移除该收藏夹内所有已失效内容吗？此操作无法撤销。",
        confirm = "清除",
        saving = isBusy,
        error = state.error,
        onDismiss = { showClean = false },
        onConfirm = { vm.cleanInvalid { showClean = false } },
    )
    if (showDelete) ConfirmFavoriteAction(
        title = "删除收藏夹",
        message = "确定删除这个收藏夹吗？其中的收藏记录将无法恢复。",
        confirm = "删除",
        saving = isBusy,
        error = state.error,
        onDismiss = { showDelete = false },
        onConfirm = { vm.delete { showDelete = false; navigator.pop() } },
    )
    resourceToDelete?.let { resource ->
        ConfirmFavoriteAction(
            title = "删除收藏",
            message = "确定从当前收藏夹移除「${resource.title}」吗？",
            confirm = "删除",
            saving = isBusy,
            error = state.error,
            onDismiss = { resourceToDelete = null },
            onConfirm = { vm.removeResource(resource) { resourceToDelete = null } },
        )
    }
}

@Composable
private fun FavoriteFolderHeader(folder: FavoriteFolder) {
    Row(Modifier.fillMaxWidth().padding(12.dp)) {
        AsyncImage(
            model = folder.cover,
            contentDescription = folder.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.width(150.dp).height(92.dp),
        )
        Column(
            Modifier.weight(1f).padding(start = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(folder.title, fontWeight = FontWeight.Bold)
            Text(folder.upper?.name.orEmpty(), color = TipColor)
            if (folder.intro.isNotBlank()) Text(
                folder.intro,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                "${folder.mediaCount} 个内容 · ${folder.countInfo.play.toStringCount()} 播放 · ${if (folder.isPrivate) "私密" else "公开"}",
                color = TipColor,
            )
        }
    }
}

@Composable
private fun FavoriteResourceCard(
    resource: FavoriteResource,
    canRemove: Boolean,
    removingEnabled: Boolean,
    onRemove: () -> Unit,
) {
    val navigator = LocalNavigation.currentOrThrow
    Card(
        modifier = Modifier.fillMaxWidth().height(116.dp)
            .padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(enabled = !resource.isInvalid && resource.type == 2) {
                navigator.push(SharedScreen.VideoPlayer(aid = resource.id))
            },
    ) {
        Row(Modifier.fillMaxSize()) {
            Box(Modifier.width(150.dp).fillMaxHeight()) {
                AsyncImage(
                    resource.cover,
                    resource.title,
                    Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                if (resource.isInvalid) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("已失效", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                }
            }
            Column(Modifier.weight(1f).fillMaxHeight().padding(8.dp)) {
                Text(
                    resource.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.weight(1f))
                Text(resource.upper.name, color = TipColor, maxLines = 1)
                Text(
                    "${resource.countInfo.play.toStringCount()} 播放 · ${
                        TimeUtils.formatSecondToTime(
                            resource.duration
                        )
                    }",
                    color = TipColor,
                )
            }
            if (canRemove) {
                IconButton(enabled = removingEnabled, onClick = onRemove) {
                    Icon(Icons.Outlined.DeleteOutline, contentDescription = "删除收藏")
                }
            }
        }
    }
}

@Composable
private fun EditFavoriteFolderDialog(
    folder: FavoriteFolder,
    saving: Boolean,
    onDismiss: () -> Unit,
    onSave: (String, String, Boolean) -> Unit,
) {
    var title by remember(folder.id) { mutableStateOf(folder.title) }
    var intro by remember(folder.id) { mutableStateOf(folder.intro) }
    var private by remember(folder.id) { mutableStateOf(folder.isPrivate) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("编辑收藏夹") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    title,
                    { title = it.take(80) },
                    label = { Text("名称") },
                    singleLine = true
                )
                TextField(intro, { intro = it.take(200) }, label = { Text("简介") })
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("私密收藏夹", Modifier.weight(1f))
                    Switch(private, { private = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && !saving,
                onClick = { onSave(title, intro, private) }) { Text("保存") }
        },
        dismissButton = { TextButton(enabled = !saving, onClick = onDismiss) { Text("取消") } },
    )
}

@Composable
private fun ConfirmFavoriteAction(
    title: String,
    message: String,
    confirm: String,
    saving: Boolean,
    error: String?,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
) = AlertDialog(
    onDismissRequest = { if (!saving) onDismiss() },
    title = { Text(title) },
    text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(message)
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        }
    },
    confirmButton = {
        TextButton(enabled = !saving, onClick = onConfirm) {
            Text(if (saving) "处理中…" else confirm)
        }
    },
    dismissButton = { TextButton(enabled = !saving, onClick = onDismiss) { Text("取消") } },
)

@Composable
private fun FavoriteDetailMessage(message: String, onRetry: (() -> Unit)? = null) {
    Column(
        Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = TipColor)
        onRetry?.let {
            Button(
                onClick = it,
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("重试") }
        }
    }
}
