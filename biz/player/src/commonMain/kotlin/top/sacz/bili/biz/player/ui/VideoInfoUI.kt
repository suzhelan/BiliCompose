package top.sacz.bili.biz.player.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardControlKey
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
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
import top.sacz.bili.biz.player.model.VideoInfo
import top.sacz.bili.biz.player.viewmodel.VideoPlayerViewModel
import top.sacz.bili.biz.user.entity.UserCard
import top.sacz.bili.biz.user.viewmodel.UserViewModel
import top.sacz.bili.shared.common.ui.ProvideContentColor
import top.sacz.bili.shared.common.ui.card.Expandable
import top.sacz.bili.shared.common.ui.shimmerEffect
import top.sacz.bili.shared.common.ui.theme.TipTextColor
import top.sacz.bili.shared.common.util.TimeUtils
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
private fun VideoDetailsUI(
    videoInfo: VideoInfo,
    userViewModel: UserViewModel = viewModel(),
    viewModel: VideoPlayerViewModel = viewModel()
) {
    val userCard by userViewModel.userCard.collectAsState()
    LaunchedEffect(videoInfo) {
        userViewModel.getUserInfo(mid = videoInfo.owner.mid)
    }
    //作者卡片在最上方
    AuthorItemUI(userCard, userViewModel)
    //然后是一个可以展开的视频基本信息,包含标题,播放量,弹幕数量,发布时间,n人正在看
    VideoBasicInfoUI(videoInfo)
    //然后是点赞 播放量 评论量等信息

}

@Composable
private fun VideoBasicInfoUI(videoInfo: VideoInfo, viewModel: VideoPlayerViewModel = viewModel()) {
    //正在观看的人数
    val onlineCountText by viewModel.onlineCountText.collectAsState()
    LaunchedEffect(videoInfo) {
        viewModel.getVideoOnlineCountText(videoInfo.aid, videoInfo.cid)
    }
    val iconSize = 15.dp
    val textSize = 13.sp
    //可点击展开与收起的布局
    Expandable(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 10.dp),
        title = { isExpanded ->
            //视频标题
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                //标题,粗体+大字体
                Text(
                    text = videoInfo.title,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f),
                    overflow = if (isExpanded) TextOverflow.Visible else TextOverflow.Ellipsis,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1
                )
                //上下箭头
                Icon(
                    imageVector = if (isExpanded) Icons.Outlined.KeyboardControlKey else Icons.Outlined.KeyboardArrowDown,
                    contentDescription = null,
                    tint = TipTextColor,
                )
            }
            ProvideContentColor(
                color = TipTextColor
            ) {
                //基本指标
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    //播放数量
                    Icon(
                        imageVector = Icons.Outlined.SmartDisplay,
                        contentDescription = "ViewCount",
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = videoInfo.stat.view.toStringCount(),
                        fontSize = textSize,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    //弹幕数
                    Icon(
                        imageVector = Icons.Outlined.Subtitles,
                        contentDescription = "DanmakuCount",
                        modifier = Modifier.size(iconSize)
                    )
                    Text(
                        text = videoInfo.stat.danmaku.toStringCount(),
                        fontSize = textSize,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    //时间
                    Text(
                        text = TimeUtils.formatTime(videoInfo.pubdate.toLong()),
                        fontSize = textSize
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    //在线观看人数
                    Text(
                        text = onlineCountText,
                        fontSize = textSize
                    )
                }
            }
        },
        content = {
            //展开后的内容
            //BVID
            Text(
                text = videoInfo.bvid,
                color = TipTextColor,
                fontSize = textSize
            )
            //视频简介
            Text(
                text = videoInfo.desc,
                color = TipTextColor,
                fontSize = textSize
            )

        }
    )

}

private fun playbackDataUI(videoInfo: VideoInfo) {
}

@Composable
private fun AuthorItemUI(
    userCardResponse: BiliResponse<UserCard>,
    viewModel: UserViewModel
) = ConstraintLayout(
    modifier = Modifier.fillMaxWidth()
        .height(80.dp)
        .padding(10.dp)
        .clickable {
            //跳转到用户主页
        }
) {

    if (!userCardResponse.isSuccess()) {
        return@ConstraintLayout
    }
    val actionState by viewModel.actionState.collectAsState()
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
    if (actionState.isLoading()) {
        CircularProgressIndicator(modifier = Modifier.size(20.dp).constrainAs(concern) {
            top.linkTo(avatar.top)
            end.linkTo(parent.end)
            bottom.linkTo(avatar.bottom)
        })
    } else if (userCard.following) {
        //最右边添加一个点击关注的按钮
        FilledTonalButton(
            onClick = {
                viewModel.unfollow(userCard.card.mid.toLong())
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
            Icon(imageVector = Icons.Outlined.Menu, contentDescription = "unfollow")
            Text(text = "已关注", fontSize = 12.sp)
        }
    } else {
        OutlinedButton(
            onClick = {
                viewModel.follow(userCard.card.mid.toLong())
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
                }
        ) {
            Icon(imageVector = Icons.Outlined.Add, contentDescription = "addFollow")
            Text(text = "关注", fontSize = 12.sp)
        }
    }


}