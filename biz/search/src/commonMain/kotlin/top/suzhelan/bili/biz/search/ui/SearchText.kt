package top.suzhelan.bili.biz.search.ui

data class SearchTextPart(val text: String, val highlighted: Boolean)

private val emphasis =
    Regex("<em\\b[^>]*>(.*?)</em>", setOf(RegexOption.IGNORE_CASE, RegexOption.DOT_MATCHES_ALL))
private val tags = Regex("<[^>]*>")
private val entities =
    Regex("&(#x[0-9a-fA-F]+|#[0-9]+|amp|lt|gt|quot|apos|nbsp);", RegexOption.IGNORE_CASE)

fun searchTextParts(value: String): List<SearchTextPart> = buildList {
    var position = 0
    for (match in emphasis.findAll(value)) {
        if (match.range.first > position) add(
            SearchTextPart(
                plainSearchText(
                    value.substring(
                        position,
                        match.range.first
                    )
                ), false
            )
        )
        add(SearchTextPart(plainSearchText(match.groupValues[1]), true))
        position = match.range.last + 1
    }
    if (position < value.length) add(
        SearchTextPart(
            plainSearchText(value.substring(position)),
            false
        )
    )
}

fun plainSearchText(value: String): String = entities.replace(tags.replace(value, "")) { match ->
    when (val entity = match.groupValues[1].lowercase()) {
        "amp" -> "&"
        "lt" -> "<"
        "gt" -> ">"
        "quot" -> "\""
        "apos" -> "'"
        "nbsp" -> " "
        else -> {
            val code =
                if (entity.startsWith("#x")) entity.drop(2).toIntOrNull(16) else entity.drop(1)
                    .toIntOrNull()
            when {
                code == null || code !in 1..0x10ffff || code in 0xd800..0xdfff -> match.value
                code <= 0xffff -> code.toChar().toString()
                else -> ((0xd800 + ((code - 0x10000) shr 10)).toChar().toString() +
                        (0xdc00 + ((code - 0x10000) and 0x3ff)).toChar())
            }
        }
    }
}

fun searchImageUrl(value: String): String = when {
    value.startsWith("//") -> "https:$value"
    value.startsWith("http://") -> "https://${value.removePrefix("http://")}"
    else -> value
}

fun searchDuration(value: String): String {
    val parts = value.split(':')
    return if (parts.size in 2..3 && parts.all {
            it.toIntOrNull()?.let { number -> number >= 0 } == true
        }) {
        parts.joinToString(":") { it.toInt().toString().padStart(2, '0') }
    } else value
}

fun searchCount(value: Long): String {
    val unit = when {
        value >= 100_000_000 -> 100_000_000L to "亿"
        value >= 10_000 -> 10_000L to "万"
        else -> return value.toString()
    }
    val tenth = (value % unit.first) / (unit.first / 10)
    return "${value / unit.first}${if (tenth > 0) ".$tenth" else ""}${unit.second}"
}
