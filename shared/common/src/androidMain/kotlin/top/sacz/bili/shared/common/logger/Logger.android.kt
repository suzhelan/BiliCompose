package top.sacz.bili.shared.common.logger

import android.util.Log

actual object Logger {
    actual fun d(tag: String, message: String) {
        Log.d(tag, message)
    }

    actual fun i(tag: String, message: String) {
        Log.i(tag, message)
    }

    actual fun w(tag: String, message: String) {
        Log.w(tag, message)
    }

    actual fun e(tag: String, throwable: Throwable) {
        Log.e(tag, throwable.message.toString(), throwable)
    }

}