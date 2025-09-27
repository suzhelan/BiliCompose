package top.suzhelan.bili.biz.biliplayer.ui.dialog

import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.roundToInt


@Composable
fun SelectCoinCountDialog(
    onSelect: (count: Int) -> Unit,
    onDismissRequest: () -> Unit
) {
    var coinCount by remember {
        mutableIntStateOf(1)
    }
    AlertDialog(
        onDismissRequest = {
            onDismissRequest()
        },
        title = {
            Text(text = "请选择投币数量")
        },
        text = {
            SelectCoinContent(
                data = coinCounts,
                selectIndex = 0,
                visibleCount = 3,
                modifier = Modifier.height(200.dp),
                onSelect = { index, item ->
                    coinCount = item
                },
                item = {
                    CoinCountItem(it)
                }
            )
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onSelect(coinCount)
                    onDismissRequest()
                }
            ) {
                Text(text = "投币 $coinCount 个")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                }
            ) {
                Text(text = "取消")
            }
        }
    )
}

private val coinCounts = listOf(1, 2)

@Composable
private fun <T> SelectCoinContent(
    data: List<T>,
    selectIndex: Int = 0,
    visibleCount: Int = 3,
    modifier: Modifier = Modifier,
    onSelect: (index: Int, item: T) -> Unit,
    item: @Composable (item: T) -> Unit
) {
    BoxWithConstraints(modifier = modifier, propagateMinConstraints = true) {
        val density = LocalDensity.current
        val size = data.size
        val count = size * 10000
        val pickerHeight = maxHeight
        val pickerHeightPx = density.run { pickerHeight.toPx() }
        val pickerCenterLinePx = pickerHeightPx / 2
        val itemHeight = pickerHeight / visibleCount
        val itemHeightPx = pickerHeightPx / visibleCount
        val startIndex = count / 2
        val listState = rememberLazyListState(
            initialFirstVisibleItemIndex = startIndex - startIndex.floorMod(size) + selectIndex,
            initialFirstVisibleItemScrollOffset = ((itemHeightPx - pickerHeightPx) / 2).roundToInt(),
        )
        val layoutInfo by remember { derivedStateOf { listState.layoutInfo } }
        LazyColumn(
            modifier = Modifier,
            state = listState,
            flingBehavior = rememberSnapFlingBehavior(listState),
        ) {
            items(count) { index ->
                val currIndex = (index - startIndex).floorMod(size)
                val item = layoutInfo.visibleItemsInfo.find { it.index == index }
                var percent = 1f
                if (item != null) {
                    val itemCenterY = item.offset + item.size / 2
                    percent = if (itemCenterY < pickerCenterLinePx) {
                        itemCenterY / pickerCenterLinePx
                    } else {
                        1 - (itemCenterY - pickerCenterLinePx) / pickerCenterLinePx
                    }
                    if (!listState.isScrollInProgress
                        && item.offset < pickerCenterLinePx
                        && item.offset + item.size > pickerCenterLinePx
                    ) {
                        onSelect(currIndex, data[currIndex])
                    }
                }
                Box(
                    modifier = Modifier
                        .graphicsLayer {
                            alpha = 0.75f + 0.25f * percent
                            scaleX = 0.75f + 0.25f * percent
                            scaleY = 0.75f + 0.25f * percent
                            rotationX = (1 + (0.75f + 0.25f * percent)) * 180
                        }
                        .fillMaxWidth()
                        .height(itemHeight),
                    contentAlignment = Alignment.Center,
                ) {
                    item(data[currIndex])
                }
            }
        }
    }
}


@Composable
private fun CoinCountItem(coinCount: Int) {
    Text(
        text = coinCount.toString(),
        fontSize = 20.sp,
        modifier = Modifier.fillMaxWidth(),
        textAlign = TextAlign.Center
    )
}

private fun Int.floorMod(other: Int): Int = when (other) {
    0 -> this
    else -> this - floorDiv(other) * other
}
