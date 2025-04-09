package top.sacz.biz.home.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.api.Response
import top.sacz.biz.home.viewmodel.FeedViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecommendedVideoPage() {
    var isRefreshing by remember {
        mutableStateOf(false)
    }
    val viewModel: FeedViewModel = viewModel()
    val videoListResponse by viewModel.recommendedLevelList.collectAsState()
    //首次打开本页面时
    LaunchedEffect(Unit) {
        viewModel.getFeed()
    }
    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = {
            isRefreshing = true
            viewModel.getFeed()
        },
        modifier = Modifier.fillMaxSize()
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            when (val video = videoListResponse) {
                is Response.Success -> {
                    isRefreshing = false
                    items(video.data.items.size) { index ->
                        VideoCard(video.data.items[index])
                    }
                }

                is Response.Error -> {
                    isRefreshing = false
                }

                is Response.Loading -> {
                    items(20) {
                        EmptyCard()
                    }
                }
            }
        }
    }
}