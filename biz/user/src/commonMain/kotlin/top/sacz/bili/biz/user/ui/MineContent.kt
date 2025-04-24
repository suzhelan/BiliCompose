package top.sacz.bili.biz.user.ui

import androidx.compose.foundation.Image
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import bilicompose.biz.user.generated.resources.Res
import bilicompose.biz.user.generated.resources.b_coin
import bilicompose.biz.user.generated.resources.coin
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

import top.sacz.bili.biz.login.ui.NotLoggedInContent
import top.sacz.bili.biz.user.entity.mine.Mine
import top.sacz.bili.biz.user.viewmodel.MineViewModel
import top.sacz.bili.shared.auth.config.LoginMapper

private val levelIconMap = mapOf(
    0 to Res.drawable.ic_lv0,
    1 to Res.drawable.ic_lv1,
    2 to Res.drawable.ic_lv2,
    3 to Res.drawable.ic_lv3,
    4 to Res.drawable.ic_lv4,
    5 to Res.drawable.ic_lv5,
    6 to Res.drawable.ic_lv6,
)

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
private fun UserProfile(modifier: Modifier = Modifier, mineViewModel: MineViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        mineViewModel.updateMine()
    }
    val mineOrNull by mineViewModel.mine.collectAsState()
    val mine: Mine = mineOrNull ?: return
    ConstraintLayout(modifier = modifier.fillMaxWidth().padding(top = 50.dp)) {
        val (avatar, nickname, level, entry, bCoin, coin) = createRefs()
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
            text = stringResource(Res.string.b_coin, mine.bcoin),
            fontSize = 12.sp,
            style = TextStyle(color = Color.Gray),
            modifier = Modifier.constrainAs(bCoin) {
                top.linkTo(nickname.bottom)
                start.linkTo(nickname.start)
            })

        Text(
            text = stringResource(Res.string.coin, mine.coin),
            fontSize = 12.sp,
            style = TextStyle(color = Color.Gray),
            modifier = Modifier.constrainAs(coin) {
                top.linkTo(bCoin.top)
                start.linkTo(bCoin.end, 15.dp)
            })
    }
}
