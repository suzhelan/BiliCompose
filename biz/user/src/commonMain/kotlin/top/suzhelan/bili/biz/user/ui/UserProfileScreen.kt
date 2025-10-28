package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.biz.user.entity.UserSpace
import top.suzhelan.bili.biz.user.viewmodel.UserProfileViewModel
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.icons.LevelIcons
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimaryContainer
import top.suzhelan.bili.shared.common.ui.theme.ColorSurface
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 用户详情页
 */
@Composable
fun UserProfileScreen(mid: Long) {
    LocalNavigation.currentOrThrow
    val vm: UserProfileViewModel = viewModel {
        UserProfileViewModel()
    }
    val userSpaceRes by vm.userSpace.collectAsStateWithLifecycle(initialValue = BiliResponse.Loading)
    LaunchedEffect(mid) {
        vm.getUserSpace(mid)
    }
    CommonComposeUI(viewModel = vm, topBar = {
        //大顶栏
        if (userSpaceRes is BiliResponse.Success) {
            val userSpace = userSpaceRes.getOrThrow()
            UserProfile(userSpace)
        }
    }) { vm ->
        Text(text = "用户详情页")
    }

}

@Composable
private fun UserProfile(userSpace: UserSpace) =
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (cover, avatar, header1) = createRefs()
        //典型的资料卡布局
        //先放一个封面图当底图
        AsyncImage(
            model = userSpace.images.imgUrl,
            contentDescription = null,
            modifier = Modifier.fillMaxWidth().constrainAs(cover) {
                top.linkTo(parent.top)
                centerHorizontallyTo(parent)
            }
        )
        //昵称和一些资料 我这里不一比一复刻哔哩了
        Row(modifier = Modifier.constrainAs(header1) {
            top.linkTo(cover.bottom)
            start.linkTo(avatar.end, 20.dp)
        }) {
            UserHeader(userSpace)
        }
        //头像穿插在封面之间
        AsyncImage(
            model = userSpace.card.face,
            contentDescription = null,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .border(2.dp, ColorSurface, CircleShape) // 添加白色圆形描边
                .constrainAs(avatar) {
                    top.linkTo(cover.bottom, (-30).dp)
                    start.linkTo(cover.start, 20.dp)
                }
        )


    }

/**
 * 不包含头像的那一片头部信息
 */
@Composable
private fun UserHeader(userSpace: UserSpace) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
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
                TextColor
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

}