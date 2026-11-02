package top.suzhelan.bili.biz.biliplayer.viewmodel

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.flow.transformLatest

/** 完成一次三秒倒计时；后台不消耗剩余时间，调用方取消协程即可取消本次切换。 */
@OptIn(ExperimentalCoroutinesApi::class)
internal suspend fun playbackCountdown(
    foreground: StateFlow<Boolean>,
    onSecondsChange: (Int) -> Unit,
) {
    var ticks = 30
    foreground.transformLatest { active ->
        if (active) {
            while (ticks > 0) {
                delay(100)
                ticks--
                emit((ticks + 9) / 10)
            }
        }
    }.takeWhile { it > 0 }.distinctUntilChanged().collect(onSecondsChange)
}
