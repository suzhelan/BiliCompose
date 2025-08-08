package top.sacz.bili.biz.user.ui.dialog

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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.user.entity.RelationTags
import top.sacz.bili.biz.user.viewmodel.FollowListViewModel
import top.sacz.bili.shared.common.ext.toFalse
import top.sacz.bili.shared.common.ext.toTrue
import top.sacz.bili.shared.common.ui.dialog.WarnDialog
import top.sacz.bili.shared.common.ui.theme.TipTextColor


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
    val userInTags by vm.userInTags.collectAsState()
    //选中的状态
    val tagsCheckedMap: Map<Int, Boolean> = vm.tagsCheckedMap

    val isShowCreateTagDialog by vm.isShowCreateTagDialog.collectAsState()
    val isShowDeleteTagDialog by vm.isShowDeleteTagDialog.collectAsState()
    val deleteTag by vm.deleteTagId.collectAsState()
    if (isShowCreateTagDialog) {
        CreateTagDialog(
            vm = vm,
            onUpdate = {
                onUpdate()
            },
            onDismissRequest = {
                vm.closeCreateTagDialog()
            }
        )
    }
    if (isShowDeleteTagDialog) {
        WarnDialog(
            title = "删除分组",
            text = "该分组下还有用户,确定要删除该分组吗？",
            confirmButtonText = "删除",
            onConfirmRequest = {
                vm.deleteTag(deleteTag)
                vm.isShowDeleteTagDialog.toFalse()
            },
            onDismissRequest = {
                vm.isShowDeleteTagDialog.toFalse()
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
                                vm.openCreateTagDialog()
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Add, contentDescription = "Add")
                            Text(text = "新建分组")
                        }
                        TextButton(
                            onClick = {
                                vm.saveUserInTagInfo {
                                    vm.closeSettingTagsDialog()
                                }
                            }
                        ) {
                            Icon(imageVector = Icons.Outlined.Save, contentDescription = "Save")
                            Text(text = "保存")
                        }
                    }
                    Spacer(modifier = Modifier.size(8.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(
                            items = allTags,
                            key = { tag -> tag.tagid }
                        ) { tag ->
                            TodoListItemWithAnimation(
                                tag = tag,
                                isChecked = tagsCheckedMap[tag.tagid] ?: false,
                                onUpdate = { checked ->
                                    vm.updateTagsCheckedMap(tag.tagid, checked)
                                },
                                onRemove = {
                                    //删除
                                    vm.hasUserInTag(tag.tagid) { haUser ->
                                        //有用户的话展示一下dialog
                                        if (haUser) {
                                            vm.isShowDeleteTagDialog.toTrue()
                                        } else {
                                            //没有用户则直接删除
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
 * 可左右滑的列表项
 */
@Composable
fun TodoListItemWithAnimation(
    tag: RelationTags,
    isChecked: Boolean,
    onUpdate: (Boolean) -> Unit,
    onRemove: (RelationTags) -> Unit,
    modifier: Modifier = Modifier,
) {

    //监听拖动状态
    val swipeToDismissBoxState = rememberSwipeToDismissBoxState(
        confirmValueChange = {
            if (it == SwipeToDismissBoxValue.EndToStart && tag.tagid != -10) onRemove(tag)
            it == SwipeToDismissBoxValue.EndToStart && tag.tagid != -10//松手还原状态
        },
        positionalThreshold = { fullSize ->
            fullSize.times(0.5f)
        },
    )

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
                        //什么都没有
                    }
                    //从右到左
                    SwipeToDismissBoxValue.EndToStart -> {
                        if (tag.tagid != -10) {
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
                    }

                    SwipeToDismissBoxValue.Settled -> {}
                }
            }
        }
    ) {
        TagItem(
            tag = tag,
            isChecked = isChecked,
            onChecked = {
                onUpdate(it)
            }
        )
    }
}