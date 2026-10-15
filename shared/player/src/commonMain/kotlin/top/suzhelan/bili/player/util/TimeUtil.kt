package top.suzhelan.bili.player.util

object TimeUtil {
    /** 总时长包含最后不足一秒的部分，避免整数截断丢失末尾。 */
    fun durationSeconds(millis: Long): Long {
        val validMillis = millis.coerceAtLeast(0)
        return validMillis / 1000 + if (validMillis % 1000 > 0) 1 else 0
    }

    fun formatDurationMillisToTime(millis: Long): String = formatSeconds(durationSeconds(millis))

    fun formatProgressMillisToTime(positionMillis: Long, durationMillis: Long): String =
        if (durationMillis > 0 && positionMillis >= durationMillis) {
            formatDurationMillisToTime(durationMillis)
        } else {
            formatMillisToTime(positionMillis)
        }

    fun formatMillisToTime(millis: Long): String {
        return formatSeconds(millis.coerceAtLeast(0) / 1000)
    }

    private fun formatSeconds(totalSeconds: Long): String {
        val hours = totalSeconds / 3600
        val remainingSeconds = totalSeconds % 3600
        val minutes = remainingSeconds / 60
        val seconds = remainingSeconds % 60

        val formattedHours = formatTwoDigits(hours)
        val formattedMinutes = formatTwoDigits(minutes)
        val formattedSeconds = formatTwoDigits(seconds)

        return if (hours > 0) {
            "$formattedHours:$formattedMinutes:$formattedSeconds"
        } else {
            "$formattedMinutes:$formattedSeconds"
        }
    }

    private fun formatTwoDigits(value: Long): String = if (value < 10) "0$value" else "$value"
}
