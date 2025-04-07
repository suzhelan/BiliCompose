package top.sacz.biz.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import com.multiplatform.webview.web.WebView
import com.multiplatform.webview.web.rememberWebViewState
import top.sacz.biz.home.viewmodel.FeedViewModel

enum class AppDestinations(
    val label: String, val icon: ImageVector, val contentDescription: String
) {
    HOME("Home", Icons.Rounded.Home, "HOME"),
    Mine("My", Icons.Rounded.Person, "My")
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
        CustomView(currentDestination.contentDescription)
    }
}

@Composable
fun CustomView(contentDescription: String) {
    val viewModel: FeedViewModel = viewModel()
    val videoListResponse by viewModel.recommendedLevelList.collectAsState()

    val webViewState = rememberWebViewState("https://github.com/KevinnZou/compose-webview-multiplatform")
    LaunchedEffect(contentDescription) {
        viewModel.getFeed()
    }

    //垂直布局
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = contentDescription)
        WebView(
            state = webViewState,
            modifier = Modifier.fillMaxSize()
        )
    }
}