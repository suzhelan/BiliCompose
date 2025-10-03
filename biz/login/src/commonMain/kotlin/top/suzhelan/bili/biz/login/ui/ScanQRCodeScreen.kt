package top.suzhelan.bili.biz.login.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import qrscanner.CameraLens
import qrscanner.QrScanner
import top.suzhelan.bili.shared.common.logger.LogUtils
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimaryContainer
import top.suzhelan.bili.shared.common.ui.theme.ColorSurface
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.SharedScreen
import top.suzhelan.bili.shared.navigation.currentOrThrow


@Composable
fun ScanQRCodeScreen() {
    val navigation = LocalNavigation.currentOrThrow

    var scanQRCodeResult by remember {
        mutableStateOf("")
    }
    //获取状态栏高度
    val stateBarPadding = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    LaunchedEffect(scanQRCodeResult) {
        if (scanQRCodeResult.isNotEmpty()) {
            navigation.push(SharedScreen.WebView(scanQRCodeResult))
        }
    }
    Box(modifier = Modifier.fillMaxSize()) {
        QrScanner(
            modifier = Modifier.fillMaxWidth(),
            flashlightOn = false,
            cameraLens = CameraLens.Back,
            openImagePicker = false,
            overlayBorderColor = ColorPrimaryContainer,
            onCompletion = { url ->
                scanQRCodeResult = url
            },
            imagePickerHandler = {

            },
            onFailure = {
                LogUtils.d("QRCode", "Failure: $it")
            },
            zoomLevel = 2f
        )
        TextButton(
            onClick = {
                navigation.pop()
            },
            modifier = Modifier.align(Alignment.TopStart)
                .padding(top = stateBarPadding + 10.dp, start = 10.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                contentDescription = "Back",
                modifier = Modifier.size(32.dp),
                tint = ColorSurface
            )

        }
    }

}


