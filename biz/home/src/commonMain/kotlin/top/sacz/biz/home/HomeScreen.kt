package top.sacz.biz.home


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.api.Response
import top.sacz.biz.home.model.VideoList
import top.sacz.biz.home.viewmodel.FeedViewModel

enum class AppDestinations(
    val label: String, val icon: ImageVector, val contentDescription: String
) {
    HOME("HOME", Icons.Default.Home, "HOME"), FAVORITES(
        "FAVORITES", Icons.Default.Favorite, "FAVORITES"
    ),
    SHOPPING("SHOPPING", Icons.Default.ShoppingCart, "SHOPPING"), PROFILE(
        "PROFILE", Icons.Default.AccountBox, "PROFILE"
    ),
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

    LaunchedEffect(Unit) {
        viewModel.getFeed()
    }
    //垂直布局
    Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
        Text(text = contentDescription)
        ConstraintLayoutContent()
        Text(
            text = when (videoListResponse) {
                is Response.Success -> {
                    (videoListResponse as Response.Success<VideoList>).data.toString()
                }

                is Response.Error -> {
                    "Error"
                }

                else -> {
                    "Loading"
                }
            }
        )
    }
}

@Composable
fun ConstraintLayoutContent() {
    ConstraintLayout {
        // Create references for the composables to constrain
        val (button, text) = createRefs()

        Button(
            onClick = { /* Do something */ },
            // Assign reference "button" to the Button composable
            // and constrain it to the top of the ConstraintLayout
            modifier = Modifier.constrainAs(button) {
                top.linkTo(parent.top, margin = 16.dp)
            }) {
            Text("Button")
        }

        // Assign reference "text" to the Text composable
        // and constrain it to the bottom of the Button composable
        Text(
            "Text", Modifier.constrainAs(text) {
                top.linkTo(button.bottom, margin = 16.dp)
            })
    }
}