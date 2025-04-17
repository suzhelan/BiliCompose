package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import bilicompose.biz.login.generated.resources.Res
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import org.jetbrains.compose.resources.ExperimentalResourceApi
import top.sacz.bili.api.Response
import top.sacz.bili.biz.login.js.StatusJsMessageHandler
import top.sacz.bili.biz.login.model.Captcha
import top.sacz.bili.biz.login.model.VerifyResult

/**
 * 进行行为验证的Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehavioralValidationDialog(
    geetest: Response<Captcha>,
    verifyCallback: (VerifyResult) -> Unit,
    visible: Boolean,
    onDismiss: () -> Unit
) {
    if (!visible) return
    BasicAlertDialog(onDismissRequest = {
        onDismiss()
    }) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .fillMaxHeight(0.5f)
        ) {
            BehavioralValidation(geetest, verifyCallback)
        }
    }
}

@OptIn(ExperimentalResourceApi::class)
@Composable
fun BehavioralValidation(
    response: Response<Captcha>,
    verifyCallback: (VerifyResult) -> Unit,
) {
    val htmlDataState by produceState(initialValue = "await") {
        value = withContext(Dispatchers.IO) {
            return@withContext Res.readBytes("files/geetest-lite.html").decodeToString()
        }
    }
    //从html文件/字符串渲染
    val webViewState = rememberWebViewStateWithHTMLData(htmlDataState)
    //导航控制 可以调用js方法 以及页面的回退等常规操作
    val navigator = rememberWebViewNavigator()
    //监听js调用native
    val jsBridge = rememberWebViewJsBridge()
    LaunchedEffect(jsBridge) {
        val handler = StatusJsMessageHandler(verifyCallback)
        jsBridge.register(handler)
    }
    // 新增状态锁
    var hasExecuted by remember { mutableStateOf(false) }
    LaunchedEffect(response, webViewState.loadingState) {
        if (response is Response.Success
            && webViewState.loadingState is LoadingState.Finished
            && !hasExecuted) {
            val gt = response.data.geetest.gt
            val challenge = response.data.geetest.challenge
            navigator.evaluateJavaScript("startVerify('$gt','$challenge')") {
                // 执行完成后解锁（根据业务需求决定是否需要）
                // hasExecuted = false
            }
            hasExecuted = true // 标记已执行
        }
        // 当依赖项失效时重置状态
        if (response !is Response.Success) {
            hasExecuted = false
        }
    }

    Text(
        text = "${webViewState.pageTitle ?: ""} ${webViewState.loadingState}",
        modifier = Modifier.fillMaxWidth().padding(
            top = 10.dp
        ).wrapContentWidth(align = Alignment.CenterHorizontally)
    )
    //请求到了 进行展示webview以进行验证
    WebView(
        navigator = navigator,
        webViewJsBridge = jsBridge,
        state = webViewState,
        modifier = Modifier.fillMaxSize()
    )
}
