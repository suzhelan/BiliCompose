package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.sacz.bili.api.Response
import top.sacz.bili.api.ext.apiCall

import top.sacz.bili.biz.login.api.SmsLoginApi
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.model.SmsLoginToken


class SmsLoginViewModel : ViewModel() {
    private val smsLoginApi = SmsLoginApi()
    private val _countryList = MutableStateFlow<Response<CountryList>>(Response.Loading)
    val countryList = _countryList.asStateFlow()
    fun getCountryCode() = viewModelScope.launch {
        _countryList.value = apiCall {
            smsLoginApi.getCountryCode()
        }
    }

    private val _sendSmsResult = MutableStateFlow<Response<SmsLoginToken>>(Response.Loading)
    val sendSmsResult = _sendSmsResult.asStateFlow()
    fun sendSms(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
        recaptchaToken: String, // 登录 API token
        geeChallenge: String, // 极验 challenge
        geeValidate: String, // 极验 result
        geeSeccode: String, // 极验 result +'|jordan'
    ) = viewModelScope.launch {
        //进行发送验证码
        _sendSmsResult.value = apiCall {
            smsLoginApi.sendSms(
                cid,
                tel,
                recaptchaToken,
                geeChallenge,
                geeValidate,
                geeSeccode
            )
        }
        if (_sendSmsResult.value is Response.Success) {
            //发送成功后，进行倒计时
            startCountdown()
        }
    }

    private val _sendCountdown = MutableStateFlow(0)
    val sendCountdown = _sendCountdown.asStateFlow()
    private suspend fun startCountdown() = withContext(Dispatchers.Default) {
        //进行简单的倒计时
        for (i in 60 downTo 0) {
            _sendCountdown.value = i
            delay(1000)
        }
    }
}