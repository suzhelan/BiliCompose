package top.sacz.bili.biz.biliplayer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.KeyboardControlKey
import androidx.compose.material.icons.outlined.Menu
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.SmartDisplay
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material.icons.outlined.Subtitles
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.Toll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import coil3.compose.AsyncImage
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.api.isLoading
import top.sacz.bili.api.isSuccess
import top.sacz.bili.api.registerStatusListener
import top.sacz.bili.biz.biliplayer.entity.PlayerParams
import top.sacz.bili.biz.biliplayer.entity.VideoInfo
import top.sacz.bili.biz.biliplayer.ui.dialog.SelectCoinCountDialog
import top.sacz.bili.biz.biliplayer.ui.item.SimpleVideoCard
import top.sacz.bili.biz.biliplayer.viewmodel.VideoPlayerViewModel
import top.sacz.bili.biz.user.entity.UserCard
import top.sacz.bili.biz.user.viewmodel.UserViewModel
import top.sacz.bili.shared.common.ext.dismiss
import top.sacz.bili.shared.common.ext.show
import top.sacz.bili.shared.common.ui.ProvideContentColor
import top.sacz.bili.shared.common.ui.card.Expandable
import top.sacz.bili.shared.common.ui.shimmerEffect
import top.sacz.bili.shared.common.ui.theme.ColorPrimary
import top.sacz.bili.shared.common.ui.theme.TextColor
import top.sacz.bili.shared.common.ui.theme.TipTextColor
import top.sacz.bili.shared.common.util.TimeUtils
import top.sacz.bili.shared.common.util.toStringCount

@Composable
fun VideoInfoUI(playerParams: PlayerParams, viewModel: VideoPlayerViewModel) {
    val vmVideoInfo by viewModel.videoDetailsInfo.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getVideoDetailsInfo(
            avid = playerParams.avid,
            bvid = playerParams.bvid,
        )
    }
    vmVideoInfo.registerStatusListener {
        onLoading {
            Text(text = "加载中...", modifier = Modifier.fillMaxWidth().shimmerEffect())
        }
        onSuccess { data ->
            VideoDetailsUI(videoInfo = data, viewModel = viewModel)
        }
    }
}

@Composable
private fun VideoDetailsUI(
    videoInfo: VideoInfo,
    viewModel: VideoPlayerViewModel,
    userViewModel: UserViewModel = viewModel(),
) {
    val userCard by userViewModel.userCard.collectAsState()
    LaunchedEffect(Unit) {
        userViewModel.getUserInfo(mid = videoInfo.owner.mid)
    }
    //Dialog
    Dialogs(aid = videoInfo.aid, viewModel = viewModel)
    //作者卡片在最上方
    AuthorItemUI(userCard, userViewModel)
    //然后是一个可以展开的视频基本信息,包含标题,播放量,弹幕数量,发布时间,n人正在看
    VideoBasicInfoUI(videoInfo, viewModel)
    //然后是点赞 播放量 评论量等信息
    BasicIndicatorsUI(videoInfo, viewModel)
    //最后是推荐视频
    RecommendedVideoUI(videoInfo.aid, viewModel)
}

@Composable
private fun Dialogs(aid: Long, viewModel: VideoPlayerViewModel) {
    val isShowAddCoinDialog by viewModel.isShowAddCoinDialog.collectAsState()
    val coinQuotationCount by viewModel.coinQuotationCount.collectAsState()
    if (isShowAddCoinDialog) {
        if (coinQuotationCount >= 2) {
            viewModel.showMessageDialog("提示", "您已经投过币了,请勿重复投币")
            viewModel.isShowAddCoinDialog.dismiss()
        } else {
            SelectCoinCountDialog(
                onSelect = { count ->
                    viewModel.addCoin(
                        aid = aid,
                        multiply = count
                    )
                },
                onDismissRequest = {
                    viewModel.isShowAddCoinDialog.dismiss()
                }
            )
        }
    }
}

/**
 * 标题+描述+基本数据指标+标签
 */
@Composable
private fun VideoBasicInfoUI(videoInfo: VideoInfo, viewModel: VideoPlayerViewModel) {
    //正在观看的人数
    val onlineCountText by viewModel.onlineCountText.collectAsState()
    //视频关联标签
    val videoTags by viewModel.videoTags.collectAsState()
    LaunchedEffect(Unit) {
        viewModel.getVideoOnlineCountText(videoInfo.aid, videoInfo.cid)
        viewModel.getVideoTags(
            aid = videoInfo.aid,
            bvid = videoInfo.bvid,
            cid = videoInfo.cid,
        )
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
            //视频标签
            FlowRow(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                videoTags.forEach {
                    SuggestionChip(
                        modifier = Modifier.height(25.dp),
                        onClick = {
                            //跳转到标签页
                        },
                        label = {
                            Text(
                                text = it.tagName,
                            )
                        }
                    )
                }
            }
        }
    )

}

/**
 * 基本指标信息和操作 例如点赞
 */
@Composable
private fun BasicIndicatorsUI(videoInfo: VideoInfo, viewModel: VideoPlayerViewModel) {
    val isLike by viewModel.isLike.collectAsState()
    val coinQuotation by viewModel.coinQuotationCount.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.updateUserActionState(videoInfo.aid)
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp)
    ) {
        //点赞
        OperateItemUI(
            icon = Icons.Outlined.ThumbUp,
            text = videoInfo.stat.like.toStringCount(),
            isSelected = isLike,
            onClick = {
                viewModel.like(videoInfo.aid, !isLike)
            })
        //点踩
        OperateItemUI(icon = Icons.Outlined.ThumbDown, text = "不喜欢", onClick = {})
        //投币
        OperateItemUI(
            icon = Icons.Outlined.Toll,
            text = (videoInfo.stat.coin + coinQuotation).toStringCount(),
            isSelected = coinQuotation > 0,
            onClick = {
                viewModel.isShowAddCoinDialog.show()
            })
        //收藏
        OperateItemUI(
            icon = Icons.Outlined.StarOutline,
            isSelected = isFavorite,
            text = if (videoInfo.stat.favorite > 0) videoInfo.stat.favorite.toStringCount() else "收藏",
            onClick = {})
        //分享
        OperateItemUI(
            icon = Icons.Outlined.Share,
            text = if (videoInfo.stat.share > 0) videoInfo.stat.share.toStringCount() else "分享",
            onClick = {})
    }
}

@Composable
private fun RowScope.OperateItemUI(
    icon: ImageVector,
    isSelected: Boolean = false,
    text: String,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier.weight(1f).clickable {
            onClick()
        },
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = if (isSelected) ColorPrimary.copy(alpha = 0.8f) else TextColor.copy(alpha = 0.5f)
        )
        Text(
            text = text,
            fontSize = 12.sp,
            color = TextColor.copy(alpha = 0.7f)
        )
    }
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

@Composable
private fun RecommendedVideoUI(
    aid: Long,
    viewModel: VideoPlayerViewModel
) {
    val navigator = LocalNavigator.currentOrThrow
    val recommendedVideo = viewModel.recommendedVideo
    LaunchedEffect(Unit) {
        viewModel.getRecommendedVideoByVideo(aid)
    }
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        items(recommendedVideo) {
            SimpleVideoCard(it) {
                navigator.push(
                    VideoPlayerScreen(
                        PlayerParams(
                            avid = it.playerArgs.aid,
                            cid = it.playerArgs.cid,
                        ).toJson()
                    )
                )
            }
        }
    }
}