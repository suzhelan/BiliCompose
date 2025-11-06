package top.suzhelan.bili.shared.common.ui


import androidx.compose.animation.core.AnimationSpec
import androidx.compose.animation.core.AnimationState
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.DecayAnimationSpec
import androidx.compose.animation.core.animateDecay
import androidx.compose.animation.core.animateTo
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.material3.TopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.layout.LastBaseline
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.layoutId
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.roundToInt

@ExperimentalMaterial3Api
@Composable
fun KnowledgeTopAppBar(
    modifier: Modifier = Modifier,
    titleBottomPadding: Dp = 28.dp,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable RowScope.() -> Unit,
    topLayout: @Composable () -> Unit,
    bottomLayout: @Composable BoxScope.() -> Unit,
    pinnedHeight: Dp = 46.0.dp,
    scrollBehavior: TopAppBarScrollBehavior
) {

    val pinnedHeightPx: Float
    val maxHeightPx: Float
    val titleBottomPaddingPx: Int

    var bottomLayoutViewSize: IntSize by remember { mutableStateOf(IntSize(0, 0)) }
    //计算布局高度
    val bottomLayoutBox = @Composable {
        Box(
            modifier = Modifier.onSizeChanged { bottomLayoutViewSize = it },
            content = bottomLayout
        )
    }

    LocalDensity.current.run {
        pinnedHeightPx = pinnedHeight.toPx()
        maxHeightPx = bottomLayoutViewSize.height.toFloat() + pinnedHeightPx
        titleBottomPaddingPx = titleBottomPadding.roundToPx()
    }


    // 设置应用程序栏的高度偏移限制以仅隐藏底部标题区域并保留顶部标题
    // 折叠时可见。
    SideEffect {
        if (scrollBehavior.state.heightOffsetLimit != pinnedHeightPx - maxHeightPx) {
            scrollBehavior.state.heightOffsetLimit = pinnedHeightPx - maxHeightPx
        }
    }
    val colorTransitionFraction = scrollBehavior.state.collapsedFraction
    val appBarContainerColor by rememberUpdatedState(MaterialTheme.colorScheme.primaryContainer)

    val actionsRow = @Composable {
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            content = actions
        )
    }
    val topLayoutAlpha = CubicBezierEasing(.8f, 0f, .8f, .15f).transform(colorTransitionFraction)
    val bottomLayoutAlpha = 1f - colorTransitionFraction
    // Hide the top row title semantics when its alpha value goes below 0.5 threshold.
    // Hide the bottom row title semantics when the top title semantics are active.
    val hideTopRowSemantics = colorTransitionFraction < 0.5f
    val hideBottomRowSemantics = !hideTopRowSemantics

    // Set up support for resizing the top app bar when vertically dragging the bar itself.
    val appBarDragModifier = if (!scrollBehavior.isPinned) {
        Modifier.draggable(
            orientation = Orientation.Vertical,
            state = rememberDraggableState { delta ->
                scrollBehavior.state.heightOffset = scrollBehavior.state.heightOffset + delta
            },
            onDragStopped = { velocity ->
                settleAppBar(
                    scrollBehavior.state,
                    velocity,
                    scrollBehavior.flingAnimationSpec,
                    scrollBehavior.snapAnimationSpec
                )
            }
        )
    } else {
        Modifier
    }

    Surface(modifier = modifier.then(appBarDragModifier), color = appBarContainerColor) {
        Column {
            TopAppBarLayout(
                modifier = Modifier
                    .statusBarsPadding()
                    // 在填充后剪辑，这样不会在插入区域上显示标题
                    .clipToBounds(),
                heightPx = pinnedHeightPx,
                navigationIconContentColor = ColorPrimary,
                actionIconContentColor = ColorPrimary,
                title = topLayout,
                titleTextStyle = TextStyle.Default,
                titleAlpha = topLayoutAlpha,
                titleVerticalArrangement = Arrangement.Center,
                titleHorizontalArrangement = Arrangement.Start,
                titleBottomPadding = 0,
                hideTitleSemantics = hideTopRowSemantics,
                navigationIcon = navigationIcon,
                actions = actionsRow,
            )
            KnowledgeTitleLayout(
                modifier = Modifier.clipToBounds(),
                heightPx = maxHeightPx - pinnedHeightPx + scrollBehavior.state.heightOffset,
                title = bottomLayoutBox,
                titleTextStyle = TextStyle.Default,
                titleAlpha = bottomLayoutAlpha,
                titleVerticalArrangement = Arrangement.Bottom,
                titleHorizontalArrangement = Arrangement.Start,
                titleBottomPadding = titleBottomPaddingPx,
                hideTitleSemantics = hideBottomRowSemantics,
            )
        }
    }

}

@OptIn(ExperimentalMaterial3Api::class)
private suspend fun settleAppBar(
    state: TopAppBarState,
    velocity: Float,
    flingAnimationSpec: DecayAnimationSpec<Float>?,
    snapAnimationSpec: AnimationSpec<Float>?
): Velocity {
    //检查应用程序栏是否完全折叠/展开。如果是，则无需结算应用程序栏，
    //然后返回零速度。
    //请注意，由于collapsedFraction的浮点精度，不用检查 0f
    if (state.collapsedFraction < 0.01f || state.collapsedFraction == 1f) {
        return Velocity.Zero
    }
    var remainingVelocity = velocity
    //如果有一个初始速度是在前一次用户投掷后留下的，则设置动画以
    // 继续运动以展开或折叠应用程序栏。
    if (flingAnimationSpec != null && abs(velocity) > 1f) {
        var lastValue = 0f
        AnimationState(
            initialValue = 0f,
            initialVelocity = velocity,
        )
            .animateDecay(flingAnimationSpec) {
                val delta = value - lastValue
                val initialHeightOffset = state.heightOffset
                state.heightOffset = initialHeightOffset + delta
                val consumed = abs(initialHeightOffset - state.heightOffset)
                lastValue = value
                remainingVelocity = this.velocity
                // 避免舍入错误，如果有任何内容未被使用，则停止
                if (abs(delta - consumed) > 0.5f) this.cancelAnimation()
            }
    }
    // 如果提供了动画规格，则捕捉。
    if (snapAnimationSpec != null) {
        if (state.heightOffset < 0 &&
            state.heightOffset > state.heightOffsetLimit
        ) {
            AnimationState(initialValue = state.heightOffset).animateTo(
                if (state.collapsedFraction < 0.5f) {
                    0f
                } else {
                    state.heightOffsetLimit
                },
                animationSpec = snapAnimationSpec
            ) { state.heightOffset = value }
        }
    }

    return Velocity(0f, remainingVelocity)
}

@Composable
private fun TopAppBarLayout(
    modifier: Modifier,
    heightPx: Float,
    navigationIconContentColor: Color,
    actionIconContentColor: Color,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    hideTitleSemantics: Boolean,
    navigationIcon: @Composable () -> Unit,
    actions: @Composable () -> Unit,
) {
    Layout(
        {
            Box(
                Modifier
                    .layoutId("navigationIcon")
                    .padding(start = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides navigationIconContentColor,
                    content = navigationIcon
                )
            }
            Box(
                Modifier
                    .layoutId("title")
                    .padding(horizontal = TopAppBarHorizontalPadding)
                    .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
                    .graphicsLayer(alpha = titleAlpha)
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        content = title
                    )
                }
            }
            Box(
                Modifier
                    .layoutId("actionIcons")
                    .padding(end = TopAppBarHorizontalPadding)
            ) {
                CompositionLocalProvider(
                    LocalContentColor provides actionIconContentColor,
                    content = actions
                )
            }
        },
        modifier = modifier
    ) { measurables, constraints ->
        val navigationIconPlaceable =
            measurables.first { it.layoutId == "navigationIcon" }
                .measure(constraints.copy(minWidth = 0))
        val actionIconsPlaceable =
            measurables.first { it.layoutId == "actionIcons" }
                .measure(constraints.copy(minWidth = 0))

        val maxTitleWidth = if (constraints.maxWidth == Constraints.Infinity) {
            constraints.maxWidth
        } else {
            (constraints.maxWidth - navigationIconPlaceable.width - actionIconsPlaceable.width)
                .coerceAtLeast(0)
        }
        val titlePlaceable =
            measurables.first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        // Locate the title's baseline.
        val titleBaseline =
            if (titlePlaceable[LastBaseline] != AlignmentLine.Unspecified) {
                titlePlaceable[LastBaseline]
            } else {
                0
            }

        val layoutHeight = heightPx.roundToInt()

        layout(constraints.maxWidth, layoutHeight) {
            // Navigation icon
            navigationIconPlaceable.placeRelative(
                x = 0,
                y = (layoutHeight - navigationIconPlaceable.height) / 2
            )

            // Title
            titlePlaceable.placeRelative(
                x = when (titleHorizontalArrangement) {
                    Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                    Arrangement.End ->
                        constraints.maxWidth - titlePlaceable.width - actionIconsPlaceable.width
                    // Arrangement.Start.
                    // An TopAppBarTitleInset will make sure the title is offset in case the
                    // navigation icon is missing.
                    else -> max(12.dp.roundToPx(), navigationIconPlaceable.width)
                },
                y = when (titleVerticalArrangement) {
                    Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                    // Apply bottom padding from the title's baseline only when the Arrangement is
                    // "Bottom".
                    Arrangement.Bottom ->
                        if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
                        else layoutHeight - titlePlaceable.height - max(
                            0,
                            titleBottomPadding - titlePlaceable.height + titleBaseline
                        )
                    // Arrangement.Top
                    else -> 0
                }
            )

            // Action icons
            actionIconsPlaceable.placeRelative(
                x = constraints.maxWidth - actionIconsPlaceable.width,
                y = (layoutHeight - actionIconsPlaceable.height) / 2
            )
        }
    }
}


private val TopAppBarHorizontalPadding = 4.dp

@Composable
private fun KnowledgeTitleLayout(
    modifier: Modifier,
    heightPx: Float,
    title: @Composable () -> Unit,
    titleTextStyle: TextStyle,
    titleAlpha: Float,
    titleVerticalArrangement: Arrangement.Vertical,
    titleHorizontalArrangement: Arrangement.Horizontal,
    titleBottomPadding: Int,
    hideTitleSemantics: Boolean,
) {

    Layout(
        {
            Box(
                Modifier
                    .layoutId("title")
                    .then(if (hideTitleSemantics) Modifier.clearAndSetSemantics { } else Modifier)
                    .graphicsLayer(alpha = titleAlpha)
            ) {
                ProvideTextStyle(value = titleTextStyle) {
                    CompositionLocalProvider(
                        content = title
                    )
                }
            }
        },
        modifier = modifier
    ) { measurables, constraints ->

        val maxTitleWidth = constraints.maxWidth
        val titlePlaceable =
            measurables.first { it.layoutId == "title" }
                .measure(constraints.copy(minWidth = 0, maxWidth = maxTitleWidth))

        val layoutHeight = heightPx.roundToInt()

        layout(maxTitleWidth, layoutHeight) {
            // Title
            titlePlaceable.placeRelative(
                x = when (titleHorizontalArrangement) {
                    Arrangement.Center -> (constraints.maxWidth - titlePlaceable.width) / 2
                    Arrangement.End ->
                        constraints.maxWidth - titlePlaceable.width

                    else -> max(0.dp.roundToPx(), 0.dp.roundToPx())
                },
                y = when (titleVerticalArrangement) {
                    Arrangement.Center -> (layoutHeight - titlePlaceable.height) / 2
                    // Apply bottom padding from the title's baseline only when the Arrangement is
                    // "Bottom".
                    Arrangement.Bottom ->
                        if (titleBottomPadding == 0) layoutHeight - titlePlaceable.height
                        else layoutHeight - titlePlaceable.height - max(
                            0,
                            titleBottomPadding - titlePlaceable.height
                        )

                    // Arrangement.Top
                    else -> 0
                }
            )
        }
    }
}
