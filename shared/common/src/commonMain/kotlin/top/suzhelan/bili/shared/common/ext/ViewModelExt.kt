package top.suzhelan.bili.shared.common.ext

import kotlinx.coroutines.flow.MutableStateFlow

fun MutableStateFlow<Boolean>.toToggle() {
    this.value = !this.value
}

fun MutableStateFlow<Boolean>.show() {
    this.value = true
}

fun MutableStateFlow<Boolean>.dismiss() {
    this.value = false
}

fun MutableStateFlow<Boolean>.toTrue() {
    this.value = true
}

fun MutableStateFlow<Boolean>.toFalse() {
    this.value = false
}

