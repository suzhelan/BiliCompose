package top.sacz.biz.login.js

import com.multiplatform.webview.jsbridge.IJsMessageHandler
import com.multiplatform.webview.jsbridge.JsMessage
import com.multiplatform.webview.web.WebViewNavigator
import top.sacz.bili.shared.common.logger.Logger

// 定义消息处理器
class GreetJsMessageHandler : IJsMessageHandler {
    override fun handle(
        message: JsMessage,
        navigator: WebViewNavigator?,
        callback: (String) -> Unit
    ) {
        // 处理 JS 传递的参数
        val param = "${message.methodName}: ${message.params}"
        Logger.d("JS调用Compose",param)
        val response = "OK The Compose send"
        callback(response) // 回调给 JS
    }

    override fun methodName() = "getUserInfo" // 定义 JS 调用的方法名

}

