package top.suzhelan.bili.comment.ui.item

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import top.suzhelan.bili.comment.entity.Comment
import top.suzhelan.bili.comment.ui.text.CompoundEmojiMessage
import top.suzhelan.bili.comment.ui.text.CompoundEmojiMessageModel
import top.suzhelan.bili.shared.common.ui.icons.LevelIcons
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.common.ui.theme.TextColor
import top.suzhelan.bili.shared.common.ui.theme.TipColor
import top.suzhelan.bili.shared.common.util.toStringCount


@Composable
fun CommentCard(comment: Comment) {

    //构成 头像,头像框背景
    //昵称 等级 评论背景
    //评论内容
    //时间 ip ‘回复’ 点赞 点踩 更多操作
    //被回复预览
    ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(0.dp)) {
        val (avatar, nickname, level, background, content, infoLine, _, _, _, _, _) = createRefs()

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
        Avatar(member = comment.member, modifier = Modifier.constrainAs(avatar) {
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
        CompoundEmojiMessage(
            content = CompoundEmojiMessageModel.MessageContent(
                comment.content.message,
                comment.content.emote
            ),
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
                })

    }
}

@Composable
private fun Avatar(member: Comment.Member, modifier: Modifier) =
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
    comment: Comment,
    modifier: Modifier
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