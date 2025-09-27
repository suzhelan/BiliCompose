/*
 * Copyright (C) 2024-2025 OpenAni and contributors.
 *
 * 此源代码的使用受 GNU AFFERO GENERAL PUBLIC LICENSE version 3 许可证的约束, 可以在以下链接找到该许可证.
 * Use of this source code is governed by the GNU AGPLv3 license, which can be found at the following link.
 *
 * https://github.com/open-ani/ani/blob/main/LICENSE
 */

@file:Suppress("NOTHING_TO_INLINE", "KotlinRedundantDiagnosticSuppress")

package top.suzhelan.bili.player.platform

import androidx.compose.runtime.ProvidableCompositionLocal

expect val BiliLocalContext: ProvidableCompositionLocal<BiliContext>

expect abstract class BiliContext

sealed class Platform {
    abstract val name: String
    abstract val arch: Arch

    class Android(override val name: String = "Android", override val arch: Arch) : Platform()
    class Desktop(
        override val name: String = "Desktop",
        val type: String,
        override val arch: Arch
    ) : Platform()

    class IOS(override val name: String = "IOS", override val arch: Arch = Arch.AARCH_64) :
        Platform()

    /**
     * 架构
     * Desktop通过System.getProperty("os.arch")获取
     * Android通过Build.SUPPORTED_ABIS获取
     * IOS没有架构的概念,都是64
     */
    enum class Arch {
        ARMEABI_V7A,
        ARM64_V8A,
        X86,
        X86_X64,
        AMD_64,
        ARM,
        AARCH_64
    }
}

fun Platform.isIos() = this is Platform.IOS
fun Platform.isAndroid() = this is Platform.Android
fun Platform.isDesktop() = this is Platform.Desktop

expect fun getCurrentPlatform(): Platform