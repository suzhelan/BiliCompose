package top.sacz.bili.shared.common.util


import kotlinx.datetime.TimeZone
import kotlinx.datetime.number
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

object TimeUtils {
    fun formatMinutesToTime(seconds: Int): String {
        // 修复方案2（模板表达式，推荐）：
        val minutes = seconds / 60
        val remainingSeconds = seconds % 60
        return "$minutes:${remainingSeconds.toString().padStart(2, '0')}"
    }

    @OptIn(ExperimentalTime::class)
    fun formatTimeAgo(seconds: Long): String {
        val currentTime = Clock.System.now().epochSeconds
        val delta = currentTime - seconds

        return when {
            delta < 60 -> "${delta}秒前"
            delta < 3600 -> "${delta / 60}分钟前"
            delta < 86400 -> "${delta / 3600}小时前"
            else -> {
                val currentDate = Clock.System.now() // 改用 kotlinx.datetime.Instant
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val date = Instant.fromEpochMilliseconds(seconds * 1000)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

                when (currentDate.toEpochDays() - date.toEpochDays()) {
                    1L -> "昨天"
                    2L -> "前天"
                    else -> "${date.year}-${date.month.number}-${date.day}"
                }
            }
        }
    }

    /**
     * 秒级时间戳转换为时间
     * yyyy年MM月dd日 HH:mm
     */
    @OptIn(ExperimentalTime::class)
    fun formatTime(seconds: Long): String {
        val date = Instant.fromEpochMilliseconds(seconds * 1000)
            .toLocalDateTime(TimeZone.currentSystemDefault())
        return "${date.year}年${date.month.number}月${date.day}日 ${date.hour}:${
            date.minute.toString().padStart(2, '0')
        }"
    }

}




