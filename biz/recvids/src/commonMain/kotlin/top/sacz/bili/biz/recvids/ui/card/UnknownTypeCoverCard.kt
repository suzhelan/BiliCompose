package top.sacz.bili.biz.recvids.ui.card

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import top.sacz.bili.biz.recvids.entity.BaseCoverItem

@Composable
fun UnknownTypeCoverCard(unknownCoverItem: BaseCoverItem) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(230.dp)
    ) {
        Text("card_goto:${unknownCoverItem.cardGoto}")
        Text("card_type:${unknownCoverItem.cardType}")
        Text("idx:${unknownCoverItem.idx}")
    }
}