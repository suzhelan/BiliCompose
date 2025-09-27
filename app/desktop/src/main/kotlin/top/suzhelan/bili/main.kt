package top.suzhelan.bili

import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "BiliCompose",
    ) {
        App()
        DesktopAppLifecycleDelegate.onCreate()
        DisposableEffect(Unit) {
            onDispose {
                DesktopAppLifecycleDelegate.onDestroy()
            }
        }
    }
}