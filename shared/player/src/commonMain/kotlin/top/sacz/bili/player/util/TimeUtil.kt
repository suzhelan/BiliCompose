package top.sacz.bili.player.util

object TimeUtil {
    fun formatMillisToTime(millis: Long): String {
        val totalSeconds = millis / 1000
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
