package top.sacz.bili.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController

val LocalNavigation: ProvidableCompositionLocal<BiliNavigation?> =
    compositionLocalOf {
        null
    }

val <T> ProvidableCompositionLocal<T?>.currentOrThrow: T
    @Composable
    get() = current ?: error("CompositionLocal is null")

@Composable
fun rememberNavigator(): BiliNavigation {
    val navigator = rememberNavController()
    return remember {
        BiliNavigation(navigator)
    }
}

@Composable
fun MainNavHost(
    navigator: BiliNavigation,
    startDestination: BiliScreenProvider,
    modifier: Modifier = Modifier,
    builder: NavGraphBuilder.() -> Unit
) {
    CompositionLocalProvider(LocalNavigation.provides(navigator)) {
        NavHost(
            navController = LocalNavigation.currentOrThrow.navigator,
            startDestination = startDestination,
            modifier = modifier,
            builder = builder,
            enterTransition = BiliStandardTransitions.enterTransition,
            exitTransition = BiliStandardTransitions.exitTransition,
            popEnterTransition = BiliStandardTransitions.popEnterTransition,
            popExitTransition = BiliStandardTransitions.popExitTransition
        )
    }
}

/**
 * 栈式导航结构
 */
class BiliNavigation(val navigator: NavHostController) {
    fun push(item: BiliScreenProvider) {
        navigator.navigate(item)
    }

    fun pop(): Boolean {
        return navigator.popBackStack()
    }
}


interface BiliScreenProvider

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BiliBackHandler(enabled: Boolean, onBack: () -> Unit) {
}