package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow

object LoginScreen : Screen {
    override val key: ScreenKey
        get() = "/login"

    @Composable
    override fun Content() {
        _LoginScreen()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun _LoginScreen() {
    val navigator = LocalNavigator.currentOrThrow
    //登录界面内容
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(text = "登录")
                },
                navigationIcon = {
                    Icon(
                        imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                        contentDescription = "返回",
                        modifier = Modifier.clickable {
                            navigator.pop()
                        }
                    )
                }
            )
        }
    ) { paddingValues ->
        SmsLoginContent(modifier = Modifier.padding(paddingValues))
    }
}