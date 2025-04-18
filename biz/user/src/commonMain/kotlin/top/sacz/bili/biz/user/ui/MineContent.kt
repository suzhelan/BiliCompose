package top.sacz.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.sacz.bili.biz.login.config.LoginMapper
import top.sacz.bili.biz.login.ui.NotLoggedInContent
import top.sacz.bili.biz.user.viewmodel.MineViewModel

/**
 * 我的页
 */
@Composable
fun MineContent() {
    //判断是否登录 如果没有登录 显示登录按钮
    val isLogin by remember { LoginMapper.isLoginState }
    if (isLogin) {
        UserProfile()
    } else {
        NotLoggedInContent()
    }
}

/**
 * 用户信息
 */
@Composable
private fun UserProfile(mineViewModel: MineViewModel = viewModel()) {
    LaunchedEffect(Unit) {
        mineViewModel.updateMine()
    }
    val hasUserInfo by remember {
        mineViewModel.hasUserInfo
    }
    if (!hasUserInfo) return
    val accountInfo by mineViewModel.userInfo.collectAsState()
    ConstraintLayout(modifier = Modifier.fillMaxWidth()) {
        val (avatar, nickname, level) = createRefs()
        //头像
        AsyncImage(
            model = accountInfo?.face,
            contentDescription = null,
            modifier = Modifier
                .constrainAs(avatar) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    bottom.linkTo(parent.bottom)
                }
                .fillMaxWidth(0.3f)
        )
        //名字
        Text(text = accountInfo?.name ?: "", modifier = Modifier.constrainAs(nickname) {
            top.linkTo(avatar.top)
            start.linkTo(avatar.end)
        })
        //等级
        Text(
            text = "Lv${accountInfo?.level}",
            fontSize = 8.sp,
            modifier = Modifier.background(
                shape = RoundedCornerShape(2.dp),
                color = MaterialTheme.colorScheme.secondaryContainer
            ).padding(start = 1.dp, end = 1.dp, top = 1.dp, bottom = 1.dp).constrainAs(level) {
                top.linkTo(nickname.top)
                start.linkTo(nickname.end)
                bottom.linkTo(nickname.bottom)
            }
        )
    }
}
