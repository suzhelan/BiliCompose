package top.suzhelan.bili.shared.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import top.suzhelan.bili.shared.common.ui.theme.ErrorColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier.fillMaxSize(), text: String = "加载中...") {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            strokeWidth = 10.dp, modifier = Modifier.size(100.dp).padding(10.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
fun PagerBottomIndicator(lazyPagingItems: LazyPagingItems<*>) {
    PagerBottomIndicator(loadState = lazyPagingItems.loadState.append) {
        lazyPagingItems.retry()
    }
}

@Composable
fun PagerBottomIndicator(loadState: LoadState, retry: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp), // 增加垂直间距
        contentAlignment = Alignment.Center
    ) {
        when (loadState) {
            is LoadState.Loading -> {
                CircularProgressIndicator(
                    modifier = Modifier.padding(16.dp)
                )
            }

            is LoadState.NotLoading -> {
                if (loadState.endOfPaginationReached) {
                    Text(text = "没有更多了...", color = TipColor, fontSize = 12.sp)
                }
            }

            is LoadState.Error -> {
                // 错误状态处理
                val error = loadState.error
                Button(
                    onClick = retry,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ErrorColor.errorContainer, // 错误容器色
                        contentColor = ErrorColor.onErrorContainer // 对比色
                    ),
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text("加载失败，点击重试 $error")
                }
            }
        }
    }
}