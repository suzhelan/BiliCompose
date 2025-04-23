package top.sacz.bili.biz.recvids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import bilicompose.biz.recvids.generated.resources.Res
import bilicompose.biz.recvids.generated.resources.text_search
import bilicompose.biz.recvids.generated.resources.text_search_hint
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.biz.recvids.viewmodel.FeedViewModel
import top.sacz.bili.biz.user.config.AccountMapper
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.viewmodel.MineViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedVideoScreen() {
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())

    Scaffold(
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                ),
                title = {
                    HomeTopBar {}
                },
                scrollBehavior = scrollBehavior
            )
        },
    ) { paddingValue ->

        //垂直布局

        Column(modifier = Modifier.padding(paddingValue)) {
            Tab()
            RecommendedVideoContent()
        }
    }
}

@Composable
private fun Tab() {
    val titles = listOf("Tab 1", "Tab 2", "Tab 3", "Tab 4", "Tab 5", "Tab 6")
    var selectedIndex by remember { mutableStateOf(0) }
    TabRow(
        selectedTabIndex = selectedIndex,
    ) {
        titles.forEachIndexed { index, title ->
            Tab(
                text = { Text(text = title) },
                selected = selectedIndex == index,
                onClick = { selectedIndex = index }
            )
        }
    }
}

@Composable
private fun HomeTopBar(mineViewModel: MineViewModel = viewModel(), onClick: () -> Unit) {
    val isLogin by remember {
        AccountMapper.isLoginState
    }
    LaunchedEffect(isLogin) {
        mineViewModel.updateMyInfo()
    }
    val myInfoOrNull by mineViewModel.myInfo.collectAsState()
    Row {
        if (isLogin) {
            val myInfo: AccountInfo = myInfoOrNull ?: return@Row
            AsyncImage(
                model = myInfo.face,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
                    .clip(RoundedCornerShape(50.dp))
            )
        } else {
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {

                    }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        SearchBarWithClick(onClick)
    }

}

@Composable
private fun SearchBarWithClick(onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(36.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceContainer,
                shape = RoundedCornerShape(50.dp)
            )
            .clickable { onClick() } // 点击跳转逻辑
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 搜索图标
        Icon(
            imageVector = Icons.Default.Search,
            contentDescription = stringResource(Res.string.text_search),
            tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.width(8.dp))
        // 提示文字
        Text(
            text = stringResource(Res.string.text_search_hint),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecommendedVideoContent(
    modifier: Modifier = Modifier,
    viewModel: FeedViewModel = viewModel()
) {

    val lazyPagingItems = viewModel.recommendedListFlow.collectAsLazyPagingItems()
    val isRefreshing by derivedStateOf { lazyPagingItems.loadState.refresh is LoadState.Loading }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            lazyPagingItems.refresh()
        },
        modifier = modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            //骨架屏
            if (lazyPagingItems.itemCount == 0) {
                items(10) {
                    EmptyCard()
                }
            }
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.param }
            ) { index ->
                val video = lazyPagingItems[index]
                if (video != null) {
                    VideoCard(video)
                } else {
                    EmptyCard()
                }
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                BottomLoadingIndicator(lazyPagingItems.loadState.append, retry = {
                    lazyPagingItems.retry()
                })
            }
        }
    }
}

/**
 * 底部加载指示器
 */
@Composable
private fun BottomLoadingIndicator(loadState: LoadState, retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp), // 增加垂直间距
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(36.dp)
                )
            }

            is LoadState.Error -> {
                // 错误状态处理
                val error = loadState.error
                Button(
                    onClick = retry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer, // 错误容器色
                        contentColor = MaterialTheme.colorScheme.onErrorContainer // 对比色
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("加载失败，点击重试")
                }
            }

            else -> Unit
        }
    }
}
