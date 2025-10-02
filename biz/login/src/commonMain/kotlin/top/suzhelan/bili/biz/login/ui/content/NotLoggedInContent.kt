package top.suzhelan.bili.biz.login.ui.content

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import bilicompose.biz.login.generated.resources.Res
import bilicompose.biz.login.generated.resources.content_to_login

import org.jetbrains.compose.resources.stringResource
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


/**
 * 显示没登录的按钮
 */
@Composable
fun NotLoggedInContent() {
    //获取最近的导航
    val navigator = LocalNavigation.currentOrThrow
    //创建登录屏幕
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        FilledTonalButton(
            onClick = {
                //跳转到登录屏幕
                navigator.push(SharedScreen.Login)
            }
        ) {
            Text(stringResource(Res.string.content_to_login))
        }
    }
}
