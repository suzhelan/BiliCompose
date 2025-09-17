package top.sacz.bili.player.platform

import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import java.rmi.UnexpectedException

actual fun getCurrentPlatform(): Platform {
    val arch = when (System.getProperty("os.arch").lowercase()) {
        "x86" -> Platform.Arch.X86
        "x86_64" -> Platform.Arch.X86_X64//常见于Linux和macOS
        "amd64" -> Platform.Arch.AMD_64//常见于Windows
        "arm" -> Platform.Arch.ARM
        "aarch64" -> Platform.Arch.AARCH_64
        else -> throw UnexpectedException("Not supported arch")
    }
    val osName = System.getProperty("os.name")
    val systemType = when {
        osName.contains("linux", ignoreCase = true) -> "linux"
        osName.contains("win", ignoreCase = true) -> "win"
        osName.contains("mac", ignoreCase = true) -> "mac"
        else -> throw UnexpectedException("Not supported os: $osName")
    }
    return Platform.Desktop(type = systemType, arch = arch)
}

actual val BiliLocalContext: ProvidableCompositionLocal<BiliContext>
    get() = compositionLocalOf {
        //构造空的BiliContext
        DesktopBiliContext()
    }

actual abstract class BiliContext

class DesktopBiliContext : BiliContext() {

}