package top.sacz.bili.biz.player.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class PlayerArgsItem(
    @SerialName("from") val from: String = "",
    @SerialName("result") val result: String = "",
    @SerialName("message") val message: String = "",
    @SerialName("quality") val quality: Int = 0,
    @SerialName("format") val format: String = "",
    @SerialName("timelength") val timeLength: Int = 0,
    @SerialName("accept_format") val acceptFormat: String = "",
    @SerialName("accept_description") val acceptDesc: List<String> = emptyList(),
    @SerialName("accept_quality") val acceptQuality: List<Int> = emptyList(),
    @SerialName("video_codecid") val videoCodecid: Int = 0,
    @SerialName("seek_param") val seekParam: String = "",
    @SerialName("seek_type") val seekType: String = "",
    @SerialName("dash") val dash: Dash = Dash(),
    @SerialName("durl") val durl: List<Durl> = emptyList(),
    @SerialName("support_formats") val supportFormats: List<FormatItem> = emptyList(),
    @SerialName("last_play_time") val lastPlayTime: Int = 0,
    @SerialName("last_play_cid") val lastPlayCid: Int = 0
) {
    @Serializable
    data class Dash(
        @SerialName("duration") val duration: Int = 0,
        @SerialName("minBufferTime") val minBufferTime: Double = 0.0,
        @SerialName("video") val video: List<VideoItem> = emptyList(),
        @SerialName("audio") val audio: List<AudioItem> = emptyList(),
        @SerialName("dolby") val dolby: Dolby = Dolby(),
        @SerialName("flac") val flac: Flac? = null
    )

    @Serializable
    data class Durl(
        @SerialName("order") val order: Int = 0,
        @SerialName("length") val length: Int = 0,
        @SerialName("size") val size: Int = 0,
        @SerialName("ahead") val ahead: String = "",
        @SerialName("vhead") val vhead: String = "",
        @SerialName("url") val url: String = "",
        @SerialName("backup_url") val backupUrl: List<String> = emptyList()
    )

    @Serializable
    data class VideoItem(
        @SerialName("id") val id: Int = 0,
        @SerialName("baseUrl") val baseUrl: String = "",
        @SerialName("backupUrl") val backupUrl: List<String> = emptyList(),
        @SerialName("bandWidth") val bandWidth: Int = 0,
        @SerialName("mime_type") val mimeType: String = "",
        @SerialName("codecs") val codecs: String = "",
        @SerialName("width") val width: Int = 0,
        @SerialName("height") val height: Int = 0,
        @SerialName("frameRate") val frameRate: String = "",
        @SerialName("sar") val sar: String = "",
        @SerialName("startWithSap") val startWithSap: Int = 0,
        @SerialName("segmentBase") val segmentBase: JsonObject = JsonObject(mapOf()),
        @SerialName("codecid") val codecid: Int = 0
    )

    @Serializable
    data class AudioItem(
        @SerialName("id") val id: Int = 0,
        @SerialName("baseUrl") val baseUrl: String = "",
        @SerialName("backupUrl") val backupUrl: List<String> = emptyList(),
        @SerialName("bandWidth") val bandWidth: Int = 0,
        @SerialName("mime_type") val mimeType: String = "",
        @SerialName("codecs") val codecs: String = "",
        @SerialName("width") val width: Int = 0,
        @SerialName("height") val height: Int = 0,
        @SerialName("frameRate") val frameRate: String = "",
        @SerialName("sar") val sar: String = "",
        @SerialName("startWithSap") val startWithSap: Int = 0,
        @SerialName("segmentBase") val segmentBase: JsonObject = JsonObject(mapOf()),
        @SerialName("codecid") val codecid: Int = 0
    )

    @Serializable
    data class FormatItem(
        @SerialName("quality") val quality: Int = 0,
        @SerialName("format") val format: String = "",
        @SerialName("new_description") val newDesc: String = "",
        @SerialName("display_desc") val displayDesc: String = "",
        @SerialName("codecs") val codecs: List<String> = emptyList()
    )

    @Serializable
    data class Dolby(
        @SerialName("type") val type: Int = 0,
        @SerialName("audio") val audio: List<AudioItem>? = null
    )

    @Serializable
    data class Flac(
        @SerialName("display") val display: Boolean = false,
        @SerialName("audio") val audio: AudioItem = AudioItem()
    )
}

