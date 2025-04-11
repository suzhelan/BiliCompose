package top.sacz.bili.biz.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import top.sacz.bili.biz.login.ui.BehavioralValidationDialog
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
                BehavioralValidationDialog()
            }
        }
    }
}

