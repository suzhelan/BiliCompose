package top.suzhelan.bili.api.config

import io.ktor.http.encodeURLParameter

import kotlinx.serialization.Serializable
import top.suzhelan.bili.shared.common.ext.md5
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 使用了wbi接口的 可以使用这个试试
 */
object BiliWbi {
    suspend fun getEncQuery(params: Map<String, String>): String {
        val biliTicketData = BiliTicket.getBiliTickerData()
        val wbiParams = WbiParams(
            imgKey = extractImgKeyFromUrl(biliTicketData.nav.img),
            subKey = extractImgKeyFromUrl(biliTicketData.nav.sub)
        )
        return wbiParams.enc(params)
    }

    private fun extractImgKeyFromUrl(url: String): String {
        // 提取文件名部分，例如从https://i0.hdslb.com/bfs/wbi/4932caff0ff746eab6f01bf08b70ac45.png中获取 4932caff0ff746eab6f01bf08b70ac45
        val fileNameWithExtension = url.substringAfterLast("/")
        val fileName = fileNameWithExtension.substringBeforeLast(".")
        return fileName
    }
}

@Serializable
data class WbiParams(
    val imgKey: String,
    val subKey: String,
) {
    private val mixinKeyEncTab = intArrayOf(
        46, 47, 18, 2, 53, 8, 23, 32, 15, 50, 10, 31, 58, 3, 45, 35, 27, 43, 5, 49,
        33, 9, 42, 19, 29, 28, 14, 39, 12, 38, 41, 13, 37, 48, 7, 16, 24, 55, 40,
        61, 26, 17, 0, 1, 60, 51, 30, 4, 22, 25, 54, 21, 56, 59, 6, 63, 57, 62, 11,
        36, 20, 34, 44, 52
    )

    private val mixinKey: String
        get() = (imgKey + subKey).let { s ->
            buildString {
                repeat(32) {
                    append(s[mixinKeyEncTab[it]])
                }
            }
        }

    @OptIn(ExperimentalTime::class)
    fun enc(params: Map<String, Any?>): String {
        val sortedList = params.entries.sortedBy { it.key }
        val sorted = mutableMapOf<String, Any?>()
        for ((key, value) in sortedList) {
            sorted[key] = value
        }
        return buildString {
            append(sorted.toQueryString())
            val wts = Clock.System.now().epochSeconds
            sorted["wts"] = wts
            append("&wts=")
            append(wts)
            append("&w_rid=")
            append((sorted.toQueryString() + mixinKey).md5())
        }
    }

    private fun Map<String, Any?>.toQueryString() =
        this.filterValues { it != null }.entries.joinToString("&") { (k, v) ->
            "${k.encodeURLParameter()}=${v.toString().encodeURLParameter()}"
        }
}


