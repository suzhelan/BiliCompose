package top.sacz.bili.shared.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.shared.common.base.BaseViewModel

/**
 * 常规脚手架布局
 */
@Composable
inline fun <reified VM : BaseViewModel> CommonComposeUI(
    isNeedLoading: Boolean = false,
    crossinline initAction: (vm: VM) -> Unit = {},
    crossinline topBar: @Composable (vm: VM) -> Unit = {},
    crossinline bottomBar: @Composable (vm: VM) -> Unit = {},
    crossinline floatActionButton: @Composable (vm: VM) -> Unit = {},
    crossinline content: @Composable BoxScope.(vm: VM) -> Unit
) {
    val vm: VM = viewModel()
    //初始化动作 其中操作要在viewmodel执行
    LaunchedEffect(Unit) {
        initAction(vm)
    }
    //脚手架布局
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { topBar(vm) },
        bottomBar = { bottomBar(vm) },
        floatingActionButton = { floatActionButton(vm) },
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            content(vm)
        }
    }
}

/**
 * 标准的标题栏布局
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleUI(
    title: String, onClickBack: () -> Unit
) {
    TopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onClickBack()
                }
            ) {
                Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "Back")
            }
        }
    )
}

/**
 * 居中版的标题栏布局
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CenterTitleUI(
    title: String,
    onClickBack: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(text = title)
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onClickBack()
                }
            ) {
                Icon(imageVector = Icons.Outlined.ArrowBackIosNew, contentDescription = "Back")
            }
        }
    )
}