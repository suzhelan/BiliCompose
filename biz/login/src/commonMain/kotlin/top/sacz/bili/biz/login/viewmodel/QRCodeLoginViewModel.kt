package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.ext.apiCall
import top.sacz.bili.biz.login.api.QRCodeLoginApi
import top.sacz.bili.biz.login.model.TvQRCode
import top.sacz.bili.shared.auth.entity.LoginResult

class QRCodeLoginViewModel : ViewModel() {
    private val _qrCode = MutableStateFlow<BiliResponse<TvQRCode>>(BiliResponse.Loading)
    val qrCode = _qrCode.asStateFlow()


    private val _qrCodeResult = MutableStateFlow<BiliResponse<LoginResult>>(BiliResponse.Wait)
    private val qrCodeResult = _qrCodeResult.asStateFlow()

    fun getQRCode() = viewModelScope.launch {
        _qrCode.value = BiliResponse.Loading
        _qrCode.value = apiCall {
            QRCodeLoginApi().getTvQRCode()
        }
    }

    fun queryQRCodeResult(qrcodeKey: String) = viewModelScope.launch {
        _qrCodeResult.value = BiliResponse.Loading
        _qrCodeResult.value = apiCall {
            QRCodeLoginApi().queryTvQRCodeResult(qrcodeKey)
        }
    }
}