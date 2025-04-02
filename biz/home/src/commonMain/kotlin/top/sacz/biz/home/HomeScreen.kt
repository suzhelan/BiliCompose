package top.sacz.biz.home

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestinations(
    val label: String,
    val icon: ImageVector,
    val contentDescription: String
) {
    HOME("HOME", Icons.Default.Home, "HOME"),
    FAVORITES("FAVORITES", Icons.Default.Favorite, "FAVORITES"),
    SHOPPING("SHOPPING", Icons.Default.ShoppingCart, "SHOPPING"),
    PROFILE("PROFILE", Icons.Default.AccountBox, "PROFILE"),
}

@Composable
fun HomeScreen() {
    var currentDestination by rememberSaveable { mutableStateOf(AppDestinations.HOME) }
    NavigationSuiteScaffold(
        navigationSuiteItems = {
            AppDestinations.entries.forEach {
                item(
                    icon = {
                        Icon(
                            it.icon,
                            contentDescription = (it.contentDescription)
                        )
                    },
                    alwaysShowLabel = false,
                    label = { Text(it.label) },
                    selected = it == currentDestination,
                    onClick = { currentDestination = it }
                )
            }
        }
    ) {
        CustomView(currentDestination.contentDescription)
    }
}

@Composable
fun CustomView(contentDescription: String) {
    Text(text = contentDescription)
}

fun main() {

}