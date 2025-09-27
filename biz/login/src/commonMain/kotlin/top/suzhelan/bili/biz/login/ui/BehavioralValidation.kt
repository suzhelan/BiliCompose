package top.suzhelan.bili.biz.login.ui

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.jsbridge.rememberWebViewJsBridge
import com.multiplatform.webview.web.LoadingState
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewNavigator
import com.multiplatform.webview.web.rememberWebViewStateWithHTMLData
import org.jetbrains.compose.resources.ExperimentalResourceApi
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.login.js.StatusJsMessageHandler
import top.suzhelan.bili.biz.login.model.Captcha
import top.suzhelan.bili.biz.login.model.VerifyResult
import top.suzhelan.bili.biz.login.viewmodel.BehavioralViewModel

/**
 * 进行行为验证的Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehavioralValidationDialog(
    geetest: BiliResponse<Captcha>,
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
    response: BiliResponse<Captcha>,
    verifyCallback: (VerifyResult) -> Unit,
    vm : BehavioralViewModel = viewModel { BehavioralViewModel() } ,
) {
    LaunchedEffect(Unit){
        vm.loadHtmlData()
    }
    val htmlDataState by vm.htmlData.collectAsStateWithLifecycle()
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

    LaunchedEffect(response, webViewState.loadingState) {
        if (response is BiliResponse.Success && webViewState.loadingState is LoadingState.Finished) {
            val gt = response.data.geetest.gt
            val challenge = response.data.geetest.challenge
            navigator.evaluateJavaScript("startVerify('$gt','$challenge')")
        }
    }

    Text(
        text = "${webViewState.pageTitle ?: ""} ${webViewState.loadingState}",
        modifier = Modifier.fillMaxWidth().padding(
            top = 10.dp
        ).wrapContentWidth(align = Alignment.CenterHorizontally)
    )
    if (htmlDataState.length > 64) {
        //请求到了 进行展示webview以进行验证
        WebView(
            navigator = navigator,
            webViewJsBridge = jsBridge,
            state = webViewState,
            modifier = Modifier.fillMaxSize()
        )
    }
}
