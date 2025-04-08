package top.sacz.bili.shared.common.logger

expect object Logger {
    fun d(tag: String="Default", message: String)
    fun i(tag: String="Default", message: String)
    fun w(tag: String="Default", message: String)
    fun e(tag: String="Default", throwable: Throwable)
}