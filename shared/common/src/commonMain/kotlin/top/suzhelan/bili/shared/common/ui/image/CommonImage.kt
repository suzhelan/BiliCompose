package top.suzhelan.bili.shared.common.ui.image

import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.setSingletonImageLoaderFactory
import coil3.request.crossfade
import top.suzhelan.bili.shared.common.ui.LoadingIndicator

private lateinit var coilContext: PlatformContext

@Composable
fun InitCoil() {
    setSingletonImageLoaderFactory { context ->
        coilContext = context
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
    }
}

/**
 * 也许我们等待哪一天gif可以在多平台上使用
 */
@Composable
fun GifAsyncImage(
    url: String,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    alignment: Alignment = Alignment.Center,
    loading: @Composable () -> Unit = { LoadingIndicator() },
    error: @Composable (Exception) -> Unit = { LoadingIndicator() },
    alpha: Float = DefaultAlpha,
    colorFilter: ColorFilter? = null,
) {

}