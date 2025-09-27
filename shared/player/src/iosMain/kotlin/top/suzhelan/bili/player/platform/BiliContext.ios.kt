package top.suzhelan.bili.player.platform

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf

actual fun getCurrentPlatform(): Platform {
    return Platform.IOS()
}

actual val BiliLocalContext: ProvidableCompositionLocal<BiliContext>
    get() = compositionLocalOf {
        error("Not implemented")
    }

actual abstract class BiliContext