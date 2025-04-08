package top.sacz.biz.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import bilicompose.biz.login.generated.resources.Res
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import org.jetbrains.compose.resources.ExperimentalResourceApi
import top.sacz.biz.login.js.GreetJsMessageHandler

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BehavioralValidation() {
    var htmlDataState by remember {
        mutableStateOf("init")
    }
    var jsResult by remember {
        mutableStateOf("wait click")
    }
    val navigator = rememberWebViewNavigator()
    val webViewState = rememberWebViewStateWithHTMLData(htmlDataState)
    val jsBridge = rememberWebViewJsBridge()

    LaunchedEffect(jsBridge) {
        jsBridge.register(GreetJsMessageHandler())
        htmlDataState = Res.readBytes("files/geetest.html").decodeToString()
    }

    Column(Modifier.fillMaxSize()) {
        val text = webViewState.let {
            "${it.pageTitle ?: ""} ${it.loadingState} ${it.lastLoadedUrl ?: ""}"
        }
        TextButton(onClick = {
            navigator.evaluateJavaScript("getFinalResult()") {
                jsResult = it
            }
        }) {
            Text(text = "jsResult : $jsResult")
        }
        Text(text)
        WebView(
            navigator = navigator,
            webViewJsBridge = jsBridge,
            state = webViewState,
            modifier = Modifier.fillMaxSize()
        )
    }
}