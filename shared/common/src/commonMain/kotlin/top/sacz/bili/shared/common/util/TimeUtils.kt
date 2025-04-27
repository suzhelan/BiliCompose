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

    fun formatTimeAgo(videoTime: Long): String {
        val currentTime = Clock.System.now().epochSeconds
        val delta = currentTime - videoTime

        return when {
            delta < 60 -> "${delta}秒前"
            delta < 3600 -> "${delta / 60}分钟前"
            delta < 86400 -> "${delta / 3600}小时前"
            else -> {
                val currentDate = Clock.System.now() // 改用 kotlinx.datetime.Instant
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date
                val videoDate = Instant.fromEpochMilliseconds(videoTime * 1000)
                    .toLocalDateTime(TimeZone.currentSystemDefault()).date

                when (val daysDiff = currentDate.toEpochDays() - videoDate.toEpochDays()) {
                    1 -> "昨天"
                    2 -> "前天"
                    else -> "${videoDate.year}-${videoDate.monthNumber}-${videoDate.dayOfMonth}"
                }
            }
        }
    }

}




