package top.sacz.bili

import androidx.compose.material3.Text
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import dev.datlag.kcef.KCEF
import dev.datlag.kcef.KCEFBuilder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BiliCompose",
    ) {
        var restartRequired by remember { mutableStateOf(false) }
        var downloading by remember { mutableStateOf(0F) }
        var initialized by remember { mutableStateOf(false) }
        val download: KCEFBuilder.Download = remember { KCEFBuilder.Download.Builder().github().build() }

        DisposableEffect(Unit) {
            System.setProperty("kcef.force.disable.hardware_acceleration", "true")
            onDispose {
                System.clearProperty("kcef.force.disable.hardware_acceleration")
            }
        }
        LaunchedEffect(Unit) {
            withContext(Dispatchers.IO) {
                KCEF.init(builder = {
                    addArgs("--disable-gpu")
                    addArgs("--disable-software-rasterizer")
                    addArgs("--disable-gpu-compositing")
                    addArgs("--disable-gpu-sandbox")
                    addArgs("--no-sandbox")
                    addArgs("--add-opens", "java.desktop/sun.awt=ALL-UNNAMED")
                    addArgs("--add-opens", "java.desktop/java.awt.peer=ALL-UNNAMED")
                    installDir(File("kcef-bundle"))

                    /*
                      Add this code when using JDK 17.
                      Builder().github {
                          release("jbr-release-17.0.10b1087.23")
                      }.buffer(download.bufferSize).build()
                     */
                    progress {
                        onDownloading {
                            downloading = max(it, 0F)
                        }
                        onInitialized {
                            initialized = true
                        }
                    }
                    settings {
                        locale = "zh-CN"
                        // 新增以下配置
                        resourcesDirPath = File("kcef-bundle").absolutePath
                        localesDirPath = File("kcef-bundle/locales").absolutePath
                        cachePath = File("cache").absolutePath
                    }
                }, onError = {
                    it?.printStackTrace()
                }, onRestartRequired = {
                    restartRequired = true
                })
            }
        }

        if (restartRequired) {
            Text(text = "Restart required.")
        } else {
            if (initialized) {
                App()
            } else {
                Text(text = "Downloading $downloading%")
            }
        }

        DisposableEffect(Unit) {
            onDispose {
                KCEF.disposeBlocking()
            }
        }
    }
}