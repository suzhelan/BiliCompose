package top.sacz.bili.shared.common.ext

import kotlinx.coroutines.flow.MutableStateFlow


fun MutableStateFlow<Boolean>.toTrue() {
    this.value = true
}
fun MutableStateFlow<Boolean>.toFalse() {
    this.value = false
}

