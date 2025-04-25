package top.sacz.bili.biz.recvids.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import bilicompose.biz.recvids.generated.resources.Res
import bilicompose.biz.recvids.generated.resources.text_search
import bilicompose.biz.recvids.generated.resources.text_search_hint
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.biz.user.config.AccountMapper
import top.sacz.bili.biz.user.entity.AccountInfo
import top.sacz.bili.biz.user.viewmodel.MineViewModel
import top.sacz.bili.shared.navigation.SharedScreen

/**
 * 本文件包含
 * TopBar(头像+搜索栏)
 * TabRow(标签栏 : 推荐 热门.. )
 * 用来切换page的
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeVideoScreen() {
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
private fun HomeTopBar(mineViewModel: MineViewModel = viewModel(), onClickSearchBar: () -> Unit) {
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
            //获取最近的导航
            val navigator = LocalNavigator.currentOrThrow
            //创建登录屏幕
            val login = rememberScreen(SharedScreen.Login)
            Icon(
                imageVector = Icons.Outlined.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(36.dp)
                    .clip(RoundedCornerShape(50.dp))
                    .clickable {
                        navigator.push(login)
                    }
            )
        }
        Spacer(modifier = Modifier.width(10.dp))
        SearchBarWithClick(onClickSearchBar)
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

