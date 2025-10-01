package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForwardIos
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bilicompose.biz.user.generated.resources.Res
import bilicompose.biz.user.generated.resources.b_coin
import bilicompose.biz.user.generated.resources.coin
import bilicompose.biz.user.generated.resources.dynamic
import bilicompose.biz.user.generated.resources.fans
import bilicompose.biz.user.generated.resources.follow
import bilicompose.biz.user.generated.resources.ic_lv0
import bilicompose.biz.user.generated.resources.ic_lv1
import bilicompose.biz.user.generated.resources.ic_lv2
import bilicompose.biz.user.generated.resources.ic_lv3
import bilicompose.biz.user.generated.resources.ic_lv4
import bilicompose.biz.user.generated.resources.ic_lv5
import bilicompose.biz.user.generated.resources.ic_lv6
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import top.suzhelan.bili.api.getOrThrow
import top.suzhelan.bili.api.isSuccess
import top.suzhelan.bili.biz.login.ui.NotLoggedInContent
import top.suzhelan.bili.biz.user.entity.mine.Mine
import top.suzhelan.bili.biz.user.viewmodel.MineViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimaryContainer
import top.suzhelan.bili.shared.common.ui.theme.DividingLineColor
import top.suzhelan.bili.shared.common.ui.theme.TipTextColor
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow



/**
 * 我的页
 */
@Composable
fun MineContent() {
    Scaffold { paddingValues ->
        //判断是否登录 如果没有登录 显示登录按钮
        val isLogin by remember { LoginMapper.isLoginState }
        if (isLogin) {
            UserProfile(modifier = Modifier.padding(paddingValues))
        } else {
            NotLoggedInContent()
        }
    }
}

/**
 * 用户信息
 */
@Composable
private fun UserProfile(modifier: Modifier = Modifier, mineViewModel: MineViewModel = viewModel { MineViewModel() }) {
    LaunchedEffect(Unit) {
        mineViewModel.updateMine()
    }
    val mineResponse by mineViewModel.mine.collectAsStateWithLifecycle()
    if (!mineResponse.isSuccess()) {
        return
    }
    val mine = mineResponse.getOrThrow()
    Column(modifier = modifier.fillMaxWidth().padding(top = 50.dp)) {
        HeaderUserCard(mine)
        Spacer(modifier = Modifier.height(20.dp))
        UserAmount(mine)
    }
}

/**
 * 头部卡片
 */
@Composable
private fun ColumnScope.HeaderUserCard(
    mine: Mine
) {
    val levelIconMap = mapOf(
        0 to Res.drawable.ic_lv0,
        1 to Res.drawable.ic_lv1,
        2 to Res.drawable.ic_lv2,
        3 to Res.drawable.ic_lv3,
        4 to Res.drawable.ic_lv4,
        5 to Res.drawable.ic_lv5,
        6 to Res.drawable.ic_lv6,
    )

    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (avatar, nickname, level, entry, identity, bCoin, coin) = createRefs()
        //头像
        AsyncImage(
            model = mine.face,
            contentDescription = null,
            modifier = Modifier
                .size(65.dp)
                .clip(RoundedCornerShape(50.dp))
                .constrainAs(avatar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start, 20.dp)
                    bottom.linkTo(parent.bottom)
                },
        )
        //名字
        Text(
            text = mine.name, modifier = Modifier
                .constrainAs(nickname) {
                    top.linkTo(avatar.top)
                    start.linkTo(avatar.end, 20.dp)
                }
        )
        //等级
        Image(
            painter = painterResource(levelIconMap[mine.level] ?: Res.drawable.ic_lv0),
            contentDescription = "lv${mine.level}",
            modifier = Modifier
                .height(15.dp)
                .padding(start = 10.dp)
                .constrainAs(level) {
                    top.linkTo(nickname.top)
                    bottom.linkTo(nickname.bottom)
                    start.linkTo(nickname.end)
                },
        )

        //进入空间
        Icon(
            imageVector = Icons.AutoMirrored.Outlined.ArrowForwardIos,
            contentDescription = null,
            tint = Color.Gray,
            modifier = Modifier
                .size(15.dp)
                .constrainAs(entry) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end, 20.dp)
                    bottom.linkTo(parent.bottom)
                },
        )

        Text(
            text = "正式会员",
            fontSize = 12.sp,
            color = ColorPrimaryContainer,
            //带边框
            modifier = Modifier
                .constrainAs(identity) {
                    top.linkTo(nickname.bottom)
                    start.linkTo(nickname.start)
                }
                .border(
                    width = 1.dp,
                    color = ColorPrimaryContainer,
                    shape = RoundedCornerShape(2.dp)
                )
                .padding(horizontal = 2.dp, vertical = 0.dp)
        )
        //b币
        Text(
            text = stringResource(Res.string.b_coin, mine.bcoin),
            fontSize = 12.sp,
            style = TextStyle(color = TipTextColor),
            modifier = Modifier.constrainAs(bCoin) {
                top.linkTo(identity.bottom, 5.dp)
                start.linkTo(identity.start)
            })

        //硬币
        Text(
            text = stringResource(Res.string.coin, mine.coin),
            fontSize = 12.sp,
            style = TextStyle(color = TipTextColor),
            modifier = Modifier.constrainAs(coin) {
                top.linkTo(bCoin.top)
                start.linkTo(bCoin.end, 15.dp)
            })
    }
}

/**
 * 数量
 * 动态 粉丝 关注
 */
@Composable
private fun ColumnScope.UserAmount(
    mine: Mine
) {
    val navigate = LocalNavigation.currentOrThrow
    // 聚合数据列表
    val data = mapOf(
        Res.string.dynamic to mine.dynamic,
        Res.string.follow to mine.following,
        Res.string.fans to mine.follower
    )

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        data.onEachIndexed { index, (amountTextRes, count) ->
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
                    .clickable {
                        when (amountTextRes) {
                            Res.string.dynamic -> {

                            }

                            Res.string.follow -> {
                                navigate.push(SharedScreen.FollowList)
                            }

                            Res.string.fans -> {

                            }
                        }
                    }
            ) {
                Text(
                    text = count.toString(),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = stringResource(amountTextRes),
                    fontSize = 12.sp,
                    color = TipTextColor
                )
            }
            //如果是最后一个的话 不添加分割线
            if (index != data.size - 1) {
                VerticalDivider(
                    modifier = Modifier.height(25.dp),
                    color = DividingLineColor,
                    thickness = 1.dp
                )
            }
        }
    }
}
