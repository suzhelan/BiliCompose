package top.suzhelan.bili.biz.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.ArrowDropDown
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.SearchOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import bilicompose.biz.search.generated.resources.Res
import bilicompose.biz.search.generated.resources.search_back
import bilicompose.biz.search.generated.resources.search_danmaku_order
import bilicompose.biz.search.generated.resources.search_duration_all
import bilicompose.biz.search.generated.resources.search_duration_extra_long
import bilicompose.biz.search.generated.resources.search_duration_long
import bilicompose.biz.search.generated.resources.search_duration_medium
import bilicompose.biz.search.generated.resources.search_duration_short
import bilicompose.biz.search.generated.resources.search_edit
import bilicompose.biz.search.generated.resources.search_empty
import bilicompose.biz.search.generated.resources.search_empty_hint
import bilicompose.biz.search.generated.resources.search_end
import bilicompose.biz.search.generated.resources.search_failed
import bilicompose.biz.search.generated.resources.search_fans_order
import bilicompose.biz.search.generated.resources.search_favorites
import bilicompose.biz.search.generated.resources.search_network_error
import bilicompose.biz.search.generated.resources.search_newest
import bilicompose.biz.search.generated.resources.search_plays
import bilicompose.biz.search.generated.resources.search_relevance
import bilicompose.biz.search.generated.resources.search_restricted
import bilicompose.biz.search.generated.resources.search_retry
import bilicompose.biz.search.generated.resources.search_users
import bilicompose.biz.search.generated.resources.search_videos
import org.jetbrains.compose.resources.stringResource
import top.suzhelan.bili.api.ApiException
import top.suzhelan.bili.biz.search.entity.SearchCategory
import top.suzhelan.bili.biz.search.entity.UserOrder
import top.suzhelan.bili.biz.search.entity.VideoDuration
import top.suzhelan.bili.biz.search.entity.VideoOrder
import top.suzhelan.bili.biz.search.viewmodel.SearchResultsViewModel
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(
    keyword: String,
    onEditKeyword: () -> Unit,
    model: SearchResultsViewModel = viewModel { SearchResultsViewModel(keyword) },
) {
    val navigation = LocalNavigation.currentOrThrow
    val videoSpec by model.videoSpec.collectAsStateWithLifecycle()
    val userSpec by model.userSpec.collectAsStateWithLifecycle()
    var category by rememberSaveable { mutableStateOf(SearchCategory.Video) }
    val videoScroll = rememberSaveable(videoSpec, saver = LazyListState.Saver) { LazyListState() }
    val userScroll = rememberSaveable(userSpec, saver = LazyListState.Saver) { LazyListState() }
    Scaffold(topBar = {
        Column {
            TopAppBar(
                title = {
                    val editDescription = stringResource(Res.string.search_edit)
                    Surface(
                        shape = MaterialTheme.shapes.extraLarge,
                        color = MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier.fillMaxWidth()
                            .semantics { contentDescription = editDescription }
                            .clickable(onClick = onEditKeyword),
                    ) {
                        Row(
                            Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Icon(
                                Icons.Outlined.Search,
                                null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                keyword,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navigation.pop() }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ArrowBack,
                            stringResource(Res.string.search_back)
                        )
                    }
                },
            )
            SecondaryTabRow(selectedTabIndex = category.ordinal) {
                SearchCategory.entries.forEach { item ->
                    Tab(selected = category == item, onClick = { category = item }, text = {
                        Text(stringResource(if (item == SearchCategory.Video) Res.string.search_videos else Res.string.search_users))
                    })
                }
            }
        }
    }) { padding ->
        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.TopCenter) {
            Column(Modifier.widthIn(max = 960.dp).fillMaxSize()) {
                Row(
                    Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (category == SearchCategory.Video) {
                        SearchFilterMenu(
                            videoOrderLabel(videoSpec.order),
                            videoSpec.order != VideoOrder.Relevance,
                            VideoOrder.entries,
                            { videoOrderLabel(it) },
                            model::setVideoOrder
                        )
                        SearchFilterMenu(
                            durationLabel(videoSpec.duration),
                            videoSpec.duration != VideoDuration.All,
                            VideoDuration.entries,
                            { durationLabel(it) },
                            model::setDuration
                        )
                    } else {
                        SearchFilterMenu(
                            userOrderLabel(userSpec.order),
                            userSpec.order != UserOrder.Relevance,
                            UserOrder.entries,
                            { userOrderLabel(it) },
                            model::setUserOrder
                        )
                    }
                }
                if (category == SearchCategory.Video) key(videoSpec) {
                    val flow = remember(videoSpec) { model.videoResults(videoSpec) }
                    val items = flow.collectAsLazyPagingItems()
                    SearchResultList(items, videoScroll, { it.aid }) { video ->
                        SearchVideoCard(
                            video,
                            onClick = {
                                navigation.push(
                                    SharedScreen.VideoPlayer(
                                        aid = video.aid,
                                        bvid = video.bvid.takeIf { it.isNotBlank() })
                                )
                            },
                            onAuthorClick = { navigation.push(SharedScreen.UserProfile(video.mid)) },
                        )
                    }
                } else key(userSpec) {
                    val flow = remember(userSpec) { model.userResults(userSpec) }
                    val items = flow.collectAsLazyPagingItems()
                    SearchResultList(items, userScroll, { it.mid }) { user ->
                        SearchUserCard(user) { navigation.push(SharedScreen.UserProfile(user.mid)) }
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> SearchFilterMenu(
    selectedLabel: String,
    selected: Boolean,
    options: List<T>,
    label: @Composable (T) -> String,
    onSelect: (T) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }
    Box {
        FilterChip(
            selected = selected,
            onClick = { expanded = true },
            label = { Text(selectedLabel) },
            trailingIcon = { Icon(Icons.Outlined.ArrowDropDown, null, Modifier.size(18.dp)) })
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(label(option)) },
                    onClick = { expanded = false; onSelect(option) })
            }
        }
    }
}

@Composable
private fun <T : Any> SearchResultList(
    items: LazyPagingItems<T>,
    state: LazyListState,
    stableKey: (T) -> Any,
    card: @Composable (T) -> Unit,
) {
    val refresh = items.loadState.refresh
    val empty =
        refresh is LoadState.NotLoading && items.loadState.append.endOfPaginationReached && items.itemCount == 0
    when {
        refresh is LoadState.Error && items.itemCount == 0 -> SearchFailure(
            refresh.error,
            items::retry,
            Modifier.fillMaxSize()
        )

        empty -> Column(
            Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.SearchOff,
                null,
                Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(16.dp))
            Text(
                stringResource(Res.string.search_empty),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(Modifier.height(8.dp))
            Text(
                stringResource(Res.string.search_empty_hint),
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        }

        items.itemCount == 0 -> Box(
            Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) { CircularProgressIndicator() }

        else -> LazyColumn(
            state = state,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            if (refresh is LoadState.Error) item("refresh_error") {
                SearchFailure(
                    refresh.error,
                    items::retry
                )
            }
            items(
                count = items.itemCount,
                key = items.itemKey(stableKey)
            ) { index -> items[index]?.let { card(it) } }
            item("footer") {
                when (val append = items.loadState.append) {
                    is LoadState.Loading -> Box(
                        Modifier.fillMaxWidth().padding(20.dp),
                        contentAlignment = Alignment.Center
                    ) { CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp) }

                    is LoadState.Error -> SearchFailure(append.error, items::retry)
                    is LoadState.NotLoading -> if (append.endOfPaginationReached) Text(
                        stringResource(Res.string.search_end),
                        Modifier.fillMaxWidth().padding(16.dp),
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
    }
}

@Composable
private fun SearchFailure(error: Throwable, onRetry: () -> Unit, modifier: Modifier = Modifier) {
    val restricted = error is ApiException && error.code in listOf(-412, -352, -403, 412)
    Column(
        modifier.fillMaxWidth().padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.search_failed), style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(if (restricted) Res.string.search_restricted else Res.string.search_network_error),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(12.dp))
        FilledTonalButton(onClick = onRetry) { Text(stringResource(Res.string.search_retry)) }
    }
}

@Composable
private fun videoOrderLabel(order: VideoOrder): String = stringResource(
    when (order) {
        VideoOrder.Relevance -> Res.string.search_relevance
        VideoOrder.Plays -> Res.string.search_plays
        VideoOrder.Newest -> Res.string.search_newest
        VideoOrder.Danmaku -> Res.string.search_danmaku_order
        VideoOrder.Favorites -> Res.string.search_favorites
    }
)

@Composable
private fun durationLabel(duration: VideoDuration): String = stringResource(
    when (duration) {
        VideoDuration.All -> Res.string.search_duration_all
        VideoDuration.UnderTen -> Res.string.search_duration_short
        VideoDuration.TenToThirty -> Res.string.search_duration_medium
        VideoDuration.ThirtyToSixty -> Res.string.search_duration_long
        VideoDuration.OverSixty -> Res.string.search_duration_extra_long
    }
)

@Composable
private fun userOrderLabel(order: UserOrder): String = stringResource(
    if (order == UserOrder.Relevance) Res.string.search_relevance else Res.string.search_fans_order
)
