package top.suzhelan.bili.biz.search.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.NorthWest
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import bilicompose.biz.search.generated.resources.Res
import bilicompose.biz.search.generated.resources.search_back
import bilicompose.biz.search.generated.resources.search_cancel
import bilicompose.biz.search.generated.resources.search_clear_history
import bilicompose.biz.search.generated.resources.search_clear_history_message
import bilicompose.biz.search.generated.resources.search_clear_history_title
import bilicompose.biz.search.generated.resources.search_clear_input
import bilicompose.biz.search.generated.resources.search_for
import bilicompose.biz.search.generated.resources.search_history
import bilicompose.biz.search.generated.resources.search_placeholder
import bilicompose.biz.search.generated.resources.search_remove_history
import bilicompose.biz.search.generated.resources.search_retry
import bilicompose.biz.search.generated.resources.search_suggestions_failed
import bilicompose.biz.search.generated.resources.search_title
import bilicompose.biz.search.generated.resources.search_trending
import bilicompose.biz.search.generated.resources.search_trending_failed
import bilicompose.biz.search.generated.resources.search_use_suggestion
import org.jetbrains.compose.resources.stringResource
import top.suzhelan.bili.biz.search.data.SearchHistoryStore
import top.suzhelan.bili.biz.search.viewmodel.SearchInputViewModel
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow

const val SEARCH_EDIT_KEY = "search_edit_keyword"

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun SearchInputScreen(
    editKeyword: String? = null,
    onEditConsumed: () -> Unit = {},
    model: SearchInputViewModel = viewModel { SearchInputViewModel() },
) {
    val navigation = LocalNavigation.currentOrThrow
    val history by model.history.collectAsStateWithLifecycle()
    val suggestions by model.suggestions.collectAsStateWithLifecycle()
    val trending by model.trending.collectAsStateWithLifecycle()
    var field by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue()) }
    val input = field.text
    var confirmClear by rememberSaveable { mutableStateOf(false) }
    var submitting by remember { mutableStateOf(false) }
    val focus = remember { FocusRequester() }
    val keyboard = LocalSoftwareKeyboardController.current

    LaunchedEffect(editKeyword) {
        if (editKeyword != null) {
            field = TextFieldValue(editKeyword, TextRange(editKeyword.length))
            onEditConsumed()
        }
    }
    // 返回输入页时恢复联想；离开时取消，避免后台继续请求。
    LifecycleResumeEffect(input) {
        model.updateInput(input)
        onPauseOrDispose { model.stopSuggestions() }
    }
    LifecycleResumeEffect(Unit) {
        submitting = false
        onPauseOrDispose { }
    }
    LaunchedEffect(Unit) { focus.requestFocus() }

    val submit: (String) -> Unit = { value ->
        val keyword = value.trim()
        if (keyword.isNotEmpty() && !submitting) {
            submitting = true
            field = TextFieldValue(keyword, TextRange(keyword.length))
            model.stopSuggestions()
            SearchHistoryStore.add(keyword)
            keyboard?.hide()
            navigation.push(SharedScreen.SearchResults(keyword))
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            navigationIcon = {
                IconButton(onClick = { keyboard?.hide(); navigation.pop() }) {
                    Icon(
                        Icons.AutoMirrored.Outlined.ArrowBack,
                        stringResource(Res.string.search_back)
                    )
                }
            },
            title = {
                TextField(
                    value = field,
                    onValueChange = { field = it },
                    placeholder = {
                        Text(
                            stringResource(Res.string.search_placeholder),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    },
                    singleLine = true,
                    shape = MaterialTheme.shapes.extraLarge,
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                        unfocusedIndicatorColor = androidx.compose.ui.graphics.Color.Transparent,
                    ),
                    trailingIcon = {
                        if (input.isNotEmpty()) IconButton(onClick = { field = TextFieldValue() }) {
                            Icon(
                                Icons.Outlined.Close,
                                stringResource(Res.string.search_clear_input)
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(onSearch = { submit(input) }),
                    modifier = Modifier.fillMaxWidth().focusRequester(focus).onPreviewKeyEvent {
                        // 中文等输入法处于组词阶段时，Enter 应先提交候选文字。
                        if (it.key == Key.Enter && it.type == KeyEventType.KeyDown && field.composition == null) {
                            submit(input); true
                        } else false
                    },
                )
            },
            actions = {
                TextButton(
                    onClick = { submit(input) },
                    enabled = input.isNotBlank() && !submitting
                ) {
                    Text(stringResource(Res.string.search_title))
                }
            },
        )
    }) { padding ->
        Box(
            Modifier.fillMaxSize().padding(padding).imePadding(),
            contentAlignment = Alignment.TopCenter
        ) {
            LazyColumn(
                Modifier.widthIn(max = 840.dp).fillMaxWidth(),
                contentPadding = PaddingValues(vertical = 12.dp)
            ) {
                if (input.isNotBlank()) {
                    item("submit") {
                        ListItem(
                            headlineContent = {
                                Text(
                                    stringResource(
                                        Res.string.search_for,
                                        input.trim()
                                    )
                                )
                            },
                            leadingContent = {
                                Icon(
                                    Icons.Outlined.Search,
                                    null,
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            },
                            modifier = Modifier.clickable { submit(input) },
                        )
                    }
                    if (suggestions.loading) item("loading") {
                        Box(
                            Modifier.fillMaxWidth().padding(20.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(Modifier.size(24.dp), strokeWidth = 2.dp)
                        }
                    }
                    items(suggestions.items, key = { "suggestion:${it.value}" }) { suggestion ->
                        ListItem(
                            headlineContent = { HighlightedSearchText(suggestion.name.ifBlank { suggestion.value }) },
                            leadingContent = { Icon(Icons.Outlined.Search, null) },
                            trailingContent = {
                                IconButton(onClick = {
                                    field = TextFieldValue(
                                        suggestion.value,
                                        TextRange(suggestion.value.length)
                                    )
                                    focus.requestFocus()
                                }) {
                                    Icon(
                                        Icons.Outlined.NorthWest,
                                        stringResource(Res.string.search_use_suggestion)
                                    )
                                }
                            },
                            modifier = Modifier.clickable { submit(suggestion.value) },
                        )
                    }
                    if (suggestions.failed) item("suggestion_error") {
                        Text(
                            stringResource(Res.string.search_suggestions_failed),
                            Modifier.padding(20.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    if (history.isNotEmpty()) {
                        item("history_header") {
                            Row(
                                Modifier.fillMaxWidth().padding(start = 20.dp, end = 8.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    stringResource(Res.string.search_history),
                                    Modifier.weight(1f),
                                    style = MaterialTheme.typography.titleMedium
                                )
                                TextButton(onClick = { confirmClear = true }) {
                                    Text(
                                        stringResource(
                                            Res.string.search_clear_history
                                        )
                                    )
                                }
                            }
                        }
                        item("history") {
                            FlowRow(
                                Modifier.padding(horizontal = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                history.forEach { keyword ->
                                    InputChip(
                                        selected = false,
                                        onClick = { submit(keyword) },
                                        label = {
                                            Text(
                                                keyword,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.widthIn(max = 220.dp)
                                            )
                                        },
                                        avatar = {
                                            Icon(
                                                Icons.Outlined.History,
                                                null,
                                                Modifier.size(18.dp)
                                            )
                                        },
                                        trailingIcon = {
                                            IconButton(
                                                onClick = { SearchHistoryStore.remove(keyword) },
                                                modifier = Modifier.size(32.dp)
                                            ) {
                                                Icon(
                                                    Icons.Outlined.Close,
                                                    stringResource(
                                                        Res.string.search_remove_history,
                                                        keyword
                                                    ),
                                                    Modifier.size(16.dp)
                                                )
                                            }
                                        },
                                    )
                                }
                            }
                            Spacer(Modifier.height(20.dp))
                        }
                    }
                    item("trending_header") {
                        Text(
                            stringResource(Res.string.search_trending),
                            Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    if (trending.loading && trending.items.isEmpty()) item("trending_loading") {
                        Box(
                            Modifier.fillMaxWidth().padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) { CircularProgressIndicator(Modifier.size(28.dp)) }
                    }
                    if (trending.failed) item("trending_error") {
                        Row(
                            Modifier.padding(horizontal = 20.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                stringResource(Res.string.search_trending_failed),
                                Modifier.weight(1f),
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            TextButton(onClick = model::loadTrending) { Text(stringResource(Res.string.search_retry)) }
                        }
                    }
                    items(
                        trending.items.size,
                        key = { "trending:${trending.items[it].keyword}" }) { index ->
                        val word = trending.items[index]
                        ListItem(
                            headlineContent = {
                                Text(
                                    plainSearchText(word.displayName.ifBlank { word.keyword }),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            },
                            leadingContent = {
                                Text(
                                    (index + 1).toString(),
                                    Modifier.width(24.dp),
                                    style = MaterialTheme.typography.titleMedium,
                                    color = if (index < 3) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            modifier = Modifier.clickable { submit(word.keyword) },
                        )
                    }
                }
            }
        }
    }
    if (confirmClear) AlertDialog(
        onDismissRequest = { confirmClear = false },
        title = { Text(stringResource(Res.string.search_clear_history_title)) },
        text = { Text(stringResource(Res.string.search_clear_history_message)) },
        confirmButton = {
            TextButton(onClick = {
                SearchHistoryStore.clear(); confirmClear = false
            }) { Text(stringResource(Res.string.search_clear_history)) }
        },
        dismissButton = {
            TextButton(onClick = {
                confirmClear = false
            }) { Text(stringResource(Res.string.search_cancel)) }
        },
    )
}
