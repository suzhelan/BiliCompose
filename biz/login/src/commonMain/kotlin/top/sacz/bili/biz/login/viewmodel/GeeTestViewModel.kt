package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.multiplatform.webview.web.WebViewNavigator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.Response
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.login.api.GeeTestApi
import top.sacz.bili.biz.login.model.Captcha

class GeeTestViewModel : ViewModel() {
    private val _captcha = MutableStateFlow<Response<Captcha>>(Response.Loading)
    val captcha = _captcha.asStateFlow()

    fun getGeeTestCaptcha() = viewModelScope.launch {
        _captcha.value = Response.Loading
        _captcha.value = apiCall {
            GeeTestApi().captcha()
        }
    }

    fun startVerify(navigator: WebViewNavigator, gt: String, challenge: String) {
        val script = "startVerify('$gt','$challenge')"
        println(script)
        navigator.evaluateJavaScript(script) { returnMessage ->
            println("jsResult : $returnMessage")
        }
    }

}