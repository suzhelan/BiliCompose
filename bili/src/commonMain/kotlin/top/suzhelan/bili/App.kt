package top.suzhelan.bili

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import top.suzhelan.bili.route.routingScreenRegistration
import top.suzhelan.bili.shared.navigation.MainNavHost
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.rememberNavigator

@Composable
fun App(
    isDarkTheme: Boolean = isSystemInDarkTheme(),
    colorScheme: ColorScheme = if (isDarkTheme) darkColorScheme() else lightColorScheme()
) {
    val navigator = rememberNavigator()
    MaterialTheme(colorScheme = colorScheme) {
        MainNavHost(navigator, SharedScreen.Home) {
            routingScreenRegistration()
        }
    }
}
