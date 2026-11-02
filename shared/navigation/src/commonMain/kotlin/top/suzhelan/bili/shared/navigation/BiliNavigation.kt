package top.suzhelan.bili.shared.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
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

/** 区分栈内导航与应用退到后台：后台不会改变当前栈条目。 */
@Composable
fun NavigationPageEffect(onActiveChange: (Boolean) -> Unit) {
    val controller = LocalNavigation.currentOrThrow.navigator
    val owner = LocalLifecycleOwner.current
    val entryId = remember(owner) {
        (owner as? NavBackStackEntry)?.id ?: controller.currentBackStackEntry?.id
    }
    val onChange by rememberUpdatedState(onActiveChange)
    DisposableEffect(controller, entryId) {
        val listener = NavController.OnDestinationChangedListener { nav, _, _ ->
            onChange(nav.currentBackStackEntry?.id == entryId)
        }
        controller.addOnDestinationChangedListener(listener)
        onDispose {
            if (controller.currentBackStackEntry?.id != entryId) onChange(false)
            controller.removeOnDestinationChangedListener(listener)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun BiliBackHandler(enabled: Boolean, onBack: () -> Unit) {
}
