package top.sacz.bili.shared.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavBackStackEntry


/**
 * 导航动画 包括缩放、淡入淡出和滑动的组合效果
 */
object BiliTransitions {
    private const val DURATION = 500

    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            fadeIn(
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth / 10 },
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            )
        }

    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        fadeOut(
            animationSpec = tween(DURATION, easing = LinearOutSlowInEasing)
        ) + scaleOut(
            targetScale = 1.05f,
            animationSpec = tween(DURATION, easing = LinearOutSlowInEasing)
        )
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            fadeIn(
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            ) + scaleIn(
                initialScale = 0.95f,
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            ) + slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 10 },
                animationSpec = tween(DURATION, easing = FastOutSlowInEasing)
            )
        }

    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            fadeOut(
                animationSpec = tween(DURATION, easing = LinearOutSlowInEasing)
            ) + scaleOut(
                targetScale = 0.95f,
                animationSpec = tween(DURATION, easing = LinearOutSlowInEasing)
            ) + slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth / 10 },
                animationSpec = tween(DURATION, easing = LinearOutSlowInEasing)
            )
        }
}

/**
 * 实现简单标准进出动画
 */
object BiliStandardTransitions {
    private const val DURATION = 400
    val enterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(DURATION)
            )
        }
    val exitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) = {
        slideOutHorizontally(
            targetOffsetX = { fullWidth -> -fullWidth / 4 },
            animationSpec = tween(DURATION)
        )
    }

    val popEnterTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            slideInHorizontally(
                initialOffsetX = { fullWidth -> -fullWidth / 4 },
                animationSpec = tween(DURATION)
            )
        }
    val popExitTransition: (AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            slideOutHorizontally(
                targetOffsetX = { fullWidth -> fullWidth },
                animationSpec = tween(DURATION)
            )
        }

}