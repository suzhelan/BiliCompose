package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    // GeeTestViewModel.kt
    fun resetCaptcha() {
        _captcha.value = Response.Loading
    }

}