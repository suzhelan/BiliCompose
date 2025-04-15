package top.sacz.bili.biz.login.ui


import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.background
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
import org.jetbrains.compose.resources.stringResource
import top.sacz.bili.api.Response
import top.sacz.bili.biz.login.model.Country
import top.sacz.bili.biz.login.model.CountryList
import top.sacz.bili.biz.login.viewmodel.SmsLoginViewModel

/**
 * 手机号验证码登录
 */
@Composable
fun SmsLoginContent(modifier: Modifier, viewModel: SmsLoginViewModel = viewModel()) {
    //手机号输入
    var inputPhoneNumber by rememberSaveable {
        mutableStateOf("")
    }
    //验证码
    var inputVerificationCode by rememberSaveable {
        mutableStateOf("")
    }

    //打开选择国际代号弹窗
    var openSelectAnAreaCode by rememberSaveable {
        mutableStateOf(false)
    }

    //国家列表包含区号
    val countryList by viewModel.countryList.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.getCountryCode()
    }
    var areaCode by remember {
        mutableStateOf(Country("中国", "86", 0))
    }

    Column(
        modifier = modifier.padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(stringResource(Res.string.title_mobile_phone_number_login))
        Spacer(modifier = Modifier.height(20.dp))
        OutlinedTextField(
            value = inputPhoneNumber,
            onValueChange = { input ->
                // Filter to allow only digits
                if (input.all { it.isDigit() }) {
                    inputPhoneNumber = input
                }
            },
            label = { Text(stringResource(Res.string.text_enter_phone_number)) },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    //电话图标
                    Icon(imageVector = Icons.Rounded.Call, contentDescription = "")
                    //区号文本
                    TextButton(onClick = {
                        openSelectAnAreaCode = true
                    }) {
                        Text(text = "+${areaCode.countryId}")
                    }
                    if (openSelectAnAreaCode && countryList is Response.Success<CountryList>)
                        _SelectNumberArea(
                            countryList as Response.Success<CountryList>
                        ) { select ->
                            openSelectAnAreaCode = false
                            areaCode = select
                        }
                }
            }
        )
        OutlinedTextField(
            value = inputVerificationCode,
            onValueChange = { input ->
                // Filter to allow only digits
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
            }
        )
    }
}

@Composable
private fun _SelectNumberArea(
    response: Response.Success<CountryList>,
    onSelect: (Country) -> Unit
) {
    var expanded by rememberSaveable {
        mutableStateOf(true)
    }
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = {
            expanded = false
        },
    ) {
        response.data.common.forEach {
            DropdownMenuItem(
                text = {
                    Text(text = "${it.cname} +${it.countryId}")
                },
                onClick = {
                    expanded = false
                    onSelect(it)
                }
            )
        }
        response.data.others.forEach {
            DropdownMenuItem(
                text = {
                    Text(text = "${it.cname} +${it.countryId}")
                },
                onClick = {
                    expanded = false
                    onSelect(it)
                }
            )
        }
    }
}

@Composable
private fun LoadingDots(
    modifier: Modifier = Modifier,
    color: Color = MaterialTheme.colorScheme.primary,
    dotSize: Dp = 8.dp
) {
    val delays = listOf(0, 200, 400) // 每个点的动画延迟
    val infiniteTransition = rememberInfiniteTransition()

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
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
