package top.sacz.bili.player.platform

import androidx.compose.runtime.ProvidableCompositionLocal


actual typealias BiliContext = android.content.Context

actual val BiliLocalContext: ProvidableCompositionLocal<BiliContext>
    get() = androidx.compose.ui.platform.LocalContext
