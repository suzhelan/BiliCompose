package top.sacz.bili.storage.config

import com.russhwolf.settings.PropertiesSettings
import com.russhwolf.settings.Settings
import java.io.File
import java.nio.file.Files
import java.util.Properties

actual object SettingsConfig {
    actual val factor: Settings.Factory = PropertiesSettingsFactory()

    class PropertiesSettingsFactory : Settings.Factory {

        override fun create(name: String?): Settings {
            val userHome = System.getProperty("user.home")
            val properties = Properties()
            var dataPath = File("./data/prop/default.prop").toPath()
            if (name != null) {
                dataPath = File("./data/prop/$name.prop").toPath()
            }

            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath.parent)
                Files.createFile(dataPath)
            }
            properties.load(Files.newBufferedReader(dataPath))
            return PropertiesSettings(properties) {
                properties.store(Files.newBufferedWriter(dataPath), "This is a data file created by BiliCompose , Do not delete")
            }

        }
    }
}