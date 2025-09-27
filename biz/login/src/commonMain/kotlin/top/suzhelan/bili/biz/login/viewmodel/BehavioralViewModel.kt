package top.suzhelan.bili.biz.login.viewmodel

import bilicompose.biz.login.generated.resources.Res
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import top.suzhelan.bili.shared.common.base.BaseViewModel

/**
 * 行为验证ViewModel
 */
class BehavioralViewModel : BaseViewModel() {

    private val _htmlData = MutableStateFlow("等待载入...")
    val htmlData = _htmlData.asStateFlow()
    fun loadHtmlData() = launchTask {
        _htmlData.value = Res.readBytes("files/geetest-lite.html").decodeToString()
    }
}