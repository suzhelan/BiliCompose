package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.biz.user.viewmodel.FavoritePickerViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper

@Composable
fun FavoriteVideoDialog(
    aid: Long,
    visible: Boolean,
    onDismiss: () -> Unit,
    onSaved: (Boolean) -> Unit,
    onRequestLogin: () -> Unit,
) {
    if (!visible) return
    if (!LoginMapper.isLogin()) {
        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text("需要登录") },
            text = { Text("登录后才能收藏视频。") },
            confirmButton = {
                TextButton(onClick = { onDismiss(); onRequestLogin() }) { Text("去登录") }
            },
            dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
        )
        return
    }

    val vm = viewModel { FavoritePickerViewModel() }
    val state by vm.state.collectAsStateWithLifecycle()
    var showCreate by remember { mutableStateOf(false) }
    LaunchedEffect(aid) { vm.load(aid) }

    AlertDialog(
        onDismissRequest = { if (!state.isSaving) onDismiss() },
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("添加到收藏夹", modifier = Modifier.weight(1f))
                IconButton(enabled = !state.isSaving, onClick = { showCreate = true }) {
                    Icon(Icons.Outlined.Add, contentDescription = "新建收藏夹")
                }
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().heightIn(max = 420.dp)
                    .verticalScroll(rememberScrollState()),
            ) {
                when {
                    state.isLoading -> CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                    state.error != null -> Text(state.error ?: "加载失败")
                    state.folders.isEmpty() -> Text("暂无收藏夹，请先新建一个")
                    else -> state.folders.forEach { folder ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(
                                checked = folder.id in state.selectedIds,
                                enabled = !state.isSaving,
                                onCheckedChange = { vm.toggle(folder.id) },
                            )
                            Column(modifier = Modifier.weight(1f)) {
                                Text(folder.title)
                                Text("${folder.mediaCount} 个内容 · ${if (folder.isPrivate) "私密" else "公开"}")
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !state.isLoading && !state.isSaving && state.error == null,
                onClick = { vm.save { favored -> onSaved(favored); onDismiss() } },
            ) { Text(if (state.isSaving) "保存中…" else "保存") }
        },
        dismissButton = {
            TextButton(
                enabled = !state.isSaving,
                onClick = onDismiss
            ) { Text("取消") }
        },
    )

    if (showCreate) {
        CreateFavoriteFolderDialog(
            saving = state.isSaving,
            onDismiss = { showCreate = false },
            onCreate = { title, intro, private ->
                vm.createFolder(title, intro, private)
                showCreate = false
            },
        )
    }
}

@Composable
fun CreateFavoriteFolderDialog(
    saving: Boolean,
    onDismiss: () -> Unit,
    onCreate: (String, String, Boolean) -> Unit,
) {
    var title by remember { mutableStateOf("") }
    var intro by remember { mutableStateOf("") }
    var isPrivate by remember { mutableStateOf(false) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("新建收藏夹") },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it.take(80) },
                    label = { Text("名称") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.padding(4.dp))
                TextField(
                    value = intro,
                    onValueChange = { intro = it.take(200) },
                    label = { Text("简介（可选）") },
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text("设为私密", modifier = Modifier.weight(1f))
                    Switch(checked = isPrivate, onCheckedChange = { isPrivate = it })
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = title.isNotBlank() && !saving,
                onClick = { onCreate(title, intro, isPrivate) },
            ) { Text("创建") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("取消") } },
    )
}
