package top.suzhelan.bili.biz.user.entity

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames
import kotlinx.serialization.json.JsonPrimitive

@Serializable
data class UserDynamicPage(
    @SerialName("items") val items: List<UserDynamic> = emptyList(),
    @SerialName("has_more") val hasMore: Boolean,
    @SerialName("offset") val offset: String = "",
)

@Serializable
data class UserDynamicDetail(@SerialName("item") val item: UserDynamic)

@Serializable
data class UserDynamic(
    @SerialName("id_str") val id: String,
    @SerialName("type") val type: String,
    @SerialName("basic") val basic: DynamicBasic = DynamicBasic(),
    @SerialName("modules") val modules: DynamicModules,
    @SerialName("orig") val original: UserDynamic? = null,
)

@Serializable
data class DynamicBasic(
    @SerialName("comment_id_str") val commentId: String = "",
    @SerialName("comment_type") val commentType: Int = 0,
)

@Serializable
data class DynamicModules(
    @SerialName("module_author") val author: DynamicAuthor? = null,
    @SerialName("module_dynamic") val content: DynamicContent = DynamicContent(),
    @SerialName("module_stat") val stat: DynamicStat? = null,
    @SerialName("module_tag") val tag: DynamicTag? = null,
)

@Serializable
data class DynamicTag(@SerialName("text") val text: String = "")

@Serializable
data class DynamicAuthor(
    @SerialName("mid") val mid: Long,
    @SerialName("name") val name: String,
    @SerialName("face") val face: String = "",
    @SerialName("pub_time") val pubTime: String = "",
    @SerialName("pub_action") val pubAction: String = "",
    @SerialName("type") val type: String = "",
)

@Serializable
data class DynamicContent(
    @SerialName("desc") val description: DynamicText? = null,
    @SerialName("major") val major: DynamicMajor? = null,
    @SerialName("topic") val topic: DynamicTopic? = null,
)

@Serializable
data class DynamicTopic(
    @SerialName("name") val name: String,
    @SerialName("jump_url") val jumpUrl: String,
)

@Serializable
data class DynamicText(
    @SerialName("text") val text: String,
    @SerialName("rich_text_nodes") val nodes: List<DynamicTextNode> = emptyList(),
)

@Serializable
data class DynamicTextNode(
    @SerialName("type") val type: String,
    @SerialName("text") val text: String,
    @SerialName("jump_url") val jumpUrl: String = "",
    @SerialName("rid") val rid: String = "",
    @SerialName("emoji") val emoji: DynamicEmoji? = null,
)

@Serializable
data class DynamicEmoji(
    @SerialName("icon_url") val url: String,
    @SerialName("size") val size: Int = 1,
)

@Serializable
data class DynamicMajor(
    @SerialName("type") val type: String,
    @SerialName("archive") val archive: DynamicArchive? = null,
    @SerialName("ugc_season") val season: DynamicArchive? = null,
    @SerialName("draw") val draw: DynamicDraw? = null,
    @SerialName("opus") val opus: DynamicOpus? = null,
    @SerialName("article") val article: DynamicArticle? = null,
    @SerialName("pgc") val pgc: DynamicPgc? = null,
    @SerialName("common") val common: DynamicLinkCard? = null,
    @SerialName("music") val music: DynamicLinkCard? = null,
    @SerialName("courses") val courses: DynamicLinkCard? = null,
    @SerialName("none") val unavailable: DynamicUnavailable? = null,
)

@Serializable
data class DynamicArchive(
    @SerialName("aid") val aid: JsonPrimitive,
    @SerialName("cover") val cover: String,
    @SerialName("title") val title: String,
    @SerialName("duration_text") val duration: String = "",
    @SerialName("stat") val stat: DynamicArchiveStat = DynamicArchiveStat(),
)

@Serializable
data class DynamicArchiveStat(
    @SerialName("play") val play: String = "",
    @SerialName("danmaku") val danmaku: String = "",
)

@Serializable
data class DynamicDraw(@SerialName("items") val items: List<DynamicPicture>)

@Serializable
@OptIn(ExperimentalSerializationApi::class)
data class DynamicPicture(
    @SerialName("src") @JsonNames("url") val src: String,
    @SerialName("width") val width: Int = 0,
    @SerialName("height") val height: Int = 0,
)

@Serializable
data class DynamicOpus(
    @SerialName("title") val title: String? = null,
    @SerialName("summary") val summary: DynamicText,
    @SerialName("pics") val pictures: List<DynamicPicture> = emptyList(),
)

@Serializable
data class DynamicArticle(
    @SerialName("title") val title: String,
    @SerialName("desc") val description: String = "",
    @SerialName("covers") val covers: List<String> = emptyList(),
    @SerialName("jump_url") val jumpUrl: String,
)

@Serializable
data class DynamicPgc(
    @SerialName("epid") val episodeId: Long,
    @SerialName("title") val title: String,
    @SerialName("cover") val cover: String,
)

@Serializable
data class DynamicLinkCard(
    @SerialName("title") val title: String,
    @SerialName("cover") val cover: String = "",
    @SerialName("desc") val description: String = "",
    @SerialName("jump_url") val jumpUrl: String,
)

@Serializable
data class DynamicUnavailable(@SerialName("tips") val tips: String)

@Serializable
data class DynamicStat(
    @SerialName("forward") val forward: DynamicStatItem,
    @SerialName("comment") val comment: DynamicStatItem,
    @SerialName("like") val like: DynamicStatItem,
)

@Serializable
data class DynamicStatItem(
    @SerialName("count") val count: Long,
    @SerialName("status") val status: Boolean = false,
    @SerialName("forbidden") val forbidden: Boolean = false,
    @SerialName("hidden") val hidden: Boolean = false,
)
