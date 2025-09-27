package top.suzhelan.bili.player.platform

import android.os.Build

actual fun getCurrentPlatform(): Platform {
    if (Build.SUPPORTED_ABIS == null) {
        // unit testing
        return Platform.Android(arch = Platform.Arch.ARM64_V8A)
    }
    return Build.SUPPORTED_ABIS.getOrNull(0)?.let { abi ->
        when (abi.lowercase()) {
            "armeabi-v7a" -> Platform.Android(arch = Platform.Arch.ARMEABI_V7A)
            "arm64-v8a" -> Platform.Android(arch = Platform.Arch.ARM64_V8A)
            "x86_64" -> Platform.Android(arch = Platform.Arch.X86_X64)
            else -> Platform.Android(arch = Platform.Arch.ARM64_V8A)
        }
    } ?: Platform.Android(arch = Platform.Arch.ARM64_V8A)
}