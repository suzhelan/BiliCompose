package top.suzhelan.bili.comment.ui.text

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import top.suzhelan.bili.comment.entity.NewComment


object CompoundEmojiMessageModel {
    data class MessageContent(
        val message: String,
        val emote: Map<String, MessageEmote>
    )

    data class MessageEmote(
        val text: String,
        val url: String,
        val size: Int,
    )

    sealed class MessagePart {
        data class Text(val content: String) : MessagePart()
        data class Emote(
            val key: String,
            val data: MessageEmote
        ) : MessagePart()
    }

    private val emoteRegex = """\[([^]]+)]""".toRegex()

    fun parseMessage(content: MessageContent): List<MessagePart> {
        val parts = mutableListOf<MessagePart>()
        var currentIndex = 0
        val message = content.message

        emoteRegex.findAll(message).forEach { matchResult ->
            // 添加前置文本
            if (matchResult.range.first > currentIndex) {
                val text = message.substring(currentIndex, matchResult.range.first)
                parts.add(MessagePart.Text(text))
            }

            // 处理表情
            val emoteKey = matchResult.value
            val emoteData = content.emote[emoteKey]

            if (emoteData != null) {
                parts.add(MessagePart.Emote(emoteKey, emoteData))
            } else {
                parts.add(MessagePart.Text(emoteKey))
            }

            currentIndex = matchResult.range.last + 1
        }
        // 添加剩余文本
        if (currentIndex < message.length) {
            parts.add(MessagePart.Text(message.substring(currentIndex)))
        }
        return parts
    }
}


@Composable
fun CompoundEmojiMessage(
    content: CompoundEmojiMessageModel.MessageContent,
    modifier: Modifier = Modifier,
    size: Int = 14,
    textStyle: TextStyle = TextStyle.Default.copy(fontSize = size.sp)
) {
    val messageParts = remember(content) {
        CompoundEmojiMessageModel.parseMessage(content)
    }

    // 构建内联内容映射
    val inlineContentMap = buildMap {
        messageParts.filterIsInstance<CompoundEmojiMessageModel.MessagePart.Emote>()
            .forEach { emote ->
                val key = "emote_${emote.key.hashCode()}"
                // B 站 meta.size 是表情档位，不是实际 sp。size=2 直接当 2sp 会导致大表情几乎不可见。
                val emotionSize = when {
                    emote.data.size <= 1 -> size * 1.3f
                    emote.data.size <= 4 -> size * emote.data.size.toFloat()
                    else -> emote.data.size.toFloat()
                }
                put(
                    key, InlineTextContent(
                        Placeholder(
                            width = emotionSize.sp,
                            height = emotionSize.sp,
                            placeholderVerticalAlign = PlaceholderVerticalAlign.TextBottom
                        )
                    ) {
                        AsyncImage(
                            model = emote.data.url,
                            contentDescription = emote.data.text,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    })
            }
    }

    // 构建注解字符串
    val annotatedString = buildAnnotatedString {
        messageParts.forEach { part ->
            when (part) {
                is CompoundEmojiMessageModel.MessagePart.Text -> append(part.content)
                is CompoundEmojiMessageModel.MessagePart.Emote -> {
                    val inlineKey = "emote_${part.key.hashCode()}"
                    appendInlineContent(inlineKey, part.key)
                }
            }
        }
    }

    Text(
        text = annotatedString,
        inlineContent = inlineContentMap,
        style = textStyle,
        modifier = modifier
    )
}


fun NewComment.Content.Emote.toMessageEmote() = CompoundEmojiMessageModel.MessageEmote(
    text = text,
    url = url,
    size = meta.size,
)

