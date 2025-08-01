package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.login.api.QRCodeLoginApi
import top.sacz.bili.biz.login.model.TvQRCode
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.auth.entity.TvLoginResult
import top.sacz.bili.shared.common.ext.toTrue
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class QRCodeLoginViewModel : ViewModel() {
    private val _qrCode = MutableStateFlow<BiliResponse<TvQRCode>>(BiliResponse.Loading)
    val qrCode = _qrCode.asStateFlow()

    private val _qrCodeResult = MutableStateFlow<BiliResponse<TvLoginResult>>(BiliResponse.Wait)

    val isShowLoginSuccessDialog = MutableStateFlow(false)

    private val _queryMessage = MutableStateFlow("等待二维码加载")
    val queryMessage = _queryMessage.asStateFlow()

    fun getQRCode() = viewModelScope.launch {
        //被调用时 取消之前的订阅任务
        currentSubscribingJob?.cancel()
        //  加载二维码
        _qrCode.value = BiliResponse.Loading
        _qrCode.value = apiCall {
            QRCodeLoginApi().getTvQRCode()
        }
        //  开始订阅结果
        if (qrCode.value is BiliResponse.Success) {
            currentSubscribingJob =
                startSubscribingToResults((qrCode.value as BiliResponse.Success<TvQRCode>).data.authCode)
        }
    }

    private val _sendCountdown = MutableStateFlow(-1)
    val sendCountdown = _sendCountdown.asStateFlow()
    private var currentSubscribingJob: Job? = null

    /**
     * 订阅扫码结果
     */
    @OptIn(ExperimentalTime::class)
    private fun startSubscribingToResults(authCode: String) = viewModelScope.launch {
        val startTime = Clock.System.now().epochSeconds
        while (true) {
            //检查状态
            val loginResult = _qrCodeResult.value
            if (loginResult is BiliResponse.SuccessOrNull) {
                //成功后停止
                when (loginResult.code) {
                    86039, 86090 -> {
                        //没扫码和扫码未完成
                    }
                    0 -> {
                        //扫码成功 保存登录凭证
                        LoginMapper.clear()
                        LoginMapper.setTvLoginInfo(loginResult.data!!)
                        isShowLoginSuccessDialog.toTrue()
                        return@launch
                    }

                    else -> {
                        //其他状态一律停止
                        break
                    }
                }
            }
            val elapsedSeconds = ((Clock.System.now().epochSeconds - startTime)).toInt()
            val remainingSeconds = 180 - elapsedSeconds
            if (remainingSeconds < 0) break
            _sendCountdown.value = remainingSeconds
            queryQRCodeResult(authCode)
            delay(1000)
        }
    }

    private fun queryQRCodeResult(qrcodeKey: String) = viewModelScope.launch {
        val loginResult = _qrCodeResult.value
        //成功后停止
        if (loginResult is BiliResponse.SuccessOrNull && loginResult.code == 0) {
            return@launch
        }
        _qrCodeResult.value = BiliResponse.Loading
        _qrCodeResult.value = apiCall {
            QRCodeLoginApi().queryTvQRCodeResult(qrcodeKey)
        }
        when (loginResult) {
            is BiliResponse.SuccessOrNull -> {
                _queryMessage.value = loginResult.message
            }

            is BiliResponse.Error -> {
                _queryMessage.value = loginResult.msg
            }

            else -> {
            }
        }
    }
}