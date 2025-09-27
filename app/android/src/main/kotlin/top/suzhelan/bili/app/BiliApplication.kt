package top.suzhelan.bili.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner

class BiliApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(AppLifecycleDelegate(this))
    }
}