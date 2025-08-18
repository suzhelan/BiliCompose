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

fun Int.formatPlayCount(): String {
    fun formatUnit(value: Int, divisor: Int, unit: String): String {
        val integerPart = value / divisor
        val remainder = value % divisor
        return when {
            remainder == 0 -> "$integerPart$unit"
            else -> {
                val decimalDigits = when (divisor) {
                    10_000 -> remainder / 100 // 处理万单位小数（保留两位）
                    1_000 -> remainder / 10  // 处理千单位小数（保留两位）
                    else -> 0
                }
                val decimalStr = buildString {
                    append(integerPart)
                    append('.')
                    when {
                        decimalDigits % 10 == 0 -> append(decimalDigits / 10)
                        else -> append(decimalDigits.toString().padStart(2, '0'))
                    }
                }.trimEnd('0').trimEnd('.')
                "$decimalStr$unit"
            }
        }
    }

    return when {
        this >= 10_000 -> formatUnit(this, 10_000, "万")
        this >= 1_000 -> formatUnit(this, 1_000, "千")
        else -> toString()
    }
}