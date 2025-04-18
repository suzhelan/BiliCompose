package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bilicompose.biz.login.generated.resources.Res
import bilicompose.biz.login.generated.resources.content_to_login
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.shared.navigation.SharedScreen


/**
 * 显示没登录的按钮
 */
@Composable
fun NotLoggedInContent() {
    //获取最近的导航
    val navigator = LocalNavigator.currentOrThrow
    //创建登录屏幕
    val login = rememberScreen(SharedScreen.Login)
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FilledTonalButton(
            onClick = {
                //跳转到登录屏幕
                navigator.push(login)
            }
        ) {
            Text(stringResource(Res.string.content_to_login))
        }
    }
}
