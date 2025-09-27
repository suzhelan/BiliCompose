package top.suzhelan.bili.biz.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import dev.datlag.kcef.KCEF
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import kotlin.math.max

fun desktopDestroyed() {
    KCEF.disposeBlocking()
}
@Composable
fun KCEFInit() {
    var restartRequired by remember { mutableStateOf(false) }
    var downloading by remember { mutableStateOf(0F) }
    var initialized by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        withContext(Dispatchers.IO) {
            KCEF.init(builder = {
                installDir(File("kcef-bundle"))
                progress {
                    onDownloading {
                        downloading = max(it, 0F)
                    }
                    onInitialized {
                        initialized = true
                    }
                }
                settings {
                    cachePath = File("cache").absolutePath
                }
            }, onError = {
                it?.printStackTrace()
            }, onRestartRequired = {
                restartRequired = true
            })
        }
    }
    if (!initialized && downloading != 0F) {
        Window(onCloseRequest = {}, title = "KCEF Initializing...") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (restartRequired) {
                    Text(text = "Restart required.")
                } else {
                    if (initialized) {
                        Text("Initialized")
                    } else {
                        Text(text = if (downloading == 100F) "Installation..." else "Downloading: ${downloading}%")
                    }
                }
            }
        }
    }
}