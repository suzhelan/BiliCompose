package top.sacz.bili.shared.common.ext

import io.ktor.http.encodeURLParameter
import io.ktor.utils.io.core.toByteArray
import korlibs.crypto.MD5

/**
 * 自定义的 String.format 函数，支持跨平台使用。
 * @param args 格式化参数
 * @return 格式化后的字符串
 */
expect fun String.format(vararg args: Any?): String

inline fun String.format(format: String, vararg args: Any?): String {
    return format.format(*args)
}

// 通用 URL 编码
fun String.urlEncode(): String {
    return this.encodeURLParameter()
}

// 通用 MD5（使用 Krypto 等跨平台库）
fun String.md5(): String {
    return MD5.digest(this@md5.toByteArray()).hex
}