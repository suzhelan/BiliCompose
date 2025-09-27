package top.suzhelan.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.biz.login.api.SmsLoginApi
import top.suzhelan.bili.biz.login.model.CountryList
import top.suzhelan.bili.biz.login.model.SendSmsLoginCodeResult
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.auth.entity.LoginResult
import top.suzhelan.bili.shared.common.logger.LogUtils


class SmsLoginViewModel : ViewModel() {
    private val smsLoginApi = SmsLoginApi()
    private val _countryList = MutableStateFlow<BiliResponse<CountryList>>(BiliResponse.Loading)
    val countryList = _countryList.asStateFlow()
    fun getCountryCode() = viewModelScope.launch {
        _countryList.value = apiCall {
            smsLoginApi.getCountryCode()
        }
    }

    private val _sendSmsResult =
        MutableStateFlow<BiliResponse<SendSmsLoginCodeResult>>(BiliResponse.Loading)
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
        if (_sendSmsResult.value is BiliResponse.Success) {
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

    private val _loginResult = MutableStateFlow<BiliResponse<LoginResult>>(BiliResponse.Loading)
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
            LogUtils.d("登录参数 cid $cid, tel $tel, code $code, captchaKey $captchaKey")
            val loginResultRes = smsLoginApi.smsLogin(
                cid,
                tel,
                code,
                captchaKey
            )
            if (loginResultRes.code == 0) {
                _loginResult.value = loginResultRes
            } else {
                _loginResult.value = BiliResponse.Error(
                    loginResultRes.code,
                    loginResultRes.message
                )
            }
            LogUtils.d("登录结果 ${_loginResult.value}")
            //保存登录凭证
            if (_loginResult.value is BiliResponse.Success) {
                LoginMapper.clear()
                LoginMapper.setAppLoginInfo(loginResultRes.data)
            }
        } catch (e: Exception) {
            _loginResult.value = BiliResponse.Error(-1, e.message ?: "未知错误")
        }

    }
}