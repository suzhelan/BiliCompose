package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material.icons.outlined.Folder
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.FavoriteFolder
import top.suzhelan.bili.biz.user.viewmodel.FavoriteFoldersUiState
import top.suzhelan.bili.biz.user.viewmodel.FavoriteFoldersViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@Composable
@OptIn(ExperimentalMaterial3Api::class)
fun FavoriteFoldersScreen(mid: Long) {
    val navigator = LocalNavigation.currentOrThrow
    val vm = viewModel { FavoriteFoldersViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    val isOwner = LoginMapper.isLogin() && runCatching { LoginMapper.getMid() }.getOrNull() == mid
    LaunchedEffect(mid) { vm.load(mid, force = true) }
    AppScaffold(topBar = {
        TopAppBar(
            title = { Text(if (isOwner) "我的收藏" else "收藏夹") },
            navigationIcon = {
                IconButton(onClick = navigator::pop) {
                    Icon(Icons.Outlined.ArrowBackIosNew, contentDescription = "返回")
                }
            },
            actions = {
                if (isOwner) IconButton(onClick = { showCreate = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "新建收藏夹")
                }
            },
        )
    }) {
        FavoriteFoldersLazyContent(
            state = state,
            onRetry = { vm.load(mid, force = true) },
            onLoadMore = vm::loadMoreCollected,
            onOpen = { navigator.push(SharedScreen.FavoriteDetail(it.id)) },
        )
    }
    if (showCreate) CreateFavoriteFolderDialog(
        saving = state.isMutating,
        onDismiss = { showCreate = false },
        onCreate = { title, intro, private ->
            vm.createFolder(title, intro, private) { showCreate = false }
        },
    )
}

@Composable
fun FavoriteFoldersProfileContent(mid: Long) {
    val navigator = LocalNavigation.currentOrThrow
    val vm = viewModel { FavoriteFoldersViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    LaunchedEffect(mid) { vm.load(mid, force = true) }
    when {
        state.isLoading -> Box(
            Modifier.fillMaxWidth().padding(32.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }

        state.error != null -> FavoriteMessage(state.error ?: "加载失败") {
            vm.load(
                mid,
                force = true
            )
        }

        state.created.isEmpty() && state.collected.isEmpty() -> FavoriteMessage("暂无公开收藏夹")
        else -> Column(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
            FavoriteFolderSection("创建的收藏夹", state.created) {
                navigator.push(SharedScreen.FavoriteDetail(it.id))
            }
            FavoriteFolderSection("收藏的其他人收藏夹", state.collected) {
                navigator.push(SharedScreen.FavoriteDetail(it.id))
            }
            if (state.collected.size < state.collectedCount) {
                Button(
                    onClick = vm::loadMoreCollected,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (state.isLoadingMore) "加载中…" else "加载更多")
                }
            }
        }
    }
}

@Composable
private fun FavoriteFoldersLazyContent(
    state: FavoriteFoldersUiState,
    onRetry: () -> Unit,
    onLoadMore: () -> Unit,
    onOpen: (FavoriteFolder) -> Unit,
) {
    when {
        state.isLoading -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        state.error != null && state.created.isEmpty() && state.collected.isEmpty() ->
            FavoriteMessage(state.error, onRetry)

        state.created.isEmpty() && state.collected.isEmpty() -> FavoriteMessage("暂无收藏夹")
        else -> LazyColumn(Modifier.fillMaxSize()) {
            if (state.created.isNotEmpty()) {
                item { FavoriteSectionHeader("我创建的收藏夹", state.created.size) }
                items(state.created, key = { "created-${it.id}" }) {
                    FavoriteFolderCard(
                        it,
                        onOpen
                    )
                }
            }
            if (state.collected.isNotEmpty()) {
                item { FavoriteSectionHeader("收藏的其他人收藏夹", state.collectedCount) }
                items(state.collected, key = { "collected-${it.id}" }) {
                    FavoriteFolderCard(
                        it,
                        onOpen
                    )
                }
                if (state.collected.size < state.collectedCount) item {
                    Button(
                        onClick = onLoadMore,
                        modifier = Modifier.fillMaxWidth().padding(16.dp)
                    ) {
                        Text(if (state.isLoadingMore) "加载中…" else "加载更多")
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteFolderSection(
    title: String,
    folders: List<FavoriteFolder>,
    onOpen: (FavoriteFolder) -> Unit,
) {
    if (folders.isEmpty()) return
    FavoriteSectionHeader(title, folders.size)
    folders.forEach { FavoriteFolderCard(it, onOpen) }
}

@Composable
private fun FavoriteSectionHeader(title: String, count: Int) {
    Text("$title · $count", fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(12.dp))
}

@Composable
private fun FavoriteFolderCard(folder: FavoriteFolder, onOpen: (FavoriteFolder) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp)
            .clickable(enabled = !folder.isInvalid) { onOpen(folder) },
        shape = RoundedCornerShape(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(96.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (folder.cover.isNotBlank()) {
                AsyncImage(
                    folder.cover,
                    folder.title,
                    Modifier.size(128.dp, 96.dp),
                    contentScale = ContentScale.Crop
                )
            } else {
                Box(Modifier.size(128.dp, 96.dp), contentAlignment = Alignment.Center) {
                    Icon(
                        Icons.Outlined.Folder,
                        null,
                        Modifier.size(40.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
            Column(
                Modifier.weight(1f).padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(5.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        folder.title,
                        Modifier.weight(1f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                    if (folder.isPrivate) Icon(Icons.Outlined.Lock, "私密", Modifier.size(16.dp))
                }
                val owner = folder.upper?.name.orEmpty()
                if (owner.isNotBlank()) Text(owner, color = TipColor, maxLines = 1)
                Text(
                    if (folder.isInvalid) "收藏夹已失效" else "${folder.mediaCount} 个内容",
                    color = TipColor
                )
            }
        }
    }
}

@Composable
private fun FavoriteMessage(text: String, retry: (() -> Unit)? = null) {
    Column(
        Modifier.fillMaxWidth().padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text, textAlign = TextAlign.Center, color = TipColor)
        retry?.let {
            Button(
                onClick = it,
                modifier = Modifier.padding(top = 12.dp)
            ) { Text("重试") }
        }
    }
}
