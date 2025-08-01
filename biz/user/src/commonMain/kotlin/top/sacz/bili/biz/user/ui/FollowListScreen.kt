package top.sacz.bili.biz.user.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerScope
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.paging.LoadState
import app.cash.paging.compose.collectAsLazyPagingItems
import app.cash.paging.compose.itemKey
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.sacz.bili.biz.user.entity.RelationUser
import top.sacz.bili.biz.user.viewmodel.FollowListViewModel
import top.sacz.bili.shared.common.ui.CommonComposeUI
import top.sacz.bili.shared.common.ui.LoadingIndicator
import top.sacz.bili.shared.common.ui.TitleUI
import top.sacz.bili.shared.common.ui.dialog.DialogHandler
import top.sacz.bili.shared.common.ui.theme.TextColor
import top.sacz.bili.shared.common.ui.theme.TipTextColor

/**
 * 我的关注列表
 */
object FollowListScreen : Screen {
    @Composable
    override fun Content() {
        CommonComposeUI<FollowListViewModel>(
            initAction = { vm ->
                vm.queryTags()
            },
            topBar = {
                val navigate = LocalNavigator.currentOrThrow
                TitleUI(title = "关注列表", onClickBack = {
                    navigate.pop()
                })
            }
        ) { vm ->
            DialogHandler(vm)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                TabPageUI(vm)
            }
        }
    }

    @Composable
    private fun ColumnScope.TabPageUI(vm: FollowListViewModel) {
        val scoop = rememberCoroutineScope()
        val tags = vm.tags
        val pagerState = rememberPagerState(pageCount = { tags.size }, initialPage = 0)
        //如果tags为空 则显示加载进度条
        if (tags.isEmpty()) {
            LoadingIndicator()
            return
        }
        TabRow(
            selectedTabIndex = pagerState.currentPage,
        ) {
            tags.forEachIndexed { index, item ->
                Tab(
                    text = {
                        Text(text = item.name)
                    },
                    selected = pagerState.currentPage == index,
                    onClick = {
                        scoop.launch { pagerState.animateScrollToPage(index) }
                    }
                )
            }
        }
        HorizontalPager(
            state = pagerState,
        ) { page ->
            val tag = tags[page]
            FollowListPage(vm, tag.tagid)
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun PagerScope.FollowListPage(vm: FollowListViewModel, tagId: Int) {
        val lazyPagingItems = vm.getFollowListFlow(tagId).collectAsLazyPagingItems()
        LazyColumn(
            modifier = Modifier.fillMaxSize()
        ) {
            items(
                count = lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey {
                    it.mid
                }
            ) { index ->
                val relationUser = lazyPagingItems[index]
                if (relationUser == null) {
                    return@items
                }
                UserItemUI(vm, relationUser)
            }
            when (lazyPagingItems.loadState.append) {
                is LoadState.Loading -> {
                    item {
                        LoadingIndicator()
                    }
                }

                is LoadState.NotLoading -> {
                    item {
                        Text(
                            text = "没有更多了",
                            modifier = Modifier.fillMaxWidth().padding(10.dp),
                            textAlign = TextAlign.Center,
                            color = TipTextColor
                        )
                    }
                }

                else -> {
                }
            }
        }
    }

    @Composable
    private fun LazyItemScope.UserItemUI(vm: FollowListViewModel, item: RelationUser) {
        var userState by remember { mutableStateOf(item) }
        ConstraintLayout(modifier = Modifier.fillMaxWidth().height(70.dp).padding(10.dp)) {
            //元素内容 头像 昵称 签名 关注按钮 头像左下角会员标识
            val (avatar, text, _, followBtn) = createRefs()
            AsyncImage(
                model = userState.face,
                contentDescription = "avatar",
                modifier = Modifier.constrainAs(avatar) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(parent.start)
                }
                    .height(50.dp)
                    .width(50.dp).clip(RoundedCornerShape(50.dp))
            )
            Column(
                modifier = Modifier.constrainAs(text) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    start.linkTo(avatar.end, 15.dp)
                    end.linkTo(followBtn.start, 10.dp)
                    width = Dimension.fillToConstraints
                },
                verticalArrangement = Arrangement.spacedBy((-2).dp)
            ) {
                Text(
                    text = userState.uname,
                    color = TextColor,
                    fontSize = 15.sp
                )
                Text(
                    text = userState.sign,
                    color = TipTextColor,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            when (userState.attribute) {
                0 -> {
                    OutlinedButton(
                        onClick = {
                            vm.addFollow(userState.mid) {
                                userState = userState.copy(attribute = it)
                            }
                        },
                        contentPadding = PaddingValues(
                            vertical = 0.dp,
                            horizontal = 0.dp
                        ),
                        modifier = Modifier
                            .size(85.dp, 30.dp)
                            .constrainAs(followBtn) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                        Text(text = "关注")
                    }
                }

                2, 6 -> {
                    //已关注
                    //右侧 关注按钮
                    FilledTonalButton(
                        onClick = {
                            vm.cancelFollow(userState.mid) {
                                userState = item.copy(attribute = it)
                            }
                        },
                        contentPadding = PaddingValues(
                            vertical = 0.dp,
                            horizontal = 0.dp
                        ),
                        modifier = Modifier
                            .size(85.dp, 30.dp)
                            .constrainAs(followBtn) {
                                top.linkTo(parent.top)
                                bottom.linkTo(parent.bottom)
                                end.linkTo(parent.end)
                            }
                    ) {
                        Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                        Text(text = if (userState.attribute == 2) "已关注" else "已互粉")
                    }
                }
            }

        }
    }
}