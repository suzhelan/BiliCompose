package top.suzhelan.bili.biz.login.js

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import top.suzhelan.bili.biz.login.model.VerifyResult

// 定义消息处理器
class StatusJsMessageHandler(val verifyCallback: (VerifyResult) -> Unit) : IJsMessageHandler {
    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        verifyCallback(Json.decodeFromString(VerifyResult.serializer(), message.params))
        CoroutineScope(Dispatchers.Main).launch {
            callback("ComposeResult:OK") // 回调给 JS
        }
    }

    override fun methodName() = "onStatus" // 定义 JS 调用的方法名

}

