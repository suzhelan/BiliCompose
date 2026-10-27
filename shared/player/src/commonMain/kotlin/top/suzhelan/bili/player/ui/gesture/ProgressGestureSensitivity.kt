package top.suzhelan.bili.player.ui.gesture

/**
 * 手势灵敏度 单位是整个播放器宽度的跨越时间 例如从左边滑倒右边一共推进多少进度
 */
private const val MAX_SEEK_MILLIS_PER_WIDTH = 240_000.0

/**
 * 时长越长，横向拖动的进度比例倍率越低。
 * 滑过整个播放器宽度的时间跨度为 duration * 120s / (duration + 120s)，
 * 短视频接近原有比例，长视频平滑趋近 120 秒，避免一次滑动跳过大量内容。
 */
fun progressGestureSensitivity(totalDurationMillis: Long): Float {
    if (totalDurationMillis <= 0L) return 0f
    return (MAX_SEEK_MILLIS_PER_WIDTH /
            (totalDurationMillis.toDouble() + MAX_SEEK_MILLIS_PER_WIDTH)).toFloat()
}
