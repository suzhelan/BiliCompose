package top.sacz.bili

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import org.jetbrains.compose.ui.tooling.preview.Preview
import top.sacz.bili.biz.home.HomeScreen
import top.sacz.bili.route.RouteNavigationConfig

@Composable
@Preview
fun App() {
    RouteNavigationConfig.routingScreenRegistration()
    //判断是否深色模式
    val isDarkTheme = isSystemInDarkTheme()
    MaterialTheme(colorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()) {
        Navigator(HomeScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
