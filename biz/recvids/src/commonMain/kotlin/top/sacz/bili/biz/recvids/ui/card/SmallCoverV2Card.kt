package top.sacz.bili.biz.recvids.ui.card

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.FeaturedPlayList
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.core.registry.rememberScreen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel
import top.sacz.bili.biz.recvids.model.SmallCoverV2Item
import top.sacz.bili.shared.common.ui.autoSkeleton
import top.sacz.bili.shared.navigation.SharedScreen

@Composable
fun EmptyCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(215.dp)
            .autoSkeleton(true, CardDefaults.shape)
    ) {

    }
}

@Composable
fun VideoCard(video: SmallCoverV2Item,viewModel: VideoPlayerViewModel = viewModel()) {
    val navigator  = LocalNavigator.currentOrThrow
    val videoScreen = rememberScreen(SharedScreen.VideoPlayer("https://xy59x47x230x45xy.mcdn.bilivideo.cn:8082/v1/resource/25795959394-1-100050.m4s?agrr=1&build=0&buvid=XY4575A4A96E1002D541664F99E74AD701088&bvc=vod&bw=1700069&deadline=1746531270&dl=0&e=ig8euxZM2rNcNbdlhoNvNC8BqJIzNbfqXBvEqxTEto8BTrNvN0GvT90W5JZMkX_YN0MvXg8gNEV4NC8xNEV4N03eN0B5tZlqNxTEto8BTrNvNeZVuJ10Kj_g2UB02J0mN0B5tZlqNCNEto8BTrNvNC7MTX502C8f2jmMQJ6mqF2fka1mqx6gqj0eN0B599M%3D&f=u_0_0&gen=playurlv3&mcdnid=50018194&mid=479396940&nbs=1&nettype=0&og=hw&oi=3059343394&orderid=0%2C3&os=mcdn&platform=pc&sign=eda56a&tag=&traceid=trJRxEBaiUmtAY_0_e_N&uipk=5&uparams=e%2Ctrid%2Cdeadline%2Cnbs%2Cplatform%2Cos%2Cog%2Cmid%2Ctag%2Cuipk%2Coi%2Cgen&upsig=b0e0ae433fe8f3769b98b5d800727650"))
    //首先就是一个圆角卡片背景
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(225.dp)
            .clickable {
                navigator.push(videoScreen)
            }
    ) {
        //上半部分 封面
        Box(modifier = Modifier.fillMaxWidth().height(145.dp)) {
            //封面图像
            AsyncImage(
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.fillMaxSize(),
                model = "${video.cover}@10q.webp",
                contentDescription = video.title,
            )
            //用来做底部渐黑的效果
            Box(
                modifier = Modifier.fillMaxWidth().height(30.dp)
                    .align(Alignment.BottomStart)
                    .background(
                        Brush.verticalGradient(
                            0f to Color.Transparent,
                            1f to Color.Black.copy(alpha = 0.4f)
                        )
                    )
            )

            Row(
                modifier = Modifier.fillMaxWidth()
                    .height(20.dp)
                    .padding(horizontal = 5.dp)
                    .align(Alignment.BottomStart),
                verticalAlignment = Alignment.CenterVertically
            ) {
                CoverInfoBar(video)
            }
        }
        //下半部分 各种文字信息
        //视频标题
        VideoInfoBar(video)
    }
}

@Composable
private fun CoverInfoBar(video: SmallCoverV2Item) {
    //图标变白色
    Icon(
        imageVector = Icons.Outlined.SmartDisplay,
        contentDescription = null,
        modifier = Modifier.size(15.dp),
        tint = Color.White,
    )
    Text(
        text = video.coverLeftText1,
        fontSize = 11.sp,
        style = TextStyle(color = Color.White)
    )

    //间距
    Spacer(Modifier.width(5.dp))

    Icon(
        imageVector = Icons.AutoMirrored.Outlined.FeaturedPlayList,
        contentDescription = null,
        modifier = Modifier.size(15.dp),
        tint = Color.White,
    )
    Text(
        text = video.coverLeftText2,
        fontSize = 11.sp,
        style = TextStyle(color = Color.White)
    )
    //视频时长位于最右侧
    Text(
        text = video.coverRightText,
        fontSize = 11.sp,
        style = TextStyle(color = Color.White),
        modifier = Modifier.fillMaxWidth().wrapContentWidth(Alignment.End)
    )

}

@Composable
private fun VideoInfoBar(video: SmallCoverV2Item) {
    Spacer(Modifier.height(10.dp))
    //标题
    Text(
        text = video.title,
        maxLines = 2,
        minLines = 2,
        fontSize = 14.sp,
        lineHeight = 15.sp,
        overflow = TextOverflow.Ellipsis,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.fillMaxWidth().padding(horizontal = 5.dp)
    )
    //最底下的up主等信息
    Row(
        modifier = Modifier.fillMaxSize().padding(horizontal = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val upColor = if (isSystemInDarkTheme()) Color.LightGray else Color.DarkGray
        if (video.rCmdReasonStyle != null) {
            val cmdReasonStyle = video.rCmdReasonStyle
            Text(
                text = cmdReasonStyle.text,
                fontSize = 12.sp,
                modifier = Modifier.background(
                    shape = RoundedCornerShape(2.dp),
                    color = MaterialTheme.colorScheme.secondaryContainer
                ).padding(vertical = 1.dp, horizontal = 3.dp)
            )
        } else {
            Icon(
                imageVector = Icons.Rounded.Person,
                contentDescription = null,
                modifier = Modifier.size(18.dp),
                tint = upColor
            )
        }
        Spacer(Modifier.width(5.dp))
        Text(
            text = video.args.upName,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = upColor ,
            style = MaterialTheme.typography.headlineSmall,
            fontSize = 12.sp
        )
    }
}

