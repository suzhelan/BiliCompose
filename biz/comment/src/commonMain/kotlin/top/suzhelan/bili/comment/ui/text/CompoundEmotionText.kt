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
import top.suzhelan.bili.api.HttpJsonDecoder
import top.suzhelan.bili.comment.entity.Comment


object CompoundEmojiMessageModel {
    data class MessageContent(
        val message: String,
        val emote: Map<String, Comment.Content.Emote>
    )

    sealed class MessagePart {
        data class Text(val content: String) : MessagePart()
        data class Emote(
            val key: String,
            val data: Comment.Content.Emote
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
                //计算高度，小表情要比文本高一点，大表情正常算
                val emotionSize = if (emote.data.meta.size == 1) {
                    size * 1.3f
                } else {
                    emote.data.meta.size.toFloat()
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

@Composable
fun DemoView(
    modifier: Modifier = Modifier
) {
    val data = """
        {
          "message": "[希丝奈cisne-表情包_打call]你好你好[希丝奈cisne-表情包_打call][我没发表情哦]嘿嘿嘿[希丝奈cisne-表情包_抱抱][希丝奈cisne-表情包_抱抱][希丝奈cisne-表情包_抱抱]",
          "members": [],
          "emote": {
            "[希丝奈cisne-表情包_打call]": {
              "id": 81786,
              "package_id": 5578,
              "state": 0,
              "type": 3,
              "attr": 0,
              "text": "[希丝奈cisne-表情包_打call]",
              "url": "https://i0.hdslb.com/bfs/garb/3a1b16df55bdd5c196ec61c25aefb0e6388b0fbb.png",
              "meta": {
                "size": 2
              },
              "mtime": 1723003260,
              "jump_url": "https://www.bilibili.com/h5/mall/digital-card/home?act_id=103074&from=emoji&f_source=garb&-Abrowser=live&hybrid_set_header=2&navhide=1&anchor_task=1",
              "jump_title": "打call"
            },
            "[希丝奈cisne-表情包_抱抱]": {
              "id": 81790,
              "package_id": 5578,
              "state": 0,
              "type": 3,
              "attr": 0,
              "text": "[希丝奈cisne-表情包_抱抱]",
              "url": "https://i0.hdslb.com/bfs/garb/b7e5a3389fc9e50832784ae3d8f373fc03fda60b.png",
              "meta": {
                "size": 2
              },
              "mtime": 1723003260,
              "jump_url": "https://www.bilibili.com/h5/mall/digital-card/home?act_id=103074&from=emoji&f_source=garb&-Abrowser=live&hybrid_set_header=2&navhide=1&anchor_task=1",
              "jump_title": "抱抱"
            }
          },
          "jump_url": {

          },
          "max_line": 6
        }
    """.trimIndent()
    val content = HttpJsonDecoder.decodeFromString(Comment.Content.serializer(), data)
    CompoundEmojiMessage(
        content = CompoundEmojiMessageModel.MessageContent(
            message = content.message,
            emote = content.emote,
        ), modifier = modifier
    )
}

