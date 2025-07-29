package top.sacz.bili.biz.login.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.core.screen.ScreenKey
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import kotlinx.coroutines.launch


object LoginScreen : Screen {
    enum class Destination(val label: String) {
        SMS("短信登录"),
        QRCODE("二维码登录")
    }

    override val key: ScreenKey
        get() = "/login"

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val scope = rememberCoroutineScope()
        val pagerState =
            rememberPagerState(pageCount = { Destination.entries.size }, initialPage = 0)
        val snackbarHostState = remember { SnackbarHostState() }
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
            },
            snackbarHost = {
                SnackbarHost(hostState = snackbarHostState)
            }
        ) { contentPadding ->

            Column(
                modifier = Modifier.padding(contentPadding)
            ) {
                //tab
                PrimaryTabRow(
                    selectedTabIndex = pagerState.currentPage,
                ) {
                    Destination.entries.forEachIndexed { index, destination ->
                        Tab(
                            selected = pagerState.currentPage == index,
                            onClick = {
                                scope.launch {
                                    pagerState.animateScrollToPage(index)
                                }
                            },
                            text = {
                                Text(
                                    text = destination.label,
                                )
                            }
                        )
                    }
                }

                HorizontalPager(state = pagerState) { page ->
                    when (page) {
                        0 -> SmsLoginContent() { message ->
                            scope.launch {
                                snackbarHostState.showSnackbar(message)
                            }
                        }

                        1 -> QRCodeLoginContent()
                    }
                }
            }
        }
    }
}
