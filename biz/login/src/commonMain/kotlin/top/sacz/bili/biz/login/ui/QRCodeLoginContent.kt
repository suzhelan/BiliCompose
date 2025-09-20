package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
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
import top.sacz.bili.shared.auth.config.LoginMapper
import top.sacz.bili.shared.common.ui.LoadingIndicator
import top.sacz.bili.shared.common.ui.dialog.MessageDialog
import top.sacz.bili.shared.common.ui.theme.ColorPrimary
import top.sacz.bili.shared.navigation.LocalNavigation
import top.sacz.bili.shared.navigation.currentOrThrow

/**
 * 扫码登录组件 比较简单
 * 1. 获取二维码和qrcodeKey
 * 2. 用qrcode轮询二维码状态
 */
@Composable
fun QRCodeLoginContent(viewModel: QRCodeLoginViewModel = viewModel()) {
    //获取最近的导航 登录成功后pop当前页面
    val getQRCode by viewModel.qrCode.collectAsStateWithLifecycle()
    val sendCountdown by viewModel.sendCountdown.collectAsStateWithLifecycle()
    val queryMessage by viewModel.queryMessage.collectAsStateWithLifecycle()
    val isShowLoginSuccessDialog by viewModel.isShowLoginSuccessDialog.collectAsStateWithLifecycle()
    LaunchedEffect(Unit) {
        viewModel.getQRCode()
    }
    if (isShowLoginSuccessDialog) {
        LoginSuccessDialog()
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "请使用哔哩哔哩app扫码登录"
        )
        Spacer(modifier = Modifier.size(20.dp))
        Text(
            text = "扫码状态:$queryMessage",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Text(
            text = "二维码过期倒计时:${sendCountdown}秒",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.outlineVariant,
        )
        Spacer(modifier = Modifier.size(20.dp))
        when (val qrCode = getQRCode) {
            is BiliResponse.Error -> {
            }

            is BiliResponse.Loading -> {
                LoadingIndicator(
                    modifier = Modifier.size(150.dp)
                )
            }

            is BiliResponse.Success<TvQRCode> -> {
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

        OutlinedButton(
            onClick = {
                viewModel.getQRCode()
            }
        ) {
            Text(text = "重新获取二维码")
        }
    }
}

@Composable
private fun LoginSuccessDialog() {
    val navigator = LocalNavigation.currentOrThrow
    MessageDialog(
        icon = Icons.Outlined.VerifiedUser,
        title = "登录成功",
        text = "登录成功当前用户mid:${LoginMapper.getMid()}",
        confirmButtonText = "确定",
        onConfirmRequest = {
            navigator.pop()
        }
    )
}

@Composable
private fun QRCodeImage(url: String) {
    val qrcodeColor = ColorPrimary
    val qrcodePainter: Painter = rememberQrCodePainter(url) {
        //可以在二维码加logo 但是没什么必要
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
        contentDescription = "二维码",
        modifier = Modifier.size(150.dp).clickable {
            qrcodePainter.toByteArray(1024, 1024, ImageFormat.PNG)
        }
    )
}