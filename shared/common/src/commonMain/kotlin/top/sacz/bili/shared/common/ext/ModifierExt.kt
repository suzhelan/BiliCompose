package top.sacz.bili.shared.common.ext

import androidx.compose.foundation.layout.size
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp

object Visibility {
    const val VISIBLE = 0
    const val INVISIBLE = 4
    const val GONE = 8
}

/**
 * 是否可见
 */
fun Modifier.visible(visible: Int): Modifier {
    return when (visible) {
        Visibility.VISIBLE -> this
        Visibility.INVISIBLE -> this.alpha(0f)
        Visibility.GONE -> this.size(0.dp)
        else -> this.alpha(1f)
    }
}

fun Modifier.isGone(hide: Boolean): Modifier {
    return if (hide) {
        visible(Visibility.GONE)
    } else {
        this
    }
}

fun Modifier.isInvisible(invisible: Boolean): Modifier {
    return if (invisible) {
        visible(Visibility.INVISIBLE)
    } else {
        this
    }
}

fun Modifier.isVisible(visible: Boolean): Modifier {
    return if (visible) {
        this
    } else {
        visible(Visibility.GONE)
    }
}