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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Login
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
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
import bilicompose.biz.login.generated.resources.error_code_1002
import bilicompose.biz.login.generated.resources.error_code_1003
import bilicompose.biz.login.generated.resources.error_code_1006
import bilicompose.biz.login.generated.resources.error_code_1007
import bilicompose.biz.login.generated.resources.error_code_1025
import bilicompose.biz.login.generated.resources.error_code_2400
import bilicompose.biz.login.generated.resources.error_code_2406
import bilicompose.biz.login.generated.resources.error_code_86203
import bilicompose.biz.login.generated.resources.text_enter_phone_number
import bilicompose.biz.login.generated.resources.text_login
import bilicompose.biz.login.generated.resources.text_send_verification_code
import bilicompose.biz.login.generated.resources.text_verification_code
import bilicompose.biz.login.generated.resources.title_mobile_phone_number_login
import bilicompose.biz.login.generated.resources.toast_phone_number_is_not_valid
import bilicompose.biz.login.generated.resources.verify_error
import bilicompose.biz.login.generated.resources.verify_success
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.api.BiliResponse
import top.sacz.bili.biz.login.model.Captcha
import top.sacz.bili.biz.login.model.Country
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.model.SendSmsLoginCodeResult
import top.sacz.bili.biz.login.model.VerifyResult
import top.sacz.bili.biz.login.viewmodel.GeeTestViewModel
import top.sacz.bili.biz.login.viewmodel.SmsLoginViewModel
import top.sacz.bili.shared.common.ui.theme.ColorPrimary
import top.sacz.bili.shared.navigation.LocalNavigation
import top.sacz.bili.shared.navigation.currentOrThrow
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * 手机号验证码登录
 */
@OptIn(ExperimentalTime::class)
@Composable
fun SmsLoginContent(
    smsLoginViewModel: SmsLoginViewModel = viewModel(),
    geeTestViewModel: GeeTestViewModel = viewModel(),
    showToast: (String) -> Unit = {}
) {
    //获取最近的导航 登录成功后pop当前页面
    val navigator = LocalNavigation.currentOrThrow

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
    val countryList by smsLoginViewModel.countryList.collectAsState()
    //请求申请极验的结果
    val geetest by geeTestViewModel.captcha.collectAsState()
    //获取国家区号
    LaunchedEffect(Unit) {
        smsLoginViewModel.getCountryCode()
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
    //验证码发送倒计时 60 ... 0
    val countdown by smsLoginViewModel.sendCountdown.collectAsState()
    //发送验证码结果
    val sendSmsResult by smsLoginViewModel.sendSmsResult.collectAsState()

    val verifyPhoneNumber by remember {
        derivedStateOf {
            val countryId = areaCode.countryId
            val number = inputPhoneNumber
            when (countryId) {
                "86" -> number.length == 11 && number.all { it.isDigit() }
                else -> number.isNotBlank()
            }
        }
    }
    //toast内容
    val verifySuccess = stringResource(Res.string.verify_success)
    val verifyError = stringResource(Res.string.verify_error)
    val toastPhoneNumberIsNotValid = stringResource(Res.string.toast_phone_number_is_not_valid)

    //请求发送验证码错误提示码
    val sendSmsErrorMessages = mapOf(
        1002 to stringResource(Res.string.error_code_1002),
        86203 to stringResource(Res.string.error_code_86203),
        1003 to stringResource(Res.string.error_code_1003),
        1025 to stringResource(Res.string.error_code_1025),
        2400 to stringResource(Res.string.error_code_2400),
        2406 to stringResource(Res.string.error_code_2406)
    )
    LaunchedEffect(sendSmsResult) {
        if (sendSmsResult is BiliResponse.Error) {
            val response = sendSmsResult as BiliResponse.Error
            if (response.code == 0) return@LaunchedEffect
            showToast(
                "code:${response.code} " + (sendSmsErrorMessages[response.code] ?: response.msg)
            )
        }
    }
    LaunchedEffect(geetest) {
        if (geetest is BiliResponse.Error) {
            val response = geetest as BiliResponse.Error
            if (response.code == 0) return@LaunchedEffect
            showVerificationDialog = false
            showToast(
                "code:${response.code} " + (sendSmsErrorMessages[response.code] ?: response.msg)
            )
        }
    }
    //订阅人机验证结果
    LaunchedEffect(geetestVerificationResult) {
        if (geetestVerificationResult.eventType == "await") return@LaunchedEffect
        when (geetestVerificationResult.eventType) {
            "success" -> {
                val geetestResult = geetestVerificationResult.data!!
                showVerificationDialog = false
                smsLoginViewModel.sendSms(
                    areaCode.countryId,
                    inputPhoneNumber,
                    (geetest as BiliResponse.Success<Captcha>).data.token,
                    geetestResult.geetestChallenge,
                    geetestResult.geetestValidate,
                    geetestResult.geetestSeccode
                )
                showToast(verifySuccess)
            }

            "error" -> {
                showToast(verifyError)
                showVerificationDialog = false
            }

        }
    }

    val loginResult by smsLoginViewModel.loginResult.collectAsState()
    val loginErrorMessages = mapOf(
        1002 to stringResource(Res.string.error_code_1006),
        86203 to stringResource(Res.string.error_code_1007),
    )
    LaunchedEffect(loginResult) {
        when (val response = loginResult) {
            is BiliResponse.Success -> {
                navigator.pop()
            }

            is BiliResponse.Error -> {
                showToast(loginErrorMessages[response.code] ?: response.msg)
            }

            else -> {
            }
        }
    }
    BehavioralValidationDialog(
        geetest = geetest,
        visible = showVerificationDialog,
        verifyCallback = { verifyResults ->
            geetestVerificationResult = verifyResults
        },
        onDismiss = {
            showVerificationDialog = false
        }
    )
    Column(
        modifier = Modifier.fillMaxSize().padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.title_mobile_phone_number_login))
        Spacer(modifier = Modifier.height(20.dp))
        //手机号输入框
        OutlinedTextField(
            value = inputPhoneNumber,
            isError = sendSmsResult is BiliResponse.Error,
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
                    SelectNumberArea(
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
            isError = loginResult is BiliResponse.Error,
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
                        if (verifyPhoneNumber) {
                            showVerificationDialog = true
                            //申请人机验证
                            geeTestViewModel.getGeeTestCaptcha(
                                areaCode.countryId,
                                inputPhoneNumber
                            )
                        } else {
                            showToast(toastPhoneNumberIsNotValid)
                        }
                    },
                    enabled = countdown == 0
                ) {
                    Text(text = if (countdown == 0) stringResource(Res.string.text_send_verification_code) else "${countdown}s")
                }
            }
        )
        Spacer(modifier = Modifier.height(30.dp))
        //登录按钮
        OutlinedButton(onClick = {
            smsLoginViewModel.login(
                areaCode.countryId,
                inputPhoneNumber,
                inputVerificationCode,
                (sendSmsResult as BiliResponse.Success<SendSmsLoginCodeResult>).data.captchaKey
            )
        }) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.Login,
                    contentDescription = stringResource(Res.string.text_login),
                    tint = ColorPrimary
                )
                Text(text = stringResource(Res.string.text_login))
            }
        }
    }


}

/**
 * 选择国际区号弹窗
 */
@Composable
private fun SelectNumberArea(
    show: Boolean,
    response: BiliResponse<CountryList>,
    onSelect: (Country) -> Unit,
    onDismiss: () -> Unit
) {
    DropdownMenu(
        expanded = show,
        onDismissRequest = onDismiss,
    ) {
        when (response) {
            is BiliResponse.Success -> {
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
            is BiliResponse.Loading -> {
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
    color: Color = ColorPrimary,
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
