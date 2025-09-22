package top.sacz.bili.app

import android.content.Context
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import top.sacz.bili.player.controller.AndroidPlayerParam
import top.sacz.bili.storage.config.SettingsConfig

/**
 * 所有需要Android平台Context初始化的操作，比如初始化存储，初始化播放器参数等等，都放在这里
 */
class AppLifecycleDelegate(private val context: Context) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        //初始化数据库和一些需要context的操作
        SettingsConfig.context = context
        AndroidPlayerParam.init(context)
    }

}