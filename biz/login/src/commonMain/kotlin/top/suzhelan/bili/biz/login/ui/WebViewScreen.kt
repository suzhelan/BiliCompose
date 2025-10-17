package top.suzhelan.bili.biz.login.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.multiplatform.webview.cookie.Cookie
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import top.suzhelan.bili.api.config.commonHeaders
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.CommonComposeUI
import top.suzhelan.bili.shared.common.ui.DefaultViewModel
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun WebViewScreen(
    url: String,
) {
    //从html文件/字符串渲染
    val state = rememberWebViewState(
        url = url,
        additionalHttpHeaders = commonHeaders.apply {
            put("Cookie", LoginMapper.getCookie())
        }
    )
    val navigation = LocalNavigation.currentOrThrow

    LaunchedEffect(state.loadingState){
        if (state.loadingState is LoadingState.Finished) {
            val cookieInfo = LoginMapper.getUniversalLoginInfo().cookieInfo
            cookieInfo.domains.forEachIndexed {
                index, url ->
                val cooke = cookieInfo.cookies[index]
                val cookie = Cookie(
                    name = cooke.name,
                    value = cooke.value,
                    expiresDate = cooke.expires.toLong(),
                )
                state.cookieManager.setCookie(
                    url,
                    cookie
                )
            }
        }
    }
    CommonComposeUI<DefaultViewModel>(
        viewModel = DefaultViewModel(),
        topBar = {
            TitleUI(state.pageTitle ?: "") {
                navigation.pop()
            }
        },
        content = {
            WebView(
                state = state,
                modifier = Modifier.fillMaxSize()
            )
        }
    )
}