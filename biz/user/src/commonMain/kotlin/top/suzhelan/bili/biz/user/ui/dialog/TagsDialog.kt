package top.suzhelan.bili.biz.user.ui.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.suzhelan.bili.api.registerStatusListener
import top.suzhelan.bili.biz.user.entity.RelationTags
import top.suzhelan.bili.biz.user.viewmodel.FollowListViewModel
import top.suzhelan.bili.shared.common.ext.dismiss
import top.suzhelan.bili.shared.common.ext.show
import top.suzhelan.bili.shared.common.ext.toFalse
import top.suzhelan.bili.shared.common.ext.toTrue
import top.suzhelan.bili.shared.common.ui.dialog.WarnDialog
import top.suzhelan.bili.shared.common.ui.theme.TipColor


/**
 * 保存用户到指定分组
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsDialog(
    vm: FollowListViewModel,
    onUpdate: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = false,
    )
    //获取所有分组
    val allTags = vm.tags.filter {
        it.tagid != -20 && it.tagid != 0
    }
    //获取用户所在的分组
    val userInTags by vm.userInTags.collectAsStateWithLifecycle()

    val isShowCreateTagDialog by vm.isShowCreateTagDialog.collectAsStateWithLifecycle()
    val isShowDeleteTagDialog by vm.isShowDeleteTagDialog.collectAsStateWithLifecycle()
    val isShowRenameTagDialog by vm.isShowRenameTagDialog.collectAsStateWithLifecycle()
    val renameTag by vm.renameTag.collectAsStateWithLifecycle()
    val deleteTag by vm.deleteTagId.collectAsStateWithLifecycle()
    if (isShowCreateTagDialog) {
        CreateTagDialog(
            vm = vm,
            onUpdate = {
                onUpdate()
            },
            onDismissRequest = {
                vm.isShowCreateTagDialog.dismiss()
            }
        )
    }
    if (isShowDeleteTagDialog) {
        WarnDialog(
            title = "删除分组",
            text = "该分组下还有用户,确定要删除该分组吗？删除后用户会被移至默认分组",
            confirmButtonText = "删除",
            onConfirmRequest = {
                vm.deleteTag(deleteTag)
                vm.isShowDeleteTagDialog.dismiss()
            },
            onDismissRequest = {
                vm.isShowDeleteTagDialog.dismiss()
            }
        )
    }
    if (isShowRenameTagDialog) {
        RenameTagDialog(
            tag = renameTag,
            onUpdate = { newTagName ->
                vm.renameTag(tagId = renameTag.tagid, tagName = newTagName) {
                    vm.queryTags()
                    vm.isShowRenameTagDialog.dismiss()
                }
            },
            onDismissRequest = {
                vm.isShowRenameTagDialog.dismiss()
            }
        )
    }

    ModalBottomSheet(
        modifier = Modifier.fillMaxHeight(),
        sheetState = sheetState,
        onDismissRequest = { onDismissRequest() }
    ) {
        userInTags.registerStatusListener {
            onSuccess {
                Column(
                    modifier = Modifier.fillMaxSize()
                        .padding(16.dp)
                ) {
                    //title
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.FormatListBulleted,
                            modifier = Modifier.size(25.dp),
                            contentDescription = "Menu"
                        )
                        Text(
                            text = "设置分组",
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        TextButton(
                            onClick = {
                                vm.isShowCreateTagDialog.toTrue()
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                            Text(text = "新建分组")
                        }
                        TextButton(
                            onClick = {
                                vm.saveUserInTagInfo {
                                    vm.isShowSettingTagsDialog.toFalse()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Save, contentDescription = "Save")
                            Text(text = "保存")
                        }
                    }
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        item {
                            Text(
                                text = "点击分组右侧的更多按钮可重命名或删除",
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                color = TipColor,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                        items(
                            items = allTags,
                            key = { tag -> tag.tagid }
                        ) { tag ->
                            TagItem(
                                tag = tag,
                                isChecked = vm.tagsCheckedMap[tag.tagid] ?: false,
                                onChecked = { checked ->
                                    vm.updateTagsCheckedMap(tag.tagid, checked)
                                },
                                onRename = { vm.showRenameTagDialog(tag) },
                                onDelete = {
                                    vm.hasUserInTag(tag.tagid) { hasUser ->
                                        if (hasUser) {
                                            vm.isShowDeleteTagDialog.show()
                                        } else {
                                            vm.deleteTag(tag.tagid)
                                        }
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.size(8.dp))
                        }
                    }
                }
            }
        }
    }
}

/**
 * 点击卡片选择分组，通过更多菜单管理分组。
 */
@Composable
private fun TagItem(
    tag: RelationTags,
    isChecked: Boolean,
    onChecked: (Boolean) -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit,
) {
    var menuExpanded by remember(tag.tagid) { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onChecked(!isChecked)
        }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f).padding(start = 8.dp, end = 8.dp)) {
                Text(
                    text = tag.name,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = tag.tip,
                    fontSize = 12.sp,
                    color = TipColor,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Checkbox(
                checked = isChecked,
                onCheckedChange = onChecked
            )
            if (tag.tagid != -10) {
                Box {
                    IconButton(onClick = { menuExpanded = true }) {
                        Icon(
                            imageVector = Icons.Outlined.MoreVert,
                            contentDescription = "管理分组：${tag.name}"
                        )
                    }
                    DropdownMenu(
                        expanded = menuExpanded,
                        onDismissRequest = { menuExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("重命名") },
                            leadingIcon = {
                                Icon(Icons.Outlined.Edit, contentDescription = null)
                            },
                            onClick = {
                                menuExpanded = false
                                onRename()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("删除") },
                            leadingIcon = {
                                Icon(Icons.Default.Delete, contentDescription = null)
                            },
                            onClick = {
                                menuExpanded = false
                                onDelete()
                            }
                        )
                    }
                }
            }
        }
    }
}


/**
 * 创建分组对话框
 */
@Composable
private fun CreateTagDialog(
    vm: FollowListViewModel,
    onUpdate: () -> Unit,
    onDismissRequest: () -> Unit
) {
    var tagName by remember {
        mutableStateOf("")
    }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = {
            Text(text = "新建分组")
        },
        text = {
            OutlinedTextField(
                value = tagName,
                onValueChange = {
                    tagName = it
                },
                label = {
                    Text(text = "分组名称")
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    vm.createTag(tagName = tagName) {
                        onUpdate()
                        onDismissRequest()
                    }
                }
            ) {
                Text(text = "创建")
            }
        }
    )
}


/**
 * 重命名分组对话框
 */
@Composable
private fun RenameTagDialog(
    tag: RelationTags,
    onUpdate: (newTagName: String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var tagName by remember {
        mutableStateOf(tag.name)
    }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = {
            Text(text = "修改分组名称")
        },
        text = {
            OutlinedTextField(
                value = tagName,
                onValueChange = {
                    tagName = it
                },
                label = {
                    Text(text = "分组名称")
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onUpdate(tagName)
                }
            ) {
                Text(text = "重命名")
            }
        }
    )
}
