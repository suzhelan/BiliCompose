package top.sacz.bili.shared.common.util




import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

object TimeUtils {
    fun formatMinutesToTime(seconds: Int): String {
        // 修复方案2（模板表达式，推荐）：
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }

    fun formatTimeAgo(videoCreateTime: Long): String {
        val currentTime = Clock.System.now().epochSeconds
        val delta = currentTime - videoCreateTime

        return when {
            delta < 60 -> "${delta}秒前"
            delta < 3600 -> "${delta / 60}分钟前"
            delta < 86400 -> "${delta / 3600}小时前"
            else -> {
                val currentDate = Clock.System.now() // 改用 kotlinx.datetime.Instant
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val videoDate = Instant.fromEpochMilliseconds(videoCreateTime * 1000)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

                when (currentDate.toEpochDays() - videoDate.toEpochDays()) {
                    1 -> "昨天"
                    2 -> "前天"
                    else -> "${videoDate.year}-${videoDate.monthNumber}-${videoDate.dayOfMonth}"
                }
            }
        }
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


}




