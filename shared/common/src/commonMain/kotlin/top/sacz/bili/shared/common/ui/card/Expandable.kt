package top.sacz.bili.shared.common.ui.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier


@Composable
fun Expandable(
    modifier: Modifier = Modifier,
    title: @Composable ColumnScope.(isExpanded: Boolean) -> Unit,
    isExpanded: Boolean = false,
    content: @Composable ColumnScope.(isExpanded: Boolean) -> Unit
) {
    var isExpandedState by remember { mutableStateOf(isExpanded) }

    Column(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                isExpandedState = !isExpandedState
            }
    ) {
        title(isExpandedState)
        AnimatedVisibility(
            visible = isExpandedState,
            //可自定义动画 但是不需要 默认的就很好了
        ) {
            Column(
                modifier = Modifier.clickable {

                }
            ) {
                content(isExpandedState)
            }
        }
    }
}



