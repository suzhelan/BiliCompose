package top.suzhelan.bili.biz.login.ui.content

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import qrgenerator.qrkitpainter.PatternType
import qrgenerator.qrkitpainter.QrBallType
import qrgenerator.qrkitpainter.QrFrameType
import qrgenerator.qrkitpainter.QrKitBrush
import qrgenerator.qrkitpainter.QrKitColors
import qrgenerator.qrkitpainter.QrKitShapes
import qrgenerator.qrkitpainter.QrPixelType
import qrgenerator.qrkitpainter.customBrush
import qrgenerator.qrkitpainter.getSelectedFrameShape
import qrgenerator.qrkitpainter.getSelectedPattern
import qrgenerator.qrkitpainter.getSelectedPixel
import qrgenerator.qrkitpainter.getSelectedQrBall
import qrgenerator.qrkitpainter.rememberQrKitPainter
import top.suzhelan.bili.api.BiliResponse
import top.suzhelan.bili.biz.login.model.TvQRCode
import top.suzhelan.bili.biz.login.viewmodel.QRCodeLoginViewModel
import top.suzhelan.bili.shared.auth.config.LoginMapper
import top.suzhelan.bili.shared.common.ui.LoadingIndicator
import top.suzhelan.bili.shared.common.ui.dialog.MessageDialog
import top.suzhelan.bili.shared.common.ui.theme.ColorPrimary
import top.suzhelan.bili.shared.navigation.LocalNavigation
import top.suzhelan.bili.shared.navigation.currentOrThrow

/**
 * 扫码登录组件 比较简单
 * 1. 获取二维码和qrcodeKey
 * 2. 用qrcode轮询二维码状态
 * 技术难点只有轮询时间校准和任务取消
 */
@Composable
fun QRCodeLoginContent(viewModel: QRCodeLoginViewModel = viewModel { QRCodeLoginViewModel() }) {
    //获取最近的导航 登录成功后pop当前页面
    val getQRCode by viewModel.qrCode.collectAsStateWithLifecycle()
    val sendCountdown by viewModel.sendCountdown.collectAsStateWithLifecycle()
    val queryMessage by viewModel.queryMessage.collectAsStateWithLifecycle()
    val isShowLoginSuccessDialog by viewModel.isShowLoginSuccessDialog.collectAsStateWithLifecycle()
    if (isShowLoginSuccessDialog) {
        LoginSuccessDialog()
    }
    LaunchedEffect(Unit) {
        viewModel.getQRCode()
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
//    val centerLogo = painterResource(Res.drawable)
    val painter = rememberQrKitPainter(url) {
        shapes = QrKitShapes(
            //方块内心
            ballShape = getSelectedQrBall(QrBallType.RoundCornersQrBall(0.2f)),
            //像素点
            darkPixelShape = getSelectedPixel(QrPixelType.RoundCornerPixel()),
            //方块边框
            frameShape = getSelectedFrameShape(QrFrameType.RoundCornersFrame(0.2f)),
            //整个二维码的形状
            codeShape = getSelectedPattern(PatternType.SquarePattern),
        )
        colors = QrKitColors(
            darkBrush = QrKitBrush.customBrush {
                Brush.linearGradient(
                    0f to qrcodeColor,
                    1f to qrcodeColor,
                    end = Offset(it, it)
                )
            }
        )
//        logo = QrKitLogo(centerLogo)
    }
    Image(
        painter = painter,
        contentDescription = "二维码",
        modifier = Modifier.size(150.dp).clickable {

        }
    )
}