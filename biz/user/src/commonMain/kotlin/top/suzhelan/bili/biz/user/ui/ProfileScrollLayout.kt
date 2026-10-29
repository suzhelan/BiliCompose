package top.suzhelan.bili.biz.user.ui

import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.gestures.scrollable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollConnection
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import kotlin.math.roundToInt

class ProfileScrollState(initialOffset: Float = 0f) {
    var offset by mutableFloatStateOf(initialOffset)
        private set

    private var maxOffset = 0f

    fun updateBounds(headerHeight: Int, topBarHeight: Int) {
        maxOffset = (headerHeight - topBarHeight).coerceAtLeast(0).toFloat()
        offset = offset.coerceIn(0f, maxOffset)
    }

    // 返回实际消费的手势距离，剩余距离交给列表，边界处不吞掉滚动。
    fun consumeScroll(delta: Float): Float {
        val previousOffset = offset
        offset = (offset - delta).coerceIn(0f, maxOffset)
        return previousOffset - offset
    }

    companion object {
        val Saver = Saver<ProfileScrollState, Float>(
            save = { it.offset },
            restore = { ProfileScrollState(it) },
        )
    }
}

/**
 * 共享资料区和 Tab，只让正文在 Pager 内横向切换。
 * 外层只消费头部位移，不用 verticalScroll，正文始终以有限高度测量。
 */
@Composable
fun ProfileScrollLayout(
    state: ProfileScrollState,
    listState: LazyListState,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    tabs: @Composable () -> Unit,
    topBar: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    val currentListState by rememberUpdatedState(listState)
    val nestedScrollConnection = remember(state) {
        object : NestedScrollConnection {
            override fun onPreScroll(available: Offset, source: NestedScrollSource): Offset {
                // 上滑先收起头部；下拉只有在当前列表已到顶时才展开头部。
                return if (available.y < 0f || !currentListState.canScrollBackward) {
                    Offset(0f, state.consumeScroll(available.y))
                } else {
                    Offset.Zero
                }
            }

            override fun onPostScroll(
                consumed: Offset,
                available: Offset,
                source: NestedScrollSource,
            ): Offset {
                return if (available.y > 0f && !currentListState.canScrollBackward) {
                    Offset(0f, state.consumeScroll(available.y))
                } else {
                    Offset.Zero
                }
            }
        }
    }
    // 在封面、资料、Tab 上拖动时也走同一套 pre/post scroll 和 fling 分发。
    // 列表手势由 LazyColumn 处理，此处仅接续外层手势的剩余距离。
    val scrollableState = rememberScrollableState { delta ->
        -currentListState.dispatchRawDelta(-delta)
    }

    Layout(
        modifier = modifier.clipToBounds()
            .nestedScroll(nestedScrollConnection)
            .scrollable(scrollableState, Orientation.Vertical),
        content = {
            Box { header() }
            Box { tabs() }
            Box { content() }
            Box { topBar() }
        },
    ) { measurables, constraints ->
        val width = constraints.maxWidth
        val height = constraints.maxHeight
        val looseConstraints = constraints.copy(minWidth = width, minHeight = 0)
        val headerPlaceable = measurables[0].measure(
            looseConstraints.copy(maxHeight = Constraints.Infinity)
        )
        val tabsPlaceable = measurables[1].measure(looseConstraints)
        val topBarPlaceable = measurables[3].measure(looseConstraints)
        state.updateBounds(headerPlaceable.height, topBarPlaceable.height)

        val headerOffset = state.offset.roundToInt()
        val tabTop = (headerPlaceable.height - headerOffset)
            .coerceAtLeast(topBarPlaceable.height)
        val contentTop = tabTop + tabsPlaceable.height
        val contentPlaceable = measurables[2].measure(
            Constraints.fixed(width, (height - contentTop).coerceAtLeast(0))
        )
        layout(width, height) {
            headerPlaceable.placeRelative(0, -headerOffset)
            contentPlaceable.placeRelative(0, contentTop)
            tabsPlaceable.placeRelative(0, tabTop)
            topBarPlaceable.placeRelative(0, 0)
        }
    }
}
