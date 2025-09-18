package top.sacz.bili.player.ui.topbar

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import top.sacz.bili.player.controller.PlayerSyncController
import top.sacz.bili.player.ui.theme.PlayerColor

//顶栏元素
@Composable
fun PlayerTopBar(
    controller: PlayerSyncController
) {
    val isFullScreen = controller.isFullScreen
    Row(
        modifier = Modifier.fillMaxWidth()
            .background(
                Brush.verticalGradient(
                    0f to Color.Black.copy(alpha = 0.4f),
                    1f to Color.Transparent,
                )
            )
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        //返回按钮
        IconButton(onClick = {
            controller.onBack()
        }) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                tint = PlayerColor.control,
                contentDescription = "Back",
                modifier = Modifier.size(24.dp)
            )
        }
        //仅全屏展示标题
        if (isFullScreen) {
            Text(
                text = "Title",
                color = PlayerColor.control,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}