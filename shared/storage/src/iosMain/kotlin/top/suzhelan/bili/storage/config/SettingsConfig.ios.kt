package top.suzhelan.bili.storage.config

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings

actual object SettingsConfig {
    actual val factor: Settings.Factory = NSUserDefaultsSettings.Factory()
}