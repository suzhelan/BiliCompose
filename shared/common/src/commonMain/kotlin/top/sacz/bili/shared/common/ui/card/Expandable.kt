package top.sacz.bili.shared.common.ui.card

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun ExpandableItemView(
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit,
    isExpanded: Boolean = false,
    content: @Composable () -> Unit
) {
    var isExpandedState by remember {
        mutableStateOf(isExpanded)
    }
    Column(
        modifier = modifier
            .clickable {
                isExpandedState = !isExpandedState
            }
            .animateContentSize() // 关键修饰符
    ) {
        // 标题
        title()
        // 动态内容部分
        if (isExpandedState) {
            content()
        }
    }
}