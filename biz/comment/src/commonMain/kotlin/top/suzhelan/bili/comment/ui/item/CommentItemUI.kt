package top.suzhelan.bili.comment.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MoreVert
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.comment.entity.NewComment
import top.suzhelan.bili.comment.ui.text.CompoundEmojiMessage
import top.suzhelan.bili.comment.ui.text.CompoundEmojiMessageModel
import top.suzhelan.bili.comment.ui.text.toMessageEmote
import top.suzhelan.bili.shared.common.ui.icons.LevelIcons
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.toStringCount
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun CommentCard(
    comment: NewComment,
    onClick: (NewComment) -> Unit = {},
    onReplyClick: (NewComment) -> Unit = {},
    showReplyPreview: Boolean = true,
) {
    val navigation = LocalNavigation.currentOrThrow
    //构成 头像,头像框背景
    //昵称 等级 评论背景
    //评论内容
    //时间 ip ‘回复’ 点赞 点踩 更多操作
    //被回复预览
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(comment) }
            .padding(0.dp)
    ) {
        val (avatar, nickname, level, background, content, infoLine, repliedPreview) = createRefs()

        //背景
        AsyncImage(
            model = comment.member.userSailing?.cardBg?.image,
//            model = "https://i0.hdslb.com/bfs/garb/item/b96c1df81ca32fc79165c94c4da1f7dd404f42d3.png",//示例背景
            contentDescription = "Avatar",
            alignment = Alignment.CenterEnd,
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth()
                .constrainAs(background) {
                    top.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                }
        )

        //头像
        Avatar(
            member = comment.member, modifier = Modifier
                .clickable {
                    navigation.push(SharedScreen.UserProfile(mid = comment.member.mid))
                }
                .constrainAs(avatar) {
                    top.linkTo(parent.top, 8.dp)
                    start.linkTo(parent.start, 8.dp)
                })

        //昵称
        Text(
            text = comment.member.uname,
            fontSize = 14.sp,
            modifier = Modifier.constrainAs(nickname) {
                top.linkTo(avatar.top, 8.dp)
                start.linkTo(avatar.end)
            })

        //等级
        Image(
            painter = painterResource(
                LevelIcons.levelIconMap.getValue(comment.member.levelInfo.currentLevel)
            ),
            contentDescription = "lv${comment.member.levelInfo.currentLevel}",
            modifier = Modifier
                .height(15.dp)
                .padding(start = 10.dp)
                .constrainAs(level) {
                    top.linkTo(nickname.top)
                    bottom.linkTo(nickname.bottom)
                    start.linkTo(nickname.end)
                },
        )

        //复合消息卡片
        CommentRichContent(
            comment = comment,
            modifier = Modifier.constrainAs(content) {
                top.linkTo(nickname.bottom)
                start.linkTo(nickname.start)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            }
        )

        //信息行
        InfoLineRow(
            comment = comment, modifier = Modifier
                .fillMaxWidth()
                .padding(end = 8.dp)
                .constrainAs(infoLine) {
                    top.linkTo(content.bottom)
                    start.linkTo(content.start)
                    end.linkTo(parent.end)
                    width = Dimension.fillToConstraints
                },
            onReplyClick = onReplyClick
        )

        if (showReplyPreview && comment.rcount > 0) {
            //被回复预览
            RepliedPreview(comment, modifier = Modifier.constrainAs(repliedPreview) {
                top.linkTo(infoLine.bottom)
                start.linkTo(content.start)
                end.linkTo(parent.end, 8.dp)
                width = Dimension.fillToConstraints
            })
        }
    }
}

@Composable
private fun CommentRichContent(
    comment: NewComment,
    modifier: Modifier = Modifier,
    size: Int = 14,
) = Column(modifier = modifier) {
    if (comment.content.message.isNotBlank() || comment.content.emote.isNotEmpty()) {
        CompoundEmojiMessage(
            content = CompoundEmojiMessageModel.MessageContent(
                comment.content.message,
                comment.content.emote.mapValues { (_, emote) -> emote.toMessageEmote() }
            ),
            size = size
        )
    }

    CommentPictureGrid(pictures = comment.content.pictures)
}

@Composable
private fun CommentPictureGrid(
    pictures: List<NewComment.Content.Picture>,
    modifier: Modifier = Modifier,
) {
    val validPictures = pictures.filter { picture -> picture.imgSrc.isNotBlank() }
    if (validPictures.isEmpty()) {
        return
    }

    if (validPictures.size == 1) {
        SingleCommentPicture(
            picture = validPictures.first(),
            modifier = modifier.padding(top = 6.dp)
        )
    } else {
        MultiCommentPictures(
            pictures = validPictures,
            modifier = modifier.padding(top = 6.dp)
        )
    }
}

@Composable
private fun SingleCommentPicture(
    picture: NewComment.Content.Picture,
    modifier: Modifier = Modifier,
) = BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    val aspectRatio = pictureAspectRatio(picture)
    val maxImageHeight = 260.dp
    val maxImageWidth = if (aspectRatio >= 1f) {
        maxWidth.coerceAtMost(240.dp)
    } else {
        maxWidth.coerceAtMost(180.dp)
    }
    val wantedHeight = maxImageWidth / aspectRatio
    val imageHeight = wantedHeight.coerceAtMost(maxImageHeight)
    val imageWidth = if (wantedHeight > maxImageHeight) {
        imageHeight * aspectRatio
    } else {
        maxImageWidth
    }

    AsyncImage(
        model = picture.imgSrc,
        contentDescription = "评论图片",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .width(imageWidth)
            .height(imageHeight)
            .clip(RoundedCornerShape(4.dp))
            .background(TipColor.copy(alpha = 0.08f))
    )
}

@Composable
private fun MultiCommentPictures(
    pictures: List<NewComment.Content.Picture>,
    modifier: Modifier = Modifier,
) = BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
    val columnCount = if (pictures.size == 2) 2 else 3
    val gap = 4.dp
    val maxGridWidth = maxWidth.coerceAtMost(300.dp)
    val itemSize = ((maxGridWidth - gap * (columnCount - 1)) / columnCount.toFloat())
        .coerceAtMost(96.dp)

    Column(verticalArrangement = Arrangement.spacedBy(gap)) {
        pictures.chunked(columnCount).forEach { rowPictures ->
            Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
                rowPictures.forEach { picture ->
                    AsyncImage(
                        model = picture.imgSrc,
                        contentDescription = "评论图片",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(itemSize)
                            .clip(RoundedCornerShape(4.dp))
                            .background(TipColor.copy(alpha = 0.08f))
                    )
                }
            }
        }
    }
}

private fun pictureAspectRatio(picture: NewComment.Content.Picture): Float {
    return if (picture.imgWidth > 0 && picture.imgHeight > 0) {
        (picture.imgWidth.toFloat() / picture.imgHeight.toFloat()).coerceIn(0.45f, 2.4f)
    } else {
        1f
    }
}

@Composable
private fun Avatar(member: NewComment.Member, modifier: Modifier) =
    Box(modifier = modifier.size(60.dp)) {
        //头像框边距 大概是父布局也就是80dp的1/4
        AsyncImage(
            model = member.avatar,
            contentDescription = "Avatar",
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp) // 留出头像框空间
                .clip(CircleShape)
        )
        AsyncImage(
//            model = "https://i1.hdslb.com/bfs/garb/item/f8498be6ba4e87e7469943abafa27fff9975c658.png",//示例头像框
            model = member.pendant.image,
            contentDescription = "Pendant",
            modifier = Modifier.fillMaxSize() // 背景填满整个Box
        )
    }

@Composable
private fun InfoLineRow(
    comment: NewComment,
    modifier: Modifier,
    onReplyClick: (NewComment) -> Unit,
) {
    val action = comment.action
    //时间 ip ‘回复’ 点赞 点踩 更多操作
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = comment.replyControl.timeDesc,
            color = TipColor,
            fontSize = 12.sp,
        )
        Text(
            text = " ${comment.replyControl.location.replace("IP属地：", "")}",
            color = TipColor,
            fontSize = 12.sp,
        )
        Text(
            text = " 回复",
            color = TextColor,
            fontSize = 12.sp,
            modifier = Modifier.clickable {
                onReplyClick(comment)
            }
        )
        Spacer(modifier = Modifier.weight(1f))


        Row(
            modifier = Modifier.wrapContentSize()
                .padding(horizontal = 4.dp)
                .clip(CircleShape)
                .clickable {
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Outlined.ThumbUp,
                contentDescription = "Like",
                modifier = Modifier.size(14.dp),
                tint = if (action == 1) ColorPrimary else TipColor
            )
            Spacer(modifier = Modifier.width(2.dp))
            Text(
                text = comment.like.toStringCount(),
                color = if (action == 1) ColorPrimary else TipColor,
                fontSize = 12.sp,
            )
        }


        IconButton(onClick = {

        }, modifier = Modifier.size(18.dp)) {
            Icon(
                imageVector = Icons.Outlined.ThumbDown,
                modifier = Modifier.size(14.dp),
                contentDescription = "Dislike",
                tint = if (action == 2) ColorPrimary else TipColor
            )
        }

        IconButton(onClick = {

        }, modifier = Modifier.size(18.dp)) {
            Icon(
                imageVector = Icons.Outlined.MoreVert,
                modifier = Modifier.size(14.dp),
                contentDescription = "More",
                tint = TipColor
            )
        }

    }
}

@Composable
private fun RepliedPreview(comment: NewComment, modifier: Modifier) = Column(
    modifier = modifier.fillMaxWidth()
        .background(TipColor.copy(alpha = 0.1f), RoundedCornerShape(4.dp))
        .padding(6.dp)
) {
    val replied = comment.replies ?: emptyList()
    replied.forEach {
        SimpleReplyCard(it)
    }
    //如果预览可以完全展示所有回复消息 那就不展示被回复总数
    if (replied.size != comment.rcount) {
        Text(
            text = "共${comment.rcount}条回复",
            color = ColorPrimary,
            fontSize = 12.sp
        )
    }
}

@Composable
private fun SimpleReplyCard(comment: NewComment) {
    val message = comment.content.message.ifBlank {
        if (comment.content.pictures.isNotEmpty()) {
            "[图片]"
        } else {
            ""
        }
    }
    CompoundEmojiMessage(
        content = CompoundEmojiMessageModel.MessageContent(
            "${comment.member.uname}: $message",
            comment.content.emote.mapValues { (_, emote) -> emote.toMessageEmote() }
        ),
        size = 13,
        modifier = Modifier.padding(vertical = 2.dp)
    )
}
