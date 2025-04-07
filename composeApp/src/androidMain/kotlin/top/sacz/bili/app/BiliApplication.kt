package top.sacz.bili.app

import android.app.Application
import top.sacz.bili.storage.config.SettingsConfig

class BiliApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        //初始化配置
        SettingsConfig.context = this
    }
}