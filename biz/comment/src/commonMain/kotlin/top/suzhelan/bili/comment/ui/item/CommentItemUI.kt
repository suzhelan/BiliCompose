package top.suzhelan.bili.comment.ui.item

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import coil3.compose.AsyncImage
import top.suzhelan.bili.comment.entity.Comment

@Composable
fun CommentCard(comment: Comment) {
    //构成 头像,头像框背景
    //昵称 等级 评论背景
    //评论内容
    //时间 ‘回复’ 点赞 点踩 更多操作
    //被回复预览
    ConstraintLayout(modifier = Modifier.fillMaxWidth().padding(8.dp)) {
        val (avatar, _, _, _, _, _, _, _, _, _, _) = createRefs()
        Avatar(member = comment.member, modifier = Modifier.constrainAs(avatar) {
            top.linkTo(parent.top)
            start.linkTo(parent.start)
        })
    }
}

@Composable
private fun Avatar(member: Comment.Member, modifier: Modifier) = Box(modifier = modifier.size(80.dp)) {
    //头像框边距 大概是父布局也就是80dp的1/4
    AsyncImage(
        model = member.avatar,
        contentDescription = "Avatar",
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp) // 留出头像框空间
            .clip(CircleShape)
    )
    AsyncImage(
        model = "https://i1.hdslb.com/bfs/garb/item/f8498be6ba4e87e7469943abafa27fff9975c658.png",
        contentDescription = "Pendant",
        modifier = Modifier.fillMaxSize() // 背景填满整个Box
    )
}
