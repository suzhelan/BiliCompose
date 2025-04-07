package top.sacz.bili.storage.config

import android.annotation.SuppressLint
import android.content.Context
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

@SuppressLint("StaticFieldLeak")
actual object SettingsConfig {
    lateinit var context: Context

    actual val factor: Settings.Factory by lazy {
        SharedPreferencesSettings.Factory(context)
    }
    actual val platform: String = "ANDROID"
}