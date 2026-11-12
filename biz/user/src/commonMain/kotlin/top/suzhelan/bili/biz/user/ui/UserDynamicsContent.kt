package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Reply
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.suzhelan.bili.biz.user.entity.DynamicPicture
import top.suzhelan.bili.biz.user.entity.DynamicText
import top.suzhelan.bili.biz.user.entity.UserDynamic
import top.suzhelan.bili.biz.user.entity.secureImageUrl
import top.suzhelan.bili.biz.user.viewmodel.DynamicDetailViewModel
import top.suzhelan.bili.biz.user.viewmodel.UserDynamicsViewModel
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.ui.CommentContent
import top.suzhelan.bili.shared.common.ui.AppScaffold
import top.suzhelan.bili.shared.common.ui.TitleUI
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@Composable
fun UserDynamicsContent(
    mid: Long,
    viewModel: UserDynamicsViewModel,
    listState: LazyListState,
    isActive: Boolean,
    showInitialIndicator: Boolean,
) {
    val state by viewModel.dynamics.state.collectAsStateWithLifecycle()
    LaunchedEffect(mid, isActive) {
        if (isActive) viewModel.load(mid)
    }
    ProfilePagedList(
        state = state,
        listState = listState,
        isActive = isActive,
        onRetry = { viewModel.load(mid, force = true) },
        onLoadMore = viewModel.dynamics::loadMore,
        emptyText = "暂无动态",
        key = { it.id },
        header = {},
        showInitialIndicator = showInitialIndicator,
    ) { DynamicCard(it) }
}

@Composable
fun DynamicDetailScreen(id: String) {
    key(id) {
        val vm = viewModel { DynamicDetailViewModel() }
        val state by vm.detail.state.collectAsStateWithLifecycle()
        val navigation = LocalNavigation.currentOrThrow
        LifecycleResumeEffect(id) {
            vm.load(id, force = true)
            onPauseOrDispose { }
        }
        AppScaffold(topBar = { TitleUI("动态详情") { navigation.pop() } }) {
            val dynamic = state.items.firstOrNull()
            val header: @Composable () -> Unit = {
                if (state.error != null) Column(Modifier.fillMaxWidth().padding(16.dp)) {
                    Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = { vm.load(id, force = true) }) { Text("重试") }
                }
                if (dynamic != null) DynamicContent(dynamic)
                else if (state.error == null) Box(
                    Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center
                ) { CircularProgressIndicator() }
            }
            val commentType =
                CommentSourceType.entries.find { it.type == dynamic?.basic?.commentType }
            if (dynamic != null && commentType != null && dynamic.basic.commentId.isNotBlank() &&
                dynamic.modules.stat?.comment?.forbidden != true && dynamic.modules.stat?.comment?.hidden != true
            ) {
                CommentContent(dynamic.basic.commentId, commentType, header = {
                    item(key = "dynamic-content") { header() }
                    item(key = "comment-title") {
                        Text("评论", Modifier.padding(16.dp), fontWeight = FontWeight.Medium)
                    }
                })
            } else {
                LazyColumn(Modifier.fillMaxSize()) { item { header() } }
            }
        }
    }
}

@Composable
private fun DynamicCard(dynamic: UserDynamic) {
    val navigation = LocalNavigation.currentOrThrow
    Card(
        onClick = { navigation.push(SharedScreen.DynamicDetail(dynamic.id)) },
        modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 6.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow),
    ) {
        DynamicContent(dynamic)
    }
}

@Composable
private fun DynamicContent(dynamic: UserDynamic) {
    Column(
        Modifier.fillMaxWidth().padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        dynamic.modules.tag?.let { Text(it.text, color = ColorPrimary, fontSize = 12.sp) }
        DynamicBody(dynamic)
        dynamic.modules.stat?.let { stat ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Reply,
                        "转发",
                        Modifier.size(18.dp),
                        tint = TipColor
                    )
                    Text(stat.forward.count.toStringCount(), color = TipColor, fontSize = 12.sp)
                }
                if (!stat.comment.hidden) Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        Icons.AutoMirrored.Outlined.Chat,
                        "评论",
                        Modifier.size(18.dp),
                        tint = TipColor
                    )
                    Text(stat.comment.count.toStringCount(), color = TipColor, fontSize = 12.sp)
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val color = if (stat.like.status) ColorPrimary else TipColor
                    Icon(Icons.Outlined.ThumbUp, "点赞", Modifier.size(18.dp), tint = color)
                    Text(stat.like.count.toStringCount(), color = color, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
private fun DynamicBody(dynamic: UserDynamic) {
    val navigation = LocalNavigation.currentOrThrow
    val content = dynamic.modules.content
    val major = content.major
    dynamic.modules.author?.let { author ->
        Row(
            Modifier.fillMaxWidth().then(
                if (author.type == "AUTHOR_TYPE_NORMAL") Modifier.clickable {
                    navigation.push(SharedScreen.UserProfile(author.mid))
                } else Modifier
            ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            AsyncImage(author.face.secureImageUrl(), null, Modifier.size(40.dp).clip(CircleShape))
            Column {
                Text(author.name, fontWeight = FontWeight.Medium)
                Text(
                    listOf(author.pubTime, author.pubAction).filter { it.isNotBlank() }
                        .joinToString(" · "),
                    color = TipColor, fontSize = 12.sp
                )
            }
        }
    }
    content.topic?.let { topic ->
        Text("# ${topic.name}", color = ColorPrimary, modifier = Modifier.clickable {
            navigation.push(SharedScreen.WebView(topic.jumpUrl.secureImageUrl()))
        })
    }
    major?.opus?.title?.let { Text(it, fontWeight = FontWeight.Bold) }
    (content.description ?: major?.opus?.summary)?.let { DynamicRichText(it) }
    val pictures = major?.draw?.items ?: major?.opus?.pictures.orEmpty()
    if (pictures.isNotEmpty()) DynamicPictures(pictures)
    (major?.archive ?: major?.season)?.let { video ->
        DynamicMediaCard(
            video.cover, video.title,
            listOf(video.duration, video.stat.play.takeIf { it.isNotBlank() }?.let { "$it 播放" })
                .filterNotNull().filter { it.isNotBlank() }.joinToString(" · ")
        ) { navigation.push(SharedScreen.VideoPlayer(aid = video.aid.content.toLong())) }
    }
    major?.pgc?.let { pgc ->
        DynamicMediaCard(pgc.cover, pgc.title) {
            navigation.push(SharedScreen.VideoPlayer(epid = pgc.episodeId.toString()))
        }
    }
    major?.article?.let { article ->
        DynamicMediaCard(
            article.covers.firstOrNull().orEmpty(),
            article.title,
            article.description
        ) {
            navigation.push(SharedScreen.WebView(article.jumpUrl.secureImageUrl()))
        }
    }
    (major?.common ?: major?.music ?: major?.courses)?.let { link ->
        DynamicMediaCard(link.cover, link.title, link.description) {
            navigation.push(SharedScreen.WebView(link.jumpUrl.secureImageUrl()))
        }
    }
    major?.unavailable?.let { Text(it.tips, color = TipColor) }
    if (dynamic.type == "DYNAMIC_TYPE_NONE" && major?.unavailable == null) Text(
        "原动态已失效",
        color = TipColor
    )
    if (major != null && major.type !in supportedMajorTypes) {
        TextButton(onClick = { navigation.push(SharedScreen.WebView("https://t.bilibili.com/${dynamic.id}")) }) {
            Text("查看此动态内容")
        }
    }
    dynamic.original?.let { original ->
        Card(
            onClick = { navigation.push(SharedScreen.DynamicDetail(original.id)) },
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(8.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHighest),
        ) {
            Column(
                Modifier.fillMaxWidth().padding(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) { DynamicBody(original) }
        }
    }
}

private val supportedMajorTypes = setOf(
    "MAJOR_TYPE_ARCHIVE", "MAJOR_TYPE_UGC_SEASON", "MAJOR_TYPE_DRAW", "MAJOR_TYPE_OPUS",
    "MAJOR_TYPE_ARTICLE", "MAJOR_TYPE_PGC", "MAJOR_TYPE_COMMON", "MAJOR_TYPE_MUSIC",
    "MAJOR_TYPE_COURSES", "MAJOR_TYPE_NONE",
)

@Composable
private fun DynamicRichText(content: DynamicText) {
    val navigation = LocalNavigation.currentOrThrow
    val text = buildAnnotatedString {
        if (content.nodes.isEmpty()) append(content.text)
        content.nodes.forEachIndexed { index, node ->
            when {
                node.emoji != null -> appendInlineContent(index.toString(), node.text)
                node.type == "RICH_TEXT_NODE_TYPE_AT" || node.jumpUrl.isNotBlank() -> withLink(
                    LinkAnnotation.Clickable(
                        index.toString(),
                        TextLinkStyles(SpanStyle(color = ColorPrimary))
                    ) {
                        if (node.type == "RICH_TEXT_NODE_TYPE_AT") {
                            navigation.push(SharedScreen.UserProfile(node.rid.toLong()))
                        } else navigation.push(SharedScreen.WebView(node.jumpUrl.secureImageUrl()))
                    }
                ) { append(node.text) }

                else -> append(node.text)
            }
        }
    }
    val inlineContent = content.nodes.mapIndexedNotNull { index, node ->
        node.emoji?.let { emoji ->
            val size = if (emoji.size == 1) 20.sp else 32.sp
            index.toString() to InlineTextContent(
                Placeholder(
                    size,
                    size,
                    PlaceholderVerticalAlign.Center
                )
            ) {
                AsyncImage(emoji.url.secureImageUrl(), node.text, Modifier.fillMaxSize())
            }
        }
    }.toMap()
    Text(text, inlineContent = inlineContent, fontSize = 15.sp, lineHeight = 24.sp)
}

@Composable
private fun DynamicMediaCard(
    cover: String,
    title: String,
    description: String = "",
    onClick: () -> Unit
) {
    Column(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant).clickable(onClick = onClick)
    ) {
        if (cover.isNotBlank()) AsyncImage(
            cover.secureImageUrl(), null,
            Modifier.fillMaxWidth().aspectRatio(16f / 9f), contentScale = ContentScale.Crop
        )
        Column(Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            if (description.isNotBlank()) Text(
                description,
                color = TipColor,
                fontSize = 12.sp,
                maxLines = 3
            )
        }
    }
}

@Composable
private fun DynamicPictures(pictures: List<DynamicPicture>) {
    var previewIndex by remember(pictures) { mutableStateOf<Int?>(null) }
    val columns = when (pictures.size) {
        1 -> 1; 2, 4 -> 2; else -> 3
    }
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        pictures.chunked(columns).forEachIndexed { row, group ->
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                group.forEachIndexed { column, picture ->
                    val ratio = if (pictures.size == 1 && picture.width > 0 && picture.height > 0)
                        (picture.width.toFloat() / picture.height).coerceIn(0.6f, 1.8f) else 1f
                    AsyncImage(
                        picture.src.secureImageUrl(), "动态图片",
                        Modifier.weight(1f).aspectRatio(ratio).heightIn(max = 480.dp)
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { previewIndex = row * columns + column },
                        contentScale = ContentScale.Crop
                    )
                }
                repeat(columns - group.size) { Spacer(Modifier.weight(1f)) }
            }
        }
    }
    previewIndex?.let { index ->
        Dialog(onDismissRequest = { previewIndex = null }) {
            val pager = rememberPagerState(initialPage = index, pageCount = { pictures.size })
            Column(Modifier.fillMaxWidth().background(Color.Black, RoundedCornerShape(12.dp))) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        "${pager.currentPage + 1} / ${pictures.size}",
                        Modifier.weight(1f),
                        color = Color.White
                    )
                    TextButton(onClick = { previewIndex = null }) {
                        Text(
                            "关闭",
                            color = Color.White
                        )
                    }
                }
                HorizontalPager(pager, Modifier.fillMaxWidth().weight(1f, fill = false)) { page ->
                    AsyncImage(
                        pictures[page].src.secureImageUrl(),
                        "原图",
                        Modifier.fillMaxWidth().heightIn(min = 240.dp, max = 600.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }
        }
    }
}
