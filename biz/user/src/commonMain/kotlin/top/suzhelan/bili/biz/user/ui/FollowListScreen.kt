package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.biz.login.ui.content.NotLoggedInContent
import top.suzhelan.bili.biz.user.entity.RelationListUser
import top.suzhelan.bili.biz.user.ui.dialog.TagsDialog
import top.suzhelan.bili.biz.user.viewmodel.FollowListViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ext.isInvisible
import top.suzhelan.bili.shared.common.ext.toFalse
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.common.ui.dialog.DialogHandler
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 用户关注列表，查看自己时支持分组管理。
 */
@Composable
fun FollowListScreen(mid: Long? = null) {
    RelationListScreen(mid, fans = false)
}

@Composable
fun FansListScreen(mid: Long? = null) {
    RelationListScreen(mid, fans = true)
}

@Composable
private fun RelationListScreen(mid: Long?, fans: Boolean) {
    val navigation = LocalNavigation.currentOrThrow
    val isLoggedIn by LoginMapper.isLoginState
    val listName = if (fans) "粉丝" else "关注"
    AppScaffold(
        topBar = {
            TitleUI(title = "${listName}列表", onClickBack = { navigation.pop() })
        }
    ) {
        if (!isLoggedIn) {
            NotLoggedInContent()
            return@AppScaffold
        }
        val accountMid = LoginMapper.getMid()
        val targetMid = mid ?: accountMid
        val canManageTags = !fans && targetMid == accountMid
        val viewModel = viewModel(key = "relationList:$fans:$targetMid:$accountMid") {
            FollowListViewModel(mid = targetMid.takeUnless { canManageTags }, fans = fans)
        }
        LifecycleResumeEffect(viewModel) {
            viewModel.onScreenResumed()
            if (canManageTags) viewModel.queryTags()
            onPauseOrDispose { }
        }
        DialogHandler(viewModel)
        if (canManageTags) {
            Dialogs(viewModel)
            Column(modifier = Modifier.fillMaxSize()) {
                TabPageUI(viewModel)
            }
        } else {
            FollowListPage(viewModel, tagId = -20, canManageTags = false, listName = listName)
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
private fun TabPageUI(vm: FollowListViewModel) {
    val scope = rememberCoroutineScope()
    val tags = vm.tags
    val tagsError by vm.tagsError.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { tags.size }, initialPage = 0)
    //如果tags为空 则显示加载进度条
    if (tags.isEmpty()) {
        if (tagsError != null) {
            TextButton(onClick = { vm.queryTags() }) {
                Text("${tagsError}\n点击重试")
            }
        } else LoadingIndicator()
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
                    scope.launch { pagerState.animateScrollToPage(index) }
                },
                label = {
                    Text(text = tag.name)
                }
            )
        }
    }

    HorizontalPager(
        state = pagerState,
        key = { tags[it].tagid },
    ) { page ->
        val tag = tags[page]
        FollowListPage(vm, tag.tagid)
    }
}

@Composable
private fun FollowListPage(
    vm: FollowListViewModel,
    tagId: Int,
    canManageTags: Boolean = true,
    listName: String = "关注",
) {
    val lazyPagingItems = vm.getFollowListFlow(tagId).collectAsLazyPagingItems()
    LaunchedEffect(lazyPagingItems, tagId, vm.refreshVersion) {
        vm.refreshListIfNeeded(tagId, lazyPagingItems::refresh)
    }
    val loadState = lazyPagingItems.loadState
    val refresh = loadState.refresh
    val append = loadState.append
    val hasItems = lazyPagingItems.itemCount > 0
    var lastEndReached by rememberSaveable(tagId) { mutableStateOf(false) }
    val endReached = if (refresh is LoadState.NotLoading) {
        append.endOfPaginationReached
    } else {
        lastEndReached
    }
    // 后台刷新期间，列表内容和已经确认的页尾提示一起保留。
    SideEffect { lastEndReached = endReached }

    val accessError = listOf(refresh, append, loadState.prepend).filterIsInstance<LoadState.Error>()
        .mapNotNull { it.error as? ApiException }
        .firstOrNull { it.code in listOf(-101, 22115, 22118) }
    if (accessError != null) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            if (accessError.code == -101) {
                val navigation = LocalNavigation.currentOrThrow
                TextButton(onClick = { navigation.push(SharedScreen.Login) }) {
                    Text("请登录后查看${listName}列表")
                }
            } else {
                Text("对方已设置隐私，${listName}列表不可见", color = TipColor)
            }
        }
        return
    }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(
            count = lazyPagingItems.itemCount,
            key = lazyPagingItems.itemKey {
                it.user.mid
            }
        ) { index ->
            val relationUser = lazyPagingItems[index] ?: return@items
            UserItemUI(vm, relationUser, canManageTags)
        }
        when (refresh) {
            is LoadState.Loading -> if (!hasItems) {
                item {
                    Box(Modifier.fillParentMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingIndicator()
                    }
                }
            }

            is LoadState.Error -> item {
                TextButton(onClick = lazyPagingItems::retry, modifier = Modifier.fillMaxWidth()) {
                    Text(refresh.error.listLoadMessage("${listName}列表加载失败，点击重试"))
                }
            }

            is LoadState.NotLoading -> if (!hasItems) {
                item {
                    Text(
                        text = "暂无$listName",
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        textAlign = TextAlign.Center,
                        color = TipColor
                    )
                }
            }

        }
        if (hasItems) {
            when {
                endReached -> item(key = "relationListEnd") {
                    Text(
                        text = "没有更多可查看的${listName}了",
                        modifier = Modifier.fillMaxWidth().padding(10.dp),
                        textAlign = TextAlign.Center,
                        color = TipColor,
                    )
                }

                refresh !is LoadState.NotLoading -> Unit

                append is LoadState.Loading -> item {
                    Box(
                        Modifier.fillMaxWidth().padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(Modifier.size(24.dp))
                    }
                }

                append is LoadState.Error -> item {
                    TextButton(
                        onClick = lazyPagingItems::retry,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(append.error.listLoadMessage("加载失败，点击重试"))
                    }
                }

            }
        }
    }
}

private fun Throwable.listLoadMessage(fallback: String): String {
    if (this is ApiException && code == -352) return "请求被拦截，请稍后重试"
    return message ?: fallback
}

@Composable
private fun UserItemUI(
    vm: FollowListViewModel,
    item: RelationListUser,
    canManageTags: Boolean,
) {
    val navigation = LocalNavigation.currentOrThrow
    val user = item.user
    val isSelf = user.mid == LoginMapper.getMid()
    LaunchedEffect(item) { vm.resolveMissingRelation(item) }
    val attribute = item.attribute
    val showFollowButton = !isSelf && attribute in listOf(null, 0, 2, 6)
    val isActionRunning = vm.isRelationActionRunning(user.mid)
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .height(70.dp)
            .combinedClickable(
                onClick = { navigation.push(SharedScreen.UserProfile(user.mid)) },
                onLongClick = if (canManageTags) ({
                    vm.showSettingTagsDialog(user.mid)
                }) else null,
            ).padding(10.dp)
    ) {
        val (avatar, text, followBtn, actionLoading) = createRefs()
        AsyncImage(
            model = user.face,
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
                if (showFollowButton) end.linkTo(followBtn.start, 10.dp)
                else end.linkTo(parent.end)
                width = Dimension.fillToConstraints
            },
            verticalArrangement = Arrangement.spacedBy((-2).dp)
        ) {
            Text(
                text = user.uname,
                color = TextColor,
                fontSize = 15.sp
            )
            Text(
                text = user.sign,
                color = TipColor,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }

        if (isActionRunning) {
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
        val buttonPosition = Modifier.constrainAs(followBtn) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            end.linkTo(parent.end)
        }
        if (item.needsRelationLookup && attribute == null) {
            Spacer(buttonPosition.size(85.dp, 30.dp))
        } else if (showFollowButton) {
            ButtonByAttribute(
                modifier = buttonPosition.isInvisible(isActionRunning),
                attribute = attribute,
                mid = user.mid,
                vm = vm,
            ) {
                item.needsRelationLookup = false
                item.attribute = it
            }
        }
    }
}


@Composable
private fun ButtonByAttribute(
    modifier: Modifier,
    attribute: Int?,
    mid: Long,
    vm: FollowListViewModel,
    onChange: (newAttribute: Int?) -> Unit,
) {
    when (attribute) {
        null -> TextButton(
            onClick = { vm.retryRelation(mid, onChange) },
            modifier = modifier,
        ) {
            Text("重试关注状态")
        }

        0 ->
            OutlinedButton(
                onClick = { vm.addFollow(mid, onChange) },
                contentPadding = PaddingValues(0.dp),
                modifier = modifier.size(85.dp, 30.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                Text(text = "关注")
            }

        2, 6 ->
            FilledTonalButton(
                onClick = { vm.cancelFollow(mid, onChange) },
                contentPadding = PaddingValues(0.dp),
                modifier = modifier.size(85.dp, 30.dp),
            ) {
                Icon(imageVector = Icons.Outlined.Menu, contentDescription = "Menu")
                Text(text = if (attribute == 2) "已关注" else "已互粉")
            }
    }
}
