package top.suzhelan.bili.shared.common.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LoadingIndicator(modifier: Modifier = Modifier.fillMaxSize(), text: String = "加载中...") {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            strokeWidth = 10.dp, modifier = Modifier.size(100.dp).padding(10.dp)
        )
        Text(
            text = text,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
        )
    }
}