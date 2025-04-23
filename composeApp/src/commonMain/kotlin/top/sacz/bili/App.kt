package top.sacz.bili

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
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
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
) {
    RouteNavigationConfig.routingScreenRegistration()

    MaterialTheme(colorScheme = colorScheme) {

        Navigator(HomeScreen) { navigator ->
            SlideTransition(navigator)
        }
    }
}
