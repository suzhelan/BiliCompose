package top.suzhelan.bili.player.controller

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import org.openani.mediamp.PlaybackState
import top.suzhelan.bili.player.util.TimeUtil

/** 同一次采样的播放进度，内部始终保留毫秒精度。 */
data class PlayerProgress(
    val positionMillis: Long = 0,
    val durationMillis: Long = 0,
    val finished: Boolean = false,
) {
    // 播放中只上报已走完的整秒；结束后包含最后不足一秒的部分。
    val positionSeconds: Long
        get() = if (finished && durationMillis > 0) {
            TimeUtil.durationSeconds(durationMillis)
        } else {
            positionMillis / 1000
        }
}

fun playerProgressFlow(
    positionMillis: StateFlow<Long>,
    durationMillis: StateFlow<Long>,
    playbackState: StateFlow<PlaybackState>,
): Flow<PlayerProgress> =
    combine(positionMillis, durationMillis, playbackState) { position, duration, state ->
        val validDuration = duration.coerceAtLeast(0)
        val finished = state == PlaybackState.FINISHED
        val validPosition = when {
            finished && validDuration > 0 -> validDuration
            validDuration > 0 -> position.coerceIn(0, validDuration)
            else -> position.coerceAtLeast(0)
        }
        PlayerProgress(validPosition, validDuration, finished)
    }
