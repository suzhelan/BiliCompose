package top.sacz.bili.storage.config

import com.russhwolf.settings.Settings

expect object SettingsConfig {
    val platform : String
    val factor : Settings.Factory
}