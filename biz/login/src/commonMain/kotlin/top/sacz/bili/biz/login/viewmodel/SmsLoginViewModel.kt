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
import top.sacz.bili.biz.login.config.LoginMapper
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.model.SendSmsLoginCodeResult
import top.sacz.bili.biz.login.model.SmsLoginResult
import top.sacz.bili.shared.common.logger.Logger


class SmsLoginViewModel : ViewModel() {
    private val smsLoginApi = SmsLoginApi()
    private val _countryList = MutableStateFlow<Response<CountryList>>(Response.Loading)
    val countryList = _countryList.asStateFlow()
    fun getCountryCode() = viewModelScope.launch {
        _countryList.value = apiCall {
            smsLoginApi.getCountryCode()
        }
    }

    private val _sendSmsResult =
        MutableStateFlow<Response<SendSmsLoginCodeResult>>(Response.Loading)
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

    private val _loginResult = MutableStateFlow<Response<SmsLoginResult>>(Response.Loading)
    val loginResult = _loginResult.asStateFlow()

    /**
     * 最后一步 进行登录并保存登录凭证
     */
    fun login(
        cid: String, // 国际冠字码
        tel: String, // 手机号码
        code: String,
        captchaKey: String,
    ) = viewModelScope.launch {
        try {
            Logger.d("登录参数 cid $cid, tel $tel, code $code, captchaKey $captchaKey")
            val loginResultRes = smsLoginApi.smsLogin(
                cid,
                tel,
                code,
                captchaKey
            )
            if (loginResultRes.code == 0) {
                _loginResult.value = loginResultRes
            } else {
                _loginResult.value = Response.Error(
                    loginResultRes.code,
                    loginResultRes.message
                )
            }
            Logger.d("登录结果 ${_loginResult.value}")
            //保存登录凭证
            if (_loginResult.value is Response.Success) {
                LoginMapper.clear()
                LoginMapper.setLoginInfo(loginResultRes.data)
            }
        } catch (e: Exception) {
            _loginResult.value = Response.Error(-1, e.message ?: "未知错误")
        }

    }
}