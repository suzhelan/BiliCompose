package top.sacz.bili

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import org.jetbrains.compose.ui.tooling.preview.Preview
import top.sacz.bili.route.routingScreenRegistration
import top.sacz.bili.shared.navigation.MainNavHost
import top.sacz.bili.shared.navigation.SharedScreen
import top.sacz.bili.shared.navigation.rememberNavigator

@Composable
@Preview
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
