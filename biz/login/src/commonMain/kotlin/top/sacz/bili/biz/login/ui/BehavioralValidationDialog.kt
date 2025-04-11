package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import top.sacz.bili.biz.login.viewmodel.GeeTestViewModel

/**
 * 进行行为验证的Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BehavioralValidationDialog(viewModel: GeeTestViewModel = viewModel()) {
    val geetest by viewModel.captcha.collectAsState()
    LaunchedEffect(UInt) {
        viewModel.getGeeTestCaptcha()
    }
    var isShow by remember {
        mutableStateOf(true)
    }
    if (!isShow) return
    BasicAlertDialog(onDismissRequest = {
        isShow = false
    }) {
        Card(
            modifier = Modifier
                .padding(20.dp)
                .fillMaxWidth()
                .height(500.dp),
        ) {
            BehavioralValidation(geetest) { callbackParam ->

            }
        }
    }
}