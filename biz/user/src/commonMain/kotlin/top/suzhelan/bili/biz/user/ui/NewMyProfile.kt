package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.PullToRefreshDefaults
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.viewmodel.MoreLikeViewModel
import top.suzhelan.bili.biz.user.viewmodel.UserProfileViewModel
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.icons.LevelIcons
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimaryContainer
import top.suzhelan.bili.shared.common.ui.theme.ColorSurface
import top.suzhelan.bili.shared.common.ui.theme.ErrorColor
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


/**
 * 用户详情页
 */
@Composable
fun NewUserProfileScreen(mid: Long, showBackButton: Boolean = true) {
    UserProfilePage(mid, showBackButton)
}

@Composable
private fun UserProfilePage(mid: Long, showBackButton: Boolean) {
    val vm = viewModel { UserProfileViewModel() }
    val moreLikeViewModel = viewModel { MoreLikeViewModel() }
    val userSpaceRes by vm.userSpace.collectAsStateWithLifecycle()
    val profileRefreshing by vm.isRefreshing.collectAsStateWithLifecycle()
    val refreshError by vm.refreshError.collectAsStateWithLifecycle()
    var likesRefreshing by remember { mutableStateOf(false) }
    var isPullRefreshing by remember { mutableStateOf(false) }
    val pullState = rememberPullToRefreshState()
    val snackbarHostState = remember { SnackbarHostState() }
    LifecycleResumeEffect(mid, vm, moreLikeViewModel) {
        vm.getUserSpace(mid)
        // 即使恢复时停留在“主页”Tab，先让已经读取过的点赞缓存失效。
        moreLikeViewModel.refresh(mid)
        onPauseOrDispose { }
    }
    LaunchedEffect(refreshError) {
        refreshError?.let { snackbarHostState.showSnackbar(it) }
    }
    LaunchedEffect(isPullRefreshing, profileRefreshing, likesRefreshing) {
        // 直接读取请求状态，避免手动刷新刚开始、Flow 尚未收集时提前结束指示器。
        if (!vm.isRefreshing.value && !likesRefreshing) isPullRefreshing = false
    }
    PullToRefreshBox(
        isRefreshing = isPullRefreshing,
        onRefresh = {
            isPullRefreshing = true
            vm.getUserSpace(mid)
            moreLikeViewModel.refresh(mid)
        },
        state = pullState,
        modifier = Modifier.fillMaxSize(),
        indicator = {
            PullToRefreshDefaults.Indicator(
                state = pullState,
                isRefreshing = isPullRefreshing,
                modifier = Modifier.align(Alignment.TopCenter).statusBarsPadding(),
            )
        },
    ) {
        when (userSpaceRes) {
            is BiliResponse.Error -> {
                //错误处理
                Text(
                    text = "加载失败:\n\n" + (userSpaceRes as BiliResponse.Error).cause,
                    color = ErrorColor.error,
                    modifier = Modifier.fillMaxSize().clickable { vm.getUserSpace(mid) },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            }

            is BiliResponse.Loading -> {
                //加载中
                LoadingIndicator()
            }

            is BiliResponse.Success -> {
                val userSpace = userSpaceRes.getOrThrow()
                key(userSpace.card.mid) {
                    UserProfile(
                        userSpace,
                        showBackButton,
                        moreLikeViewModel,
                        showLikesRefreshIndicator = !isPullRefreshing,
                    ) {
                        likesRefreshing = it
                    }
                }
            }

            else -> {
                //其他情况
            }
        }
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.BottomCenter))
    }
}

@Composable
private fun UserProfile(
    userSpace: UserSpace,
    showBackButton: Boolean,
    moreLikeViewModel: MoreLikeViewModel,
    showLikesRefreshIndicator: Boolean,
    onLikesRefreshingChanged: (Boolean) -> Unit,
) {
    val navigation = LocalNavigation.currentOrThrow
    val scope = rememberCoroutineScope()
    val tabItems = remember { listOf("主页", "动态", "收藏", "点赞") }
    val pagerState = rememberPagerState(pageCount = { tabItems.size })
    val listStates = tabItems.map { rememberLazyListState() }
    val scrollState = rememberSaveable(saver = ProfileScrollState.Saver) {
        ProfileScrollState()
    }
    val fadeStart = with(LocalDensity.current) { 100.dp.toPx() }
    val fadeEnd = with(LocalDensity.current) { 180.dp.toPx() }
    val topBarAlpha = remember(scrollState, fadeStart, fadeEnd) {
        derivedStateOf {
            ((scrollState.offset - fadeStart) / (fadeEnd - fadeStart)).coerceIn(0f, 1f)
        }
    }
    val showTopBar by remember(topBarAlpha) { derivedStateOf { topBarAlpha.value > 0f } }

    ProfileScrollLayout(
        state = scrollState,
        listState = listStates[pagerState.currentPage],
        modifier = Modifier.fillMaxSize().background(ColorSurface),
        header = { ProfileHeader(userSpace, showBackButton) },
        tabs = {
            ContentTab(tabItems, pagerState.currentPage) { index ->
                scope.launch { pagerState.animateScrollToPage(index) }
            }
        },
        topBar = {
            // 背景先覆盖状态栏区域，再为内容留出安全距离；整块顶部栏一起渐显。
            Box(
                Modifier.fillMaxWidth()
                    .graphicsLayer { alpha = topBarAlpha.value }
                    .background(ColorSurface)
                    .statusBarsPadding()
            ) {
                Box(Modifier.fillMaxWidth().height(64.dp)) {
                    if (showTopBar) {
                        SimpleTopBar(
                            userSpace = userSpace,
                            showBackButton = showBackButton,
                            onBack = { navigation.pop() },
                        )
                    }
                }
            }
        },
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
        ) { page ->
            if (page == 3) {
                MoreLikeVideoContent(
                    mid = userSpace.card.mid.toLong(),
                    viewModel = moreLikeViewModel,
                    listState = listStates[page],
                    isActive = pagerState.settledPage == page,
                    refreshOnResume = false,
                    enablePullToRefresh = false,
                    showRefreshIndicator = showLikesRefreshIndicator,
                    onRefreshingChanged = onLikesRefreshingChanged,
                )
            } else {
                LazyColumn(
                    state = listStates[page],
                    modifier = Modifier.fillMaxSize(),
                ) {
                    item {
                        Spacer(Modifier.fillParentMaxHeight())
                    }
                }
            }
        }
    }
}

@Composable
private fun SimpleTopBar(
    userSpace: UserSpace,
    showBackButton: Boolean,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(64.dp)
            .background(ColorSurface)
            .padding(horizontal = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (showBackButton) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Rounded.ArrowBackIosNew,
                    contentDescription = "返回",
                    tint = TextColor,
                )
            }
        }
        AsyncImage(
            model = userSpace.card.face,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(32.dp).clip(CircleShape),
        )
        Text(
            text = userSpace.card.name,
            color = TextColor,
            fontSize = 18.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f).padding(start = 10.dp),
        )
    }
}

@Composable
private fun ProfileHeader(
    userSpace: UserSpace,
    showBackButton: Boolean,
) {
    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
    ) {
        val navigation = LocalNavigation.currentOrThrow
        val (cover, avatar, nickname, header1, back) = createRefs()
        //典型的资料卡布局
        //先放一个封面图当底图
        AsyncImage(
            model = userSpace.images.imgUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth()
                .height(200.dp)
                .constrainAs(cover) {
                    top.linkTo(parent.top)
                    centerHorizontallyTo(parent)
                }
        )
        // 返回图标与顶部栏使用同一个入口开关。
        if (showBackButton) {
            Icon(
                imageVector = Icons.Rounded.ArrowBackIosNew,
                contentDescription = "返回",
                tint = Color.White,
                modifier = Modifier
                    .size(45.dp)
                    .padding(5.dp)
                    .clip(CircleShape)
                    .background(Color.Black.copy(alpha = 0.5f))
                    .padding(5.dp)
                    .constrainAs(back) {
                        top.linkTo(parent.top, 50.dp)
                        start.linkTo(parent.start, 20.dp)
                    }.clickable {
                        navigation.pop()
                    }
            )
        }
        //头像位于封面左下角
        AsyncImage(
            model = userSpace.card.face,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, ColorSurface, CircleShape) // 添加白色圆形描边
                .constrainAs(avatar) {
                    bottom.linkTo(cover.bottom, 15.dp)
                    start.linkTo(parent.start, 15.dp)
                }
        )
        //名字在头像右侧居中
        Column(
            modifier = Modifier.background(
                Color.Black.copy(alpha = 0.4f),
                RoundedCornerShape(15.dp)
            ).padding(
                start = 10.dp,
                end = 10.dp,
            ).constrainAs(nickname) {
                start.linkTo(avatar.end, 10.dp)
                top.linkTo(avatar.top)
                bottom.linkTo(avatar.bottom)
            },
        ) {
            NicknameUI(userSpace)
            LevelProgressIndicator(userSpace)
        }

        //上部分圆角白色背景
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    ColorSurface,
                    RoundedCornerShape(topStart = 15.dp, topEnd = 15.dp)
                )
                .padding(start = 10.dp, top = 20.dp, end = 10.dp)
                .constrainAs(header1) {
                    top.linkTo(cover.bottom, (-10).dp)
                }
        ) {
            //简介
            CardHeader(userSpace)
        }

    }

}

@Composable
private fun ColumnScope.NicknameUI(userSpace: UserSpace) = Row(
    verticalAlignment = Alignment.CenterVertically
) {
    //名字
    val isVip = userSpace.card.vip.vipStatus == 1
    //会员级别
    val vipLevelStr = if (isVip) {
        when (userSpace.card.vip.vipType) {
            1 -> "月大会员"
            2 -> "年大会员"
            3 -> "3大会员"
            else -> "?"
        }
    } else {
        "没会员"
    }
    Text(
        text = userSpace.card.name,
        fontSize = 20.sp,
        //如果是会员的话展示会员色
        color = if (isVip) {
            ColorPrimary
        } else {
            Color.White
        },
    )
    //等级图标
    Image(
        painter = painterResource(LevelIcons.levelIconMap.getValue(userSpace.card.levelInfo.currentLevel)),
        contentDescription = null,
        modifier = Modifier.height(35.dp)
            .aspectRatio(1f)
            .padding(start = 10.dp)

    )
    //大会员图标 我们自己画就行
    Text(
        text = vipLevelStr,
        fontSize = 13.sp,
        color = if (isVip) {
            ColorPrimary
        } else {
            Color.White
        },
        modifier = Modifier.padding(start = 10.dp)
            .background(
                if (isVip) ColorPrimaryContainer else TipColor,
                RoundedCornerShape(10.dp)
            )
            .padding(horizontal = 4.dp, vertical = 0.dp)
    )
}

/**
 * 信息卡
 */
@Composable
private fun ColumnScope.CardHeader(userSpace: UserSpace) {
    //统计信息 粉丝 关注 获赞
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //获赞数
        Text(
            text = userSpace.card.likes.likeNum.toStringCount(),
            fontSize = 20.sp,
            color = TextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = "获赞",
            modifier = Modifier.padding(start = 5.dp),
            color = TipColor
        )
        //关注数
        Text(
            text = userSpace.card.attention.toStringCount(),
            fontSize = 20.sp,
            color = TextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = "关注",
            modifier = Modifier.padding(start = 5.dp),
            color = TipColor
        )
        //粉丝数
        Text(
            text = userSpace.card.fans.toStringCount(),
            fontSize = 20.sp,
            color = TextColor,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 10.dp)
        )
        Text(
            text = "粉丝",
            modifier = Modifier.padding(start = 5.dp),
            color = TipColor
        )
    }

    //简介
    Text(
        text = userSpace.card.sign,
        color = TextColor,
        modifier = Modifier.padding(start = 10.dp, top = 15.dp)
    )
    Row(
        verticalAlignment = Alignment.CenterVertically,
    ) {
        //ip归属地,添加学校信息等uri
        for (ip in userSpace.card.spaceTag) {
            AsyncImage(
                model = ip.icon,
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.height(12.dp)
                    .padding(start = 10.dp)
            )
            Text(
                text = ip.title,
                fontSize = 10.sp,
                modifier = Modifier.padding(start = 5.dp),
                color = TipColor
            )
        }
    }
    //操作栏部分 可以在这里放一些操作按钮

}

/**
 * 等级进度条信息
 */
@Composable
private fun LevelProgressIndicator(
    userSpace: UserSpace,
) {
    if (userSpace.card.levelInfo.nextExp != null) {
        //填充阴影
        Column(modifier = Modifier.padding(bottom = 5.dp)) {
            val levelInfo = userSpace.card.levelInfo
            val percent = levelInfo.currentExp.toFloat() / levelInfo.nextExp.toFloat()
            LinearProgressIndicator(
                modifier = Modifier
                    .width(120.dp)
                    .height(5.dp),
                progress = {
                    percent
                },
                color = ColorPrimaryContainer,
                trackColor = Color.White.copy(alpha = 0.8f),
            )
            Text(
                text = "Exp:${levelInfo.currentExp}/${levelInfo.nextExp}",
                fontSize = 12.sp,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun ContentTab(
    tabItems: List<String>,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
) {
    //横向Tab
    PrimaryTabRow(
        selectedTabIndex = selectedTabIndex,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabItems.forEachIndexed { index, item ->
            Tab(
                selected = selectedTabIndex == index,
                onClick = { onTabSelected(index) },
                text = {
                    Text(text = item)
                }
            )
        }
    }
}
