package top.suzhelan.bili.shared.common.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBackIosNew
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * 页面通用骨架。
 *
 * 这个组件只负责布局和渲染，不持有或修改 ViewModel。页面负责收集自己的
 * `UiState`，并把需要展示的加载状态传入，避免重组时意外覆盖 ViewModel 状态。
 */
@Composable
fun AppScaffold(
    isLoading: Boolean = false,
    modifier: Modifier = Modifier,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    topBar: @Composable () -> Unit = {},
    bottomBar: @Composable () -> Unit = {},
    floatingActionButton: @Composable () -> Unit = {},
    content: @Composable BoxScope.() -> Unit,
) {
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = topBar,
        bottomBar = bottomBar,
        floatingActionButton = floatingActionButton,
        contentWindowInsets = contentWindowInsets,
    ) { padding ->
        Box(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            content()
            if (isLoading) {
                LoadingIndicator()
            }
        }
    }

}

/**
 * 标准的标题栏布局
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TitleUI(
    title: String,
    onClickBack: () -> Unit
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
