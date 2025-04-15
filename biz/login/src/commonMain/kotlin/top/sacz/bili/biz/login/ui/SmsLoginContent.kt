package top.sacz.bili.biz.login.ui


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import bilicompose.biz.login.generated.resources.Res
import bilicompose.biz.login.generated.resources.text_enter_phone_number
import bilicompose.biz.login.generated.resources.text_verification_code
import bilicompose.biz.login.generated.resources.title_mobile_phone_number_login
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.api.Response
import top.sacz.bili.biz.login.model.Captcha
import top.sacz.bili.biz.login.model.Country
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.model.VerifyResult
import top.sacz.bili.biz.login.viewmodel.GeeTestViewModel
import top.sacz.bili.biz.login.viewmodel.SmsLoginViewModel
import top.sacz.bili.shared.common.logger.Logger
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 手机号验证码登录
 */
@OptIn(ExperimentalTime::class)
@Composable
fun SmsLoginContent(
    modifier: Modifier,
    viewModel: SmsLoginViewModel = viewModel(),
    geeTestViewModel: GeeTestViewModel = viewModel()
) {
    //手机号输入
    var inputPhoneNumber by rememberSaveable {
        mutableStateOf("")
    }
    //验证码
    var inputVerificationCode by rememberSaveable {
        mutableStateOf("")
    }

    //打开选择国际代号弹窗
    var openSelectAnAreaCode by remember {
        mutableStateOf(false)
    }

    //国家列表包含区号
    val countryList by viewModel.countryList.collectAsState()
    //请求申请极验的结果
    val geetest by geeTestViewModel.captcha.collectAsState()
    //获取国家区号
    LaunchedEffect(Unit) {
        viewModel.getCountryCode()
    }
    //用户选择的国家区号
    var areaCode by remember {
        mutableStateOf(Country("中国", "86", 1))
    }
    //展示人机验证的dialog
    var showVerificationDialog by remember {
        mutableStateOf(false)
    }
    //人机验证结果
    var geetestVerificationResult by remember {
        mutableStateOf(VerifyResult(null, "await", Clock.System.now().epochSeconds))
    }
    val countdown by viewModel.sendCountdown.collectAsState()

    //订阅人机验证结果
    LaunchedEffect(geetestVerificationResult) {
        when (geetestVerificationResult.eventType) {
            "success" -> {
                val geetestResult = geetestVerificationResult.data!!
                Logger.d("人机验证成功")
                showVerificationDialog = false
                viewModel.sendSms(
                    areaCode.id.toString(),
                    inputPhoneNumber,
                    (geetest as Response.Success<Captcha>).data.token,
                    (geetest as Response.Success<Captcha>).data.geetest.challenge,
                    geetestResult.geetestValidate,
                    geetestResult.geetestSeccode
                )
            }

            "error" -> {
                Logger.d("人机验证失败")
                showVerificationDialog = false
            }

            "close" -> {
                Logger.d("人机验证关闭")
                showVerificationDialog = false
            }
        }
    }
    val toaster = rememberToasterState()
    BehavioralValidationDialog(
        geetest = geetest,
        visible = showVerificationDialog,
        verifyCallback = { verifyResults ->
            Logger.d("人机验证结果：$verifyResults")
            geetestVerificationResult = verifyResults
        },
        onDismiss = {
            showVerificationDialog = false
        }
    )
    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Toaster(state = toaster)
        Text(stringResource(Res.string.title_mobile_phone_number_login))
        Spacer(modifier = Modifier.height(20.dp))
        //手机号输入框
        OutlinedTextField(
            value = inputPhoneNumber,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    inputPhoneNumber = input
                }
            },
            label = { Text(stringResource(Res.string.text_enter_phone_number)) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            leadingIcon = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(start = 10.dp)
                ) {
                    Icon(imageVector = Icons.Rounded.Call, contentDescription = "")
                    TextButton(onClick = { openSelectAnAreaCode = true }) {
                        Text(text = "+${areaCode.countryId}")
                    }
                    _SelectNumberArea(
                        show = openSelectAnAreaCode,
                        response = countryList,
                        onSelect = { select ->
                            areaCode = select
                        },
                        onDismiss = { openSelectAnAreaCode = false }
                    )
                }
            }
        )
        //验证码输入框
        OutlinedTextField(
            value = inputVerificationCode,
            onValueChange = { input ->
                if (input.all { it.isDigit() }) {
                    inputVerificationCode = input
                }
            },
            label = { Text(stringResource(Res.string.text_verification_code)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Sms, contentDescription = "")
            },
            trailingIcon = {
                //点击获取验证码后进行人机验证
                TextButton(
                    onClick = {
                        showVerificationDialog = true
                        geeTestViewModel.getGeeTestCaptcha()
                    },
                    enabled = countdown == 0
                ) {
                    Text(text = if (countdown == 0) "获取验证码" else "${countdown}s")
                }
            }
        )
    }
}

/**
 * 选择国际区号弹窗
 */
@Composable
private fun _SelectNumberArea(
    show: Boolean,
    response: Response<CountryList>,
    onSelect: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = show,
        onDismissRequest = onDismiss,
    ) {
        when (response) {
            is Response.Success -> {
                //常用国家区号列表
                response.data.common.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Text(text = "${country.cname} +${country.countryId}")
                        },
                        onClick = {
                            onSelect(country)
                            onDismiss()
                        }
                    )
                }
                //其他国家区号列表
                response.data.others.forEach { country ->
                    DropdownMenuItem(
                        text = {
                            Text(text = "${country.cname} +${country.countryId}")
                        },
                        onClick = {
                            onSelect(country)
                            onDismiss()
                        }
                    )
                }
            }
            //加载中...
            is Response.Loading -> {
                DropdownMenuItem(
                    text = {
                        LoadingDots(modifier = Modifier.fillMaxWidth())
                    },
                    onClick = {}
                )
            }

            else -> {

            }
        }
    }
}

/**
 * 三点 加载动画
 */
@Composable
private fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 8.dp
) {
    val delays = listOf(0, 200, 400) // 每个点的动画延迟
    val infiniteTransition = rememberInfiniteTransition()

    Row(
        modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        delays.forEachIndexed { index, delay ->
            val alpha by infiniteTransition.animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = keyframes {
                        durationMillis = 900
                        0.3f at delay
                        1f at delay + 300
                        0.3f at delay + 600
                    },
                    repeatMode = RepeatMode.Restart
                )
            )
            Box(
                modifier = Modifier
                    .size(dotSize)
                    .graphicsLayer { this.alpha = alpha }
                    .background(color, CircleShape)
                    .padding(2.dp)
            )
            if (index != delays.lastIndex) Spacer(Modifier.width(4.dp))
        }
    }
}
