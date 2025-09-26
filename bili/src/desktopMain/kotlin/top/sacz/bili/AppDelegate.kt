package top.sacz.bili

import androidx.compose.runtime.Composable
import top.sacz.bili.biz.login.KCEFInit
import top.sacz.bili.biz.login.desktopDestroyed

/**
 * Desktop平台的初始化逻辑
 */
object DesktopAppLifecycleDelegate  {

    /**
     * Desktop App创建时调用
     */
    @Composable
    fun onCreate() {
        KCEFInit()
    }


    /**
     * Desktop App销毁时调用
     */
    fun onDestroy() {
        desktopDestroyed()
    }

}