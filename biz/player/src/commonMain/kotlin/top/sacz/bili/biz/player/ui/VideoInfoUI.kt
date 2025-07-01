package top.sacz.bili.biz.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.isLoading
import top.sacz.bili.api.isSuccess
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.player.model.PlayerParams
import top.sacz.bili.biz.player.model.UserCard
import top.sacz.bili.biz.player.model.VideoInfo
import top.sacz.bili.biz.player.viewmodel.UserViewModel
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel
import top.sacz.bili.shared.common.ui.shimmerEffect
import top.sacz.bili.shared.common.util.toStringCount

@Composable
fun VideoInfoUI(playerParams: PlayerParams, viewModel: VideoPlayerViewModel = viewModel()) {
    val vmVideoInfo by viewModel.videoInfo.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getVideoInfo(
            avid = playerParams.avid,
            bvid = playerParams.bvid,
        )
    }
    vmVideoInfo.registerStatusListener {
        onLoading {
            Text(text = "加载中...", modifier = Modifier.fillMaxWidth().shimmerEffect())
        }
        onSuccess { data ->
            VideoDetailsUI(videoInfo = data)
        }
    }
}

@Composable
private fun VideoDetailsUI(videoInfo: VideoInfo, userViewModel: UserViewModel = viewModel()) {
    val userCard by userViewModel.userCard.collectAsState()
    LaunchedEffect(videoInfo) {
        userViewModel.getUserInfo(mid = videoInfo.owner.mid.toString())
    }
    //作者卡片在最上方
    AuthorItemUI(userCard)
    //然后是点赞 播放量 评论量等信息

}

private fun playbackDataUI(videoInfo: VideoInfo) {
}

@Composable
private fun AuthorItemUI(userCardResponse: BiliResponse<UserCard>) = ConstraintLayout(
    modifier = Modifier.fillMaxWidth()
        .height(80.dp)
        .padding(10.dp)
        .shimmerEffect(userCardResponse.isLoading()).clickable {
            //跳转到用户主页
        }
) {
    if (!userCardResponse.isSuccess()) {
        return@ConstraintLayout
    }
    val userCard = (userCardResponse as BiliResponse.Success).data
    val (avatar, name, sign, concern) = createRefs()
    AsyncImage(
        model = userCard.card.face,
        contentDescription = null,
        modifier = Modifier.size(40.dp)
            .clip(RoundedCornerShape(50.dp))
            .constrainAs(avatar) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                start.linkTo(parent.start)
            }
    )
    //名字
    Text(
        text = userCard.card.name,
        fontSize = 15.sp,
        modifier = Modifier.constrainAs(name) {
            top.linkTo(avatar.top)
            start.linkTo(avatar.end, 10.dp)
        }
    )
    //粉丝数量 视频数量
    Text(
        text = "${userCard.follower.toStringCount()}粉丝 ${userCard.archiveCount}视频",
        fontSize = 12.sp,
        modifier = Modifier.constrainAs(sign) {
            top.linkTo(name.bottom, (-5).dp)
            start.linkTo(name.start)
        }
    )
    //最右边添加一个点击关注的按钮
    FilledTonalButton(onClick = {

    },
        contentPadding = PaddingValues(vertical = 5.dp, horizontal = 5.dp),
        modifier = Modifier
            .size(
                height = 30.dp,
                width = 75.dp
            ).constrainAs(concern) {
        top.linkTo(avatar.top)
        end.linkTo(parent.end)
        bottom.linkTo(avatar.bottom)
    }) {
        Text(text = "+ 关注", fontSize = 12.sp)
    }
}