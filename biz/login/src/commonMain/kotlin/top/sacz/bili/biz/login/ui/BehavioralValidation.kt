package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.produceState
import androidx.compose.ui.Modifier
import bilicompose.biz.login.generated.resources.Res
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import org.jetbrains.compose.resources.ExperimentalResourceApi
import top.sacz.bili.api.Response
import top.sacz.bili.biz.login.js.StatusJsMessageHandler
import top.sacz.bili.biz.login.model.Captcha

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BehavioralValidation(
    response: Response<Captcha>,
    verifyCallback: (String) -> Unit,
) {
    val htmlDataState by produceState(initialValue = "await") {
        value = Res.readBytes("files/geetest-lite.html").decodeToString()
    }
    //从html文件/字符串渲染
    val webViewState = rememberWebViewStateWithHTMLData(htmlDataState)
    //导航控制 可以调用js方法 以及页面的回退等常规操作
    val navigator = rememberWebViewNavigator()
    //监听js调用native
    val jsBridge = rememberWebViewJsBridge()
    LaunchedEffect(jsBridge) {
        //监听js调用native
        jsBridge.register(StatusJsMessageHandler(verifyCallback))
    }
    Text("${webViewState.pageTitle ?: ""} ${webViewState.loadingState} ${webViewState.lastLoadedUrl ?: ""}")
    //请求到了 进行展示webview以进行验证
    WebView(
        navigator = navigator,
        webViewJsBridge = jsBridge,
        state = webViewState,
        modifier = Modifier.fillMaxSize()
    )
    if (htmlDataState != "await" && response is Response.Success) {
        val gt = response.data.geetest.gt
        val challenge = response.data.geetest.challenge
        val script = "startVerify('$gt','$challenge')"
        navigator.evaluateJavaScript(script) { returnMessage ->
            verifyCallback(returnMessage)
        }
    }
}