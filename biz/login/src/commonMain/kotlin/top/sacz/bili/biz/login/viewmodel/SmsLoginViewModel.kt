package top.sacz.bili.biz.login.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import top.sacz.bili.api.Response
import top.sacz.bili.api.ext.apiCall

import top.sacz.bili.biz.login.api.SmsLoginApi
import top.sacz.bili.biz.login.model.CountryList

class SmsLoginViewModel : ViewModel() {
    private val smsLoginApi = SmsLoginApi()
    private val _countryList = MutableStateFlow<Response<CountryList>>(Response.Loading)
    val countryList = _countryList.asStateFlow()
    fun getCountryCode() = viewModelScope.launch {
        _countryList.value = apiCall {
            smsLoginApi.getCountryCode()
        }
    }
}