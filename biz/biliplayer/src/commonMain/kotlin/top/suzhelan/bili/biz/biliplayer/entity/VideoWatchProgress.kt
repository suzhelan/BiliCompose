package top.suzhelan.bili.biz.biliplayer.entity

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Web 播放器返回的历史观看位置，时间单位为毫秒。 */
@Serializable
data class VideoWatchProgress(
    @SerialName("last_play_time")
    val lastPlayTime: Long = 0,
    @SerialName("last_play_cid")
    val lastPlayCid: Long = 0,
)
