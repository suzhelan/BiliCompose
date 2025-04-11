package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import bilicompose.biz.login.generated.resources.Res
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import org.jetbrains.compose.resources.ExperimentalResourceApi
import top.sacz.bili.api.Response
import top.sacz.bili.biz.login.js.StatusJsMessageHandler
import top.sacz.bili.biz.login.model.Captcha
import top.sacz.bili.biz.login.viewmodel.GeeTestViewModel

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BehavioralValidation(geeTestViewModel: GeeTestViewModel = viewModel()) {
    val captcha by geeTestViewModel.captcha.collectAsState()
    val htmlDataState by produceState(initialValue = "await") {
        value = Res.readBytes("files/geetest-lite.html").decodeToString()
    }
    //从html文件/字符串渲染
    val webViewState = rememberWebViewStateWithHTMLData(htmlDataState)
    webViewState.webSettings.androidWebSettings
    //导航控制 可以调用js方法 以及页面的回退等常规操作
    val navigator = rememberWebViewNavigator()

    //监听js调用native
    val jsBridge = rememberWebViewJsBridge()
    LaunchedEffect(jsBridge) {
        //监听js调用native
        jsBridge.register(StatusJsMessageHandler())
    }

    Column(Modifier.fillMaxSize()) {
        val text = webViewState.let {
            "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
        }
        Text(text)
        //开始验证
        FilledTonalButton(onClick = {
            //获取gt和challenge
            geeTestViewModel.getGeeTestCaptcha()
        }) {
            Text("开始人机验证")
        }
        //请求到了 进行展示webview以进行验证
        if (captcha is Response.Success<Captcha>) {
            WebView(
                navigator = navigator,
                webViewJsBridge = jsBridge,
                state = webViewState,
                modifier = Modifier.fillMaxSize()
            )
            val successCaptcha = captcha as Response.Success<Captcha>
            geeTestViewModel.startVerify(
                navigator = navigator,
                gt = successCaptcha.data.geetest.gt,
                challenge = successCaptcha.data.geetest.challenge
            )
        }
    }
}