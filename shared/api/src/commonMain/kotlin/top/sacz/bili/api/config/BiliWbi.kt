package top.sacz.bili.api.config

import io.ktor.http.encodeURLParameter
import kotlinx.datetime.Clock
import kotlinx.serialization.Serializable
import top.sacz.bili.shared.common.ext.md5


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

    fun getWRid(params: Map<String, String>): String {
        val sortedList = params.entries.sortedBy { it.key }
        val sorted: MutableMap<String, String> = mutableMapOf()
        for ((key, value) in sortedList) {
            sorted[key] = value
        }
        return (sorted.toQueryString() + mixinKey).md5()
    }

    fun getWTs(): Long {
        return Clock.System.now().epochSeconds
    }

    private fun Map<String, Any?>.toQueryString() =
        this.filterValues { it != null }.entries.joinToString("&") { (k, v) ->
            "${k.encodeURLParameter()}=${v.toString().encodeURLParameter()}"
        }
}


