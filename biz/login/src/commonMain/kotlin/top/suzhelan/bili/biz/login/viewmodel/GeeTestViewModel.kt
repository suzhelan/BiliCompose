package top.suzhelan.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.api.ext.apiCall
import top.suzhelan.bili.biz.login.api.GeeTestApi
import top.suzhelan.bili.biz.login.api.SmsLoginApi
import top.suzhelan.bili.biz.login.model.Captcha
import top.suzhelan.bili.biz.login.model.Geetest
import top.suzhelan.bili.biz.login.model.Tencent
import top.suzhelan.bili.shared.common.logger.LogUtils

class GeeTestViewModel : ViewModel() {
    private val _captcha = MutableStateFlow<BiliResponse<Captcha>>(BiliResponse.Loading)
    val captcha = _captcha.asStateFlow()

    fun getGeeTestCaptcha(cid: String, tel: String) =
        viewModelScope.launch {
            _captcha.value = BiliResponse.Loading
            val smsLoginApi = SmsLoginApi()
            //先请求手机号专用的验证码
            val response = smsLoginApi.getCaptchaBySms(cid, tel)
            val captcha = response.data
            if (response.code != 0) {
                _captcha.value = BiliResponse.Error(response.code, response.message)
                return@launch
            }
            val recaptchaUrl = captcha.recaptchaUrl
            if (recaptchaUrl.isEmpty()) {
                //如果recaptchaUrl没有返回数据 那么就走默认的验证码
                LogUtils.d("recaptchaUrl is empty(走默认验证码,而不是手机号专属geetest)")
                _captcha.value = apiCall {
                    GeeTestApi().captcha()
                }
                return@launch
            }
            //然后解析所需参数
            val urlParams = Url(recaptchaUrl).parameters
            val paramMap = mutableMapOf<String, String>()
            urlParams.entries().forEach {
                paramMap[it.key] = it.value.first()
            }
            val ct = paramMap["ct"]
            val recaptchaToken = paramMap["recaptcha_token"]
            val geeGt = paramMap["gee_gt"]
            val geeChallenge = paramMap["gee_challenge"]
            val result = BiliResponse.Success(
                ttl = 1,
                code = 0,
                message = "0",
                data = Captcha(
                    geetest = Geetest(
                        challenge = geeChallenge!!,
                        gt = geeGt!!
                    ),
                    tencent = Tencent(appid = ""),
                    token = recaptchaToken!!,
                    type = ct!!
                )
            )
            _captcha.value = result
        }
}