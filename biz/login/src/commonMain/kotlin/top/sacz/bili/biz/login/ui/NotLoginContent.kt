package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Call
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import bilicompose.biz.login.generated.resources.title_mobile_phone_number_login
import org.jetbrains.compose.resources.stringResource


@Composable
fun NotLoginContent() {
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SmsLoginDialog() {
    //手机号登录的界面/Dialog也行
    BasicAlertDialog(
        onDismissRequest = { /*TODO*/ },
    ) {
        Card {
            SmsLoginContent()
        }
    }
}


@Composable
fun SmsLoginContent() {
    var inputPhoneNumber by rememberSaveable {
        mutableStateOf("")
    }
    Column(
        modifier = Modifier.padding(20.dp),
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
            label = { Text("Enter Number") },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Number
            ),
            modifier = Modifier
                .fillMaxWidth(),
            leadingIcon = {
                Icon(imageVector = Icons.Rounded.Call, contentDescription = "")
            }
        )

    }
}