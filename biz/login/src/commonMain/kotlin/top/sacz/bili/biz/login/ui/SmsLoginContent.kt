package top.sacz.bili.biz.login.ui


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material.icons.rounded.Sms
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import bilicompose.biz.login.generated.resources.Res
import bilicompose.biz.login.generated.resources.text_enter_phone_number
import bilicompose.biz.login.generated.resources.text_verification_code
import bilicompose.biz.login.generated.resources.title_mobile_phone_number_login
import org.jetbrains.compose.resources.stringResource

/**
 * 手机号验证码登录
 */
@Composable
fun SmsLoginContent(modifier: Modifier) {
    //手机号输入
    var inputPhoneNumber by rememberSaveable {
        mutableStateOf("")
    }
    //验证码
    var inputVerificationCode by rememberSaveable {
        mutableStateOf("")
    }

    //选择国际代号
    var selectAnAreaCode by rememberSaveable {
        mutableStateOf(false)
    }

    val menuItemData = List(100) { "Option ${it + 1}" }
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
                        selectAnAreaCode = true
                    }) {
                        Text("+86")
                    }

                    DropdownMenu(
                        expanded = selectAnAreaCode,
                        onDismissRequest = { selectAnAreaCode = false }
                    ) {
                        menuItemData.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = { selectAnAreaCode = false }
                            )
                        }
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