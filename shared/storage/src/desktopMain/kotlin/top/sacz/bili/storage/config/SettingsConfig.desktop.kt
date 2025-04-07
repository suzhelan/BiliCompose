package top.sacz.bili.storage.config

import com.russhwolf.settings.PreferencesSettings
import com.russhwolf.settings.Settings

actual object SettingsConfig {
    actual val factor: Settings.Factory = PreferencesSettings.Factory()
    actual val platform: String = "DESKTOP"
}