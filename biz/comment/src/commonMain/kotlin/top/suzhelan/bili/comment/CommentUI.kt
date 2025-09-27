package top.suzhelan.bili.comment

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import top.suzhelan.bili.comment.entity.CommentSourceType
import top.suzhelan.bili.comment.viewmodel.CommentViewModel


@Composable
fun CommentContent(oid: String, type: CommentSourceType) {
    val viewModel: CommentViewModel = viewModel { CommentViewModel() }
    LaunchedEffect(Unit) {
        viewModel.getCommentList(oid, type)
    }

    Text(text = "评论内容")
}