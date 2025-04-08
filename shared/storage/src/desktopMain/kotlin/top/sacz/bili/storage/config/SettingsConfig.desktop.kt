package top.sacz.bili.storage.config

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.nio.file.Files
import java.util.Properties

actual object SettingsConfig {
    actual val factor: Settings.Factory = PropertiesSettingsFactory()
    actual val platform: String = "DESKTOP"

    class PropertiesSettingsFactory : Settings.Factory {

        override fun create(name: String?): Settings {
            val userHome = System.getProperty("user.home")
            val properties = Properties()
            if (name == null) {
                val defaultPath = File("./data/prop/default.prop").toPath()
                if (!Files.exists(defaultPath)) {
                    Files.createFile(defaultPath)
                }
                properties.load(Files.newBufferedReader(defaultPath))
                return PropertiesSettings(properties) {
                    properties.store(Files.newBufferedWriter(defaultPath), "")
                }
            } else {
                val toPath = File("./data/prop/$name.prop").toPath()
                if (!Files.exists(toPath)) {
                    Files.createFile(toPath)
                }
                properties.load(Files.newBufferedReader(toPath))
                return PropertiesSettings(properties) {
                    properties.store(Files.newBufferedWriter(toPath), "")
                }
            }
        }
    }
}