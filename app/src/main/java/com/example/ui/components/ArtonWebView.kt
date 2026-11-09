package com.example.ui.components

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.view.ViewGroup
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.data.adblock.AdBlockEngine
import com.example.data.model.BrowserTab
import com.example.data.model.DevConsoleMessage
import com.example.inspect.InspectElementBridge

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun ArtonWebView(
    tab: BrowserTab,
    isAdBlockEnabled: Boolean,
    isPowerSaverEnabled: Boolean,
    isForceDarkWebEnabled: Boolean,
    backTrigger: Int = 0,
    forwardTrigger: Int = 0,
    onUrlChanged: (url: String, title: String?, canGoBack: Boolean, canGoForward: Boolean) -> Unit,
    onProgressChanged: (progress: Int, isLoading: Boolean) -> Unit,
    onAdBlocked: () -> Unit,
    onCheckUrlLock: (String) -> Boolean,
    onConsoleLog: (DevConsoleMessage) -> Unit,
    onDomExtracted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val inspectBridge = remember(tab.id) {
        InspectElementBridge(
            onConsoleMessage = onConsoleLog,
            onDomExtracted = onDomExtracted
        )
    }

    val webView = remember(tab.id) {
        WebView(context).apply {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                databaseEnabled = true
                allowContentAccess = true
                allowFileAccess = false
                mediaPlaybackRequiresUserGesture = true
                useWideViewPort = true
                loadWithOverviewMode = true
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = if (tab.isPrivate) WebSettings.LOAD_NO_CACHE else WebSettings.LOAD_DEFAULT
            }

            // Register JS Bridge for Inspect Element
            addJavascriptInterface(inspectBridge, InspectElementBridge.JS_INTERFACE_NAME)

            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                    val targetUrl = request?.url?.toString() ?: return false
                    // Check URL lock
                    if (onCheckUrlLock(targetUrl)) {
                        return true // Intercept navigation for security lock
                    }
                    return false
                }

                override fun shouldInterceptRequest(
                    view: WebView?,
                    request: WebResourceRequest?
                ): WebResourceResponse? {
                    if (isAdBlockEnabled) {
                        val reqUrl = request?.url?.toString()
                        if (AdBlockEngine.isAdUrl(reqUrl)) {
                            AdBlockEngine.recordBlock(tab.id)
                            onAdBlocked()
                            return AdBlockEngine.createEmptyResponse()
                        }
                    }
                    return super.shouldInterceptRequest(view, request)
                }

                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                    super.onPageStarted(view, url, favicon)
                    onProgressChanged(10, true)
                    if (url != null) {
                        onUrlChanged(url, view?.title, view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                    }

                    // Injects console interceptor early
                    view?.evaluateJavascript(InspectElementBridge.INJECT_CONSOLE_INTERCEPTOR, null)
                }

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)
                    onProgressChanged(100, false)
                    if (url != null) {
                        onUrlChanged(url, view?.title, view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                    }

                    // Cosmetic ad hiding
                    if (isAdBlockEnabled) {
                        view?.evaluateJavascript(AdBlockEngine.COSMETIC_FILTER_JS, null)
                    }

                    // Force dark mode if enabled
                    if (isForceDarkWebEnabled) {
                        view?.evaluateJavascript(AdBlockEngine.FORCE_DARK_MODE_JS, null)
                    }

                    // Console interceptor fallback
                    view?.evaluateJavascript(InspectElementBridge.INJECT_CONSOLE_INTERCEPTOR, null)

                    // Auto extract DOM for inspect element cache
                    view?.evaluateJavascript(InspectElementBridge.EXTRACT_DOM_SCRIPT, null)
                }
            }

            webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    super.onProgressChanged(view, newProgress)
                    onProgressChanged(newProgress, newProgress < 100)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    super.onReceivedTitle(view, title)
                    val currentUrl = view?.url ?: tab.url
                    onUrlChanged(currentUrl, title, view?.canGoBack() ?: false, view?.canGoForward() ?: false)
                }

                override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                    if (consoleMessage != null) {
                        val level = when (consoleMessage.messageLevel()) {
                            ConsoleMessage.MessageLevel.ERROR -> "error"
                            ConsoleMessage.MessageLevel.WARNING -> "warn"
                            ConsoleMessage.MessageLevel.LOG -> "log"
                            else -> "info"
                        }
                        onConsoleLog(
                            DevConsoleMessage(
                                level = level,
                                message = "[${consoleMessage.sourceId()}:${consoleMessage.lineNumber()}] ${consoleMessage.message()}"
                            )
                        )
                    }
                    return super.onConsoleMessage(consoleMessage)
                }
            }
        }
    }

    // Handle desktop mode toggle
    LaunchedEffect(tab.isDesktopMode) {
        if (tab.isDesktopMode) {
            webView.settings.userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36"
        } else {
            webView.settings.userAgentString = null
        }
    }

    // Handle reader mode
    LaunchedEffect(tab.isReaderMode) {
        if (tab.isReaderMode) {
            val readerCss = """
                (function() {
                    var s = document.createElement('style');
                    s.id = 'arton-reader-style';
                    s.innerHTML = 'body { max-width: 680px !important; margin: 20px auto !important; font-family: Georgia, serif !important; font-size: 18px !important; line-height: 1.7 !important; padding: 20px !important; background: #FAF9F6 !important; color: #111 !important; } nav, header, footer, aside, .sidebar, .ads { display: none !important; }';
                    document.head.appendChild(s);
                })();
            """.trimIndent()
            webView.evaluateJavascript(readerCss, null)
        } else {
            val removeReader = "(function() { var el = document.getElementById('arton-reader-style'); if (el) el.remove(); })();"
            webView.evaluateJavascript(removeReader, null)
        }
    }

    // Handle force dark mode toggle dynamically
    LaunchedEffect(isForceDarkWebEnabled) {
        if (isForceDarkWebEnabled) {
            webView.evaluateJavascript(AdBlockEngine.FORCE_DARK_MODE_JS, null)
        } else {
            webView.evaluateJavascript(AdBlockEngine.REMOVE_FORCE_DARK_MODE_JS, null)
        }
    }

    // Handle URL navigation
    LaunchedEffect(tab.url) {
        if (tab.url.isNotBlank() && tab.url != "about:blank" && tab.url != "about:home") {
            if (webView.url != tab.url) {
                webView.loadUrl(tab.url)
            }
        }
    }

    LaunchedEffect(backTrigger) {
        if (backTrigger > 0 && webView.canGoBack()) {
            webView.goBack()
        }
    }

    LaunchedEffect(forwardTrigger) {
        if (forwardTrigger > 0 && webView.canGoForward()) {
            webView.goForward()
        }
    }

    // Low Battery Optimization: pause WebView timers if power saver is on and tab is backgrounded
    LaunchedEffect(isPowerSaverEnabled) {
        if (isPowerSaverEnabled) {
            webView.settings.offscreenPreRaster = false
        }
    }

    DisposableEffect(tab.id) {
        onDispose {
            if (tab.isPrivate) {
                webView.clearCache(true)
                webView.clearHistory()
                webView.clearFormData()
            }
            webView.stopLoading()
            webView.destroy()
        }
    }

    AndroidView(
        factory = { webView },
        modifier = modifier.fillMaxSize()
    )
}
