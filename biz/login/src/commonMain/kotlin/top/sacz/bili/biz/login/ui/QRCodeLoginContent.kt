package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import io.github.alexzhirkevich.qrose.ImageFormat
import io.github.alexzhirkevich.qrose.options.QrBallShape
import io.github.alexzhirkevich.qrose.options.QrBrush
import io.github.alexzhirkevich.qrose.options.QrFrameShape
import io.github.alexzhirkevich.qrose.options.QrPixelShape
import io.github.alexzhirkevich.qrose.options.circle
import io.github.alexzhirkevich.qrose.options.roundCorners
import io.github.alexzhirkevich.qrose.options.solid
import io.github.alexzhirkevich.qrose.rememberQrCodePainter
import io.github.alexzhirkevich.qrose.toByteArray
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.login.model.TvQRCode
import top.sacz.bili.biz.login.viewmodel.QRCodeLoginViewModel
import top.sacz.bili.shared.common.ui.LoadingIndicator

/**
 * 扫码登录组件 比较简单
 * 1. 获取二维码和qrcodeKey
 * 2. 用qrcode轮询二维码状态
 */
@Composable
fun QRCodeLoginContent(viewModel: QRCodeLoginViewModel = viewModel()) {
    //获取最近的导航 登录成功后pop当前页面
    val navigator = LocalNavigator.currentOrThrow
    val getQRCode by viewModel.qrCode.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getQRCode()
    }
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        when (val qrCode = getQRCode) {
            is BiliResponse.Error -> {
            }

            is BiliResponse.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.size(150.dp)
                )
            }

            is BiliResponse.Success<TvQRCode> -> {
                Text(
                    text = "请使用哔哩哔哩app扫码登录"
                )
                Spacer(modifier = Modifier.size(20.dp))
                QRCodeImage(qrCode.data.url)
                Spacer(modifier = Modifier.size(40.dp))
                Text(
                    text = qrCode.data.url,
                    fontSize = MaterialTheme.typography.labelSmall.fontSize,
                    color = MaterialTheme.colorScheme.outlineVariant,
                )
            }

            else -> {}

        }
    }
}

@Composable
private fun QRCodeImage(url: String) {
    val qrcodeColor = MaterialTheme.colorScheme.primary
    val qrcodePainter : Painter = rememberQrCodePainter(url) {
        //可以在二维码加logo 但是没有比他
        shapes {
            ball = QrBallShape.circle()
            darkPixel = QrPixelShape.roundCorners()
            frame = QrFrameShape.roundCorners(.25f)
        }
        colors {
            //主要色块
            dark = QrBrush.solid(qrcodeColor)
            //渐变形
            /*QrBrush.brush {
                Brush.linearGradient(
                    0f to qrcodeEndColor,
                    1f to qrcodeEndColor,
                    end = Offset(it, it)
                )
            }*/
/*            //边框色块
            frame = QrBrush.solid(cornerColor)
            //边框内圆点色块
            ball  = QrBrush.solid(cornerColor)*/
        }
    }
    Image(
        painter = qrcodePainter,
        contentDescription = null,
        modifier = Modifier.size(150.dp).clickable {
            val qrcodeBytes : ByteArray = qrcodePainter.toByteArray(1024, 1024, ImageFormat.PNG)
        }
    )
}