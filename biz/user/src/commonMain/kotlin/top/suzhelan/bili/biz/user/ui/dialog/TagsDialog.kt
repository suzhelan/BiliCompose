package top.suzhelan.bili.biz.user.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FormatListBulleted
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Save
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import top.suzhelan.bili.api.registerStatusListener
import top.suzhelan.bili.biz.user.entity.RelationTags
import top.suzhelan.bili.biz.user.viewmodel.FollowListViewModel
import top.suzhelan.bili.shared.common.ext.dismiss
import top.suzhelan.bili.shared.common.ext.show
import top.suzhelan.bili.shared.common.ext.toFalse
import top.suzhelan.bili.shared.common.ext.toTrue
import top.suzhelan.bili.shared.common.ui.dialog.WarnDialog
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.TipTextColor


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
                                text = "向右滑动项可重命名,向左滑动可删除",
                                modifier = Modifier.fillMaxWidth().padding(8.dp),
                                color = TipTextColor,
                                fontSize = 12.sp,
                                maxLines = 1,
                                textAlign = TextAlign.Center
                            )
                        }
                        items(
                            items = allTags,
                            key = { tag -> tag.tagid }
                        ) { tag ->
                            TodoListItemWithAnimation(
                                tag = tag,
                                vm = vm,
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
 * 可左右滑的列表项
 *
 */
@Composable
private fun TodoListItemWithAnimation(
    tag: RelationTags,
    vm: FollowListViewModel,
    modifier: Modifier = Modifier,
) {
    //选中的状态
    val tagsCheckedMap: Map<Int, Boolean> = vm.tagsCheckedMap
    // 监听删除确认对话框状态，在对话框关闭时（取消删除）重置滑动状态
    val isShowDeleteTagDialog by vm.isShowDeleteTagDialog.collectAsStateWithLifecycle()
    //监听拖动状态
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart && tag.tagid != -10) {
                //删除
                vm.hasUserInTag(tag.tagid) { haUser ->
                    //有用户的话展示一下dialog
                    if (haUser) {
                        vm.isShowDeleteTagDialog.show()
                    } else {
                        //没有用户则直接删除
                        vm.deleteTag(tag.tagid)
                    }
                }
            }
            if (it == SwipeToDismissBoxValue.StartToEnd && tag.tagid != -10) {
                vm.showRenameTagDialog(tag)
            }
            when {
                tag.tagid == -10 -> false
                it == SwipeToDismissBoxValue.EndToStart && !isShowDeleteTagDialog -> true
                else -> false
            }
        },
        positionalThreshold = { fullSize ->
            fullSize.times(0.5f)
        },
    )

    LaunchedEffect(isShowDeleteTagDialog) {
        if (!isShowDeleteTagDialog) {
            // 对话框关闭时重置滑动状态
            swipeToDismissBoxState.reset()
        }
    }

    SwipeToDismissBox(
        state = swipeToDismissBoxState,
        modifier = modifier.fillMaxWidth()
            .wrapContentHeight()
            .clip(CardDefaults.shape),
        backgroundContent = {
            if (tag.tagid != -10) {
                when (swipeToDismissBoxState.dismissDirection) {
                    //从左到右
                    SwipeToDismissBoxValue.StartToEnd -> {
                        //重命名
                        Icon(
                            imageVector = Icons.Outlined.Edit,
                            contentDescription = "Remove item",
                            modifier = Modifier.fillMaxSize()
                                .background(
                                    lerp(
                                        Color.LightGray,
                                        ColorPrimary,
                                        swipeToDismissBoxState.progress
                                    )
                                )
                                .wrapContentSize(Alignment.CenterStart)
                                .padding(12.dp),
                            tint = Color.White
                        )
                    }
                    //从右到左
                    SwipeToDismissBoxValue.EndToStart -> {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Remove item",
                            modifier = Modifier.fillMaxSize()
                                .background(
                                    lerp(
                                        Color.LightGray,
                                        Color.Red,
                                        swipeToDismissBoxState.progress
                                    )
                                )
                                .wrapContentSize(Alignment.CenterEnd)
                                .padding(12.dp),
                            tint = Color.White
                        )
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        }
    ) {
        TagItem(
            tag = tag,
            isChecked = tagsCheckedMap[tag.tagid] ?: false,
            onChecked = { checked ->
                vm.updateTagsCheckedMap(tag.tagid, checked)
            }
        )
    }
}

/**
 * 实际显示分组的ItemUI
 */
@Composable
private fun TagItem(tag: RelationTags, isChecked: Boolean, onChecked: (Boolean) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = {
            onChecked(!isChecked)
        }
    ) {
        ConstraintLayout(
            modifier = Modifier.fillMaxWidth()
                .padding(8.dp)
        ) {
            val (checkboxRef, nameRef, tipRef) = createRefs()
            Text(
                text = tag.name,
                fontSize = 16.sp,
                //太长则省略
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(nameRef) {
                    start.linkTo(parent.start, 8.dp)
                    top.linkTo(parent.top)
                }
            )

            Text(
                text = tag.tip,
                fontSize = 12.sp,
                color = TipTextColor,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.constrainAs(tipRef) {
                    start.linkTo(nameRef.start)
                    top.linkTo(nameRef.bottom)
                    end.linkTo(checkboxRef.start, 20.dp)
                    width = Dimension.fillToConstraints
                }
            )

            Checkbox(
                checked = isChecked,
                onCheckedChange = {
                    onChecked(it)
                },
                modifier = Modifier.constrainAs(checkboxRef) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
            )
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