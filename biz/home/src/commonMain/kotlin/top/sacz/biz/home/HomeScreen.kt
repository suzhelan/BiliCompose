package top.sacz.biz.home


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import top.sacz.biz.home.ui.RecommendedVideoPage

enum class AppDestinations(
    val label: String, val icon: ImageVector, val contentDescription: String
) {
    HOME("Home", Icons.Rounded.Home, "HOME"),
    Mine("My", Icons.Rounded.Person, "My")
}

@OptIn(ExperimentalMaterial3Api::class)
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
        Scaffold { padding ->
            Box(modifier = Modifier.padding(padding)) {
                CustomView(currentDestination.contentDescription)
            }
        }
    }
}

@Composable
fun CustomView(contentDescription: String) {
    //垂直布局
    RecommendedVideoPage()

}

