package top.sacz.bili.biz.home

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import top.sacz.bili.biz.login.ui.BehavioralValidation
import top.sacz.bili.biz.recvids.ui.RecommendedVideoPage

enum class AppDestinations(
    val label: String, val icon: ImageVector, val contentDescription: String
) {
    HOME("Home", Icons.Rounded.Home, "HOME"),
    Mine("My", Icons.Rounded.Person, "MY")
}


@Composable
fun HomeScreen() {

    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    //在横屏设备导航栏在左侧 竖屏在底部
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach { //遍历枚举
                item(
                    icon = {
                        Icon(
                            it.icon, contentDescription = (it.contentDescription)
                        )
                    },
                    alwaysShowLabel = false,
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it })
            }
        }) {
        when (currentDestination) {
            AppDestinations.HOME -> {
                RecommendedVideoPage()
            }

            AppDestinations.Mine -> {
                MinimalDialog {
                    // Handle dialog close
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MinimalDialog(onDismissRequest: () -> Unit) {
    BasicAlertDialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            BehavioralValidation()
        }
    }
}