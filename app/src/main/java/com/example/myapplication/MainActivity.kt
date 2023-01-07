package com.example.myapplication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.example.myapplication.ui.theme.MyApplicationTheme
import timber.log.Timber
import kotlin.math.roundToInt

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyApplicationTheme {
                App()
            }
        }
    }
}

@Composable
fun App() {
    BasicColumn {
        var webViewState by remember { mutableStateOf(WebViewState(WebContent.Url("https://google.com"))) }
        var addressBarState by remember { mutableStateOf("") }
        var toolbarOffsetHeightPx by remember { mutableStateOf(0f) }
        val webViewNavigator = rememberWebViewNavigator()

        MyTopAppBar(
            modifier = Modifier.offset { IntOffset(0, toolbarOffsetHeightPx.roundToInt()) },
            url = addressBarState,
            onUrlEntered = { addressBarState = it },
            onUrlSubmitted = { webViewState = WebViewState(WebContent.Url(addressBarState)) },
        )

        val localDensity = LocalDensity.current
        WebView(
            modifier = Modifier
                .offset { IntOffset(0, toolbarOffsetHeightPx.roundToInt()) },
            state = webViewState,
            navigator = webViewNavigator,
            client = remember { AccompanistWebViewClient() },
            onWebPageScroll = { x, y, oldX, oldY ->
                val toolbarHeightPx = with(localDensity) { SmallTopAppBarHeight.roundToPx().toFloat() }
                val deltaY = oldY - y
                val newOffset = toolbarOffsetHeightPx + deltaY
                Timber.d("Updated toolbar offset: toolbarOffsetHeightPx=$newOffset")

                toolbarOffsetHeightPx = newOffset.coerceIn(-toolbarHeightPx, 0f)
            }
        )
    }
}