package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBackIosNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.ui.profile.FavouritePreviewCard
import top.suzhelan.bili.biz.user.ui.profile.ProfileVideoPreView
import top.suzhelan.bili.biz.user.ui.profile.VideoPreViewCard
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
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 用户详情页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileScreen(mid: Long) {
    val vm: UserProfileViewModel = viewModel {
        UserProfileViewModel()
    }

    val userSpaceRes by vm.userSpace.collectAsStateWithLifecycle(initialValue = BiliResponse.Loading)
    LaunchedEffect(mid) {
        vm.getUserSpace(mid)
    }
    when (userSpaceRes) {
        is BiliResponse.Error -> {
            //错误处理
            Text(
                text = "加载失败:\n\n" + (userSpaceRes as BiliResponse.Error).cause,
                color = ErrorColor.error,
                modifier = Modifier.fillMaxSize(),
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
            UserProfile(userSpace)
        }

        else -> {
            //其他情况
        }
    }

}

@Composable
private fun UserProfile(userSpace: UserSpace) =

    ConstraintLayout(
        modifier = Modifier.fillMaxWidth()
            .verticalScroll(rememberScrollState())
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
        //返回图标 原型黑色半透明背景
        Icon(
            imageVector = Icons.Rounded.ArrowBackIosNew,
            contentDescription = "Back",
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
                .padding(horizontal = 10.dp, vertical = 20.dp)
                .constrainAs(header1) {
                    top.linkTo(cover.bottom, (-10).dp)
                }
        ) {
            //简介
            CardHeader(userSpace)
            //RowTab
            ContentTab(userSpace)
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
}

/**
 * 等级进度条信息
 */
@Composable
private fun LevelProgressIndicator(
    userSpace: UserSpace
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
private fun ContentTab(userSpace: UserSpace) {
    val navigator = LocalNavigation.currentOrThrow
    val scope = rememberCoroutineScope()
    val tabItems = listOf(
        "主页",
        "动态",
        "投稿",
        "收藏",
        "追番"
    )
    val pageState = rememberPagerState(
        initialPage = 0,
        pageCount = {
            tabItems.size
        }
    )
    //横向Tab
    PrimaryTabRow(
        selectedTabIndex = pageState.currentPage,
        modifier = Modifier.fillMaxWidth()
    ) {
        tabItems.forEachIndexed { index, item ->
            Tab(
                selected = index == 0,
                onClick = {
                    scope.launch {
                        pageState.animateScrollToPage(index)
                    }
                },
                text = {
                    Text(text = item)
                }
            )
        }
    }
    HorizontalPager(
        state = pageState,
        modifier = Modifier.fillMaxSize()
            .padding(
                top = 10.dp
            )
    ) {
        when (it) {
            0 -> {
                //主页
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    ProfileVideoPreView(
                        title = "视频 - ${userSpace.archive.count}条",
                        items = userSpace.archive.item.take(4),//最多只展示四条
                    ) { item ->
                        //视频卡片
                        VideoPreViewCard(
                            cover = item.cover,
                            title = item.title,
                            playCount = item.play,
                            duration = item.duration,
                            danmaku = item.danmaku,
                            onClick = {
                                //跳转视频详情
                                navigator.push(
                                    SharedScreen.VideoPlayer(
                                        aid = item.param.toLong(),
                                    )
                                )
                            }
                        )
                    }
                    if (userSpace.favourite2.count > 0) {
                        ProfileVideoPreView(
                            title = "收藏 - ${userSpace.favourite2.count}个",
                            items = userSpace.favourite2.item.take(4),
                        ) { item ->
                            //收藏卡片
                            FavouritePreviewCard(item)
                        }
                    }
                    //最近点赞的视频
                    if (userSpace.likeArchive.count > 0) {
                        ProfileVideoPreView(
                            title = "最近点赞 - ${userSpace.likeArchive.count}个",
                            items = userSpace.likeArchive.item.take(4),
                        ) { item ->
                            //最近点赞的视频卡片
                            VideoPreViewCard(
                                cover = item.cover,
                                title = item.title,
                                playCount = item.play,
                                duration = item.duration,
                                danmaku = item.danmaku,
                                onClick = {
                                    //跳转视频详情
                                    navigator.push(
                                        SharedScreen.VideoPlayer(
                                            aid = item.param.toLong(),
                                        )
                                    )
                                }
                            )
                        }
                    }
                }
            }
        }

    }
}