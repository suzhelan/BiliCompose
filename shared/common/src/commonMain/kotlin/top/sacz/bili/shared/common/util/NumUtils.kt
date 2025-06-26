package top.sacz.bili.shared.common.util

import top.sacz.bili.shared.common.ext.format


fun Int.toStringCount(): String {
    return when {
        this < 10000 -> this.toString()
        else -> "%.1f万".format(this.toDouble() / 10000) // 保留一位小数
    }
}

object NumUtils {

}