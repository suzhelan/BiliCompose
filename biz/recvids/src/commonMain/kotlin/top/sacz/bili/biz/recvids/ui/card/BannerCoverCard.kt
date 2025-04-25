package top.sacz.bili.biz.recvids.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import top.sacz.bili.biz.recvids.model.BannerCoverItem
import top.sacz.bili.biz.recvids.model.isStatic

//TODO banner太复杂 涉及到购物 网页 静态 视频业务 暂时先不做
@Composable
fun BannerCoverCard(bannerCoverItem: BannerCoverItem) {
    Box(modifier = Modifier.fillMaxWidth().height(225.dp).clip(CardDefaults.shape)) {

    }
}

data class BannerModel(
    val imageUrl: String,
    val contentDescription: String
)

@Composable
fun BannerCarouselWidget(
    banners: List<BannerCoverItem.BannerItem>,
    modifier: Modifier = Modifier
) {
    val pagerState = rememberPagerState(pageCount = {
        banners.size
    })

    Box(
        contentAlignment = Alignment.BottomCenter,
        modifier = modifier
    ) {
        HorizontalPager(
            state = pagerState,
            contentPadding = PaddingValues(horizontal = 16.dp),
            pageSpacing = 8.dp,
            verticalAlignment = Alignment.Top,
        ) { page ->
            val bannerItem = banners[page]
            val imageUrl = if(bannerItem.isStatic()) bannerItem.staticBanner.image else bannerItem.cover
            BannerImage(
                imageUrl = imageUrl,
                contentDescription = imageUrl
            )
        }
        Row(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            repeat(pagerState.pageCount) { iteration ->
                val color =
                    if (pagerState.currentPage == iteration) Color.DarkGray else Color.LightGray
                Box(
                    modifier = Modifier
                        .padding(2.dp)
                        .clip(CircleShape)
                        .background(color)
                        .size(8.dp)
                )
            }
        }
    }
}

@Composable
private fun BannerImage(
    imageUrl: String,
    contentDescription: String,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = imageUrl,
        contentDescription = contentDescription,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp)
            .clip(RoundedCornerShape(8.dp))
    )
}