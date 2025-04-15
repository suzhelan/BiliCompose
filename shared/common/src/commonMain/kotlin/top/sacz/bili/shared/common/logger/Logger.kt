package top.sacz.bili.shared.common.logger

import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

object Logger {
    init {
        Napier.base(
            DebugAntilog()
        )
    }
    fun d(message: String) {
        d("Default", message)
    }
    fun d(tag: String = "Default", message: String) {
        Napier.d(message, null, tag)
    }

    fun i(tag: String = "Default", message: String) {
        Napier.i(message, null, tag)
    }

    fun w(tag: String = "Default", message: String) {
        Napier.w(message, null, tag)
    }

    fun e(tag: String = "Default", throwable: Throwable) {
        Napier.e("Error", throwable, tag)
    }

    fun e(tag: String = "Default", message: String, throwable: Throwable) {
        Napier.e(message, throwable, tag)
    }
}