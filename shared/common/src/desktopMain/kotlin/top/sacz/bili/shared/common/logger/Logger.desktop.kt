package top.sacz.bili.shared.common.logger

actual object Logger {
    actual fun d(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun i(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun w(tag: String, message: String) {
        println("$tag: $message")
    }

    actual fun e(tag: String, throwable: Throwable) {
        throwable.printStackTrace()
    }

}