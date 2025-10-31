package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
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
import top.suzhelan.bili.shared.common.util.toStringCount

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
    CommonComposeUI(
        viewModel = vm,
        topBar = {
            if (userSpaceRes is BiliResponse.Success) {
                val userSpace = userSpaceRes.getOrThrow()
                UserProfile(userSpace)
            }
        }) { vm ->
        LazyColumn(
            modifier = Modifier.fillMaxWidth()
                .background(TipColor),
        ) {
            items(100) {
                Text(text = "Item $it")
            }
        }
    }

}

@Composable
private fun UserProfile(userSpace: UserSpace) =
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (cover, avatar, nickname, header1) = createRefs()
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
                .padding(start = 10.dp, top = 20.dp)
                .constrainAs(header1) {
                    top.linkTo(cover.bottom, (-10).dp)
                }
        ) {
            //简介
            CardHeader(userSpace)
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
        verticalAlignment = Alignment.CenterVertically
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