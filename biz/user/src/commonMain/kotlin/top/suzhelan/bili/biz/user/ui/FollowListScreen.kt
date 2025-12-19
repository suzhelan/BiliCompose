package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
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
import androidx.constraintlayout.compose.ConstraintLayoutScope
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.user.entity.RelationUser
import top.suzhelan.bili.biz.user.ui.dialog.TagsDialog
import top.suzhelan.bili.biz.user.viewmodel.FollowListViewModel
import top.suzhelan.bili.shared.common.ext.isInvisible
import top.suzhelan.bili.shared.common.ext.toFalse
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 我的关注列表
 */
@Composable
fun FollowListScreen() {
    CommonComposeUI<FollowListViewModel>(
        initAction = { vm ->
            vm.queryTags()
        },
        viewModel = FollowListViewModel(),
        topBar = {
            val navigate = LocalNavigation.currentOrThrow
            TitleUI(title = "关注列表", onClickBack = {
                navigate.pop()
            })
        }
    ) { vm ->
        DialogHandler(vm)
        Dialogs(vm)
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            TabPageUI(vm)
        }
    }
}

@Composable
private fun Dialogs(vm: FollowListViewModel) {
    val isShowSettingTagsDialog by vm.isShowSettingTagsDialog.collectAsStateWithLifecycle()
    if (isShowSettingTagsDialog) {
        TagsDialog(
            vm,
            onUpdate = {
                vm.queryTags()
            },
            onDismissRequest = {
                vm.isShowSettingTagsDialog.toFalse()
            }
        )
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

    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        tags.forEachIndexed { index, tag ->
            FilterChip(
                selected = pagerState.currentPage == index,
                onClick = {
                    scoop.launch { pagerState.animateScrollToPage(index) }
                },
                label = {
                    Text(text = tag.name)
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
            val relationUser = lazyPagingItems[index] ?: return@items
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
                        color = TipColor
                    )
                }
            }

            else -> {
            }
        }
    }
}

@Composable
private fun LazyItemScope.UserItemUI(
    vm: FollowListViewModel,
    item: RelationUser,
) {
    var userState by remember { mutableStateOf(item) }
    val actionState = vm.followList[item.mid]
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = {},
                onLongClick = {
                    vm.showSettingTagsDialog(item.mid)
                }
            ).padding(10.dp)
    ) {
        //元素内容 头像 昵称 签名 关注按钮 头像左下角会员标识
        val (avatar, text, followBtn, actionLoading) = createRefs()
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
                color = TipColor,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (actionState is BiliResponse.Loading) {
            CircularProgressIndicator(
                modifier = Modifier.size(30.dp)
                    .constrainAs(actionLoading) {
                        start.linkTo(followBtn.start)
                        end.linkTo(followBtn.end)
                        top.linkTo(followBtn.top)
                        bottom.linkTo(followBtn.bottom)
                    }
            )
        }
        ButtonByAttribute(
            modifier = Modifier
                .isInvisible(actionState is BiliResponse.Loading)
                .constrainAs(followBtn) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                },
            attribute = userState.attribute,
            mid = userState.mid,
            vm = vm
        ) {
            userState = userState.copy(attribute = it)
        }
    }
}


@Composable
private fun ConstraintLayoutScope.ButtonByAttribute(
    modifier: Modifier = Modifier,
    attribute: Int,
    mid: Long,
    vm: FollowListViewModel,
    onChange: (newAttribute: Int) -> Unit
) {
    when (attribute) {
        0 ->
            OutlinedButton(
                onClick = {
                    vm.addFollow(mid) {
                        onChange(it)
                    }
                },
                contentPadding = PaddingValues(
                    vertical = 0.dp,
                    horizontal = 0.dp
                ),
                modifier = modifier
                    .size(85.dp, 30.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                Text(text = "关注")
            }

        2, 6 ->
            FilledTonalButton(
                onClick = {
                    vm.cancelFollow(mid) {
                        onChange(it)
                    }
                },
                contentPadding = PaddingValues(
                    vertical = 0.dp,
                    horizontal = 0.dp
                ),
                modifier = modifier
                    .size(85.dp, 30.dp)
            ) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                Text(text = if (attribute == 2) "已关注" else "已互粉")
            }
    }
}