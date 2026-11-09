package com.example.inspect

import android.webkit.JavascriptInterface
import com.example.data.model.DevConsoleMessage
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class InspectElementBridge(
    private val onConsoleMessage: (DevConsoleMessage) -> Unit,
    private val onDomExtracted: (String) -> Unit
) {

    @JavascriptInterface
    fun postConsoleLog(level: String, message: String) {
        val entry = DevConsoleMessage(
            level = level.lowercase(),
            message = message,
            timestamp = System.currentTimeMillis()
        )
        onConsoleMessage(entry)
    }

    @JavascriptInterface
    fun postDomContent(html: String) {
        onDomExtracted(html)
    }

    companion object {
        const val JS_INTERFACE_NAME = "ArtonDevBridge"

        // Script to intercept console logs and send them to ArtonDevBridge
        const val INJECT_CONSOLE_INTERCEPTOR = """
            (function() {
                if (window.__arton_dev_installed) return;
                window.__arton_dev_installed = true;

                var oldLog = console.log;
                var oldInfo = console.info;
                var oldWarn = console.warn;
                var oldError = console.error;

                function formatArg(arg) {
                    if (arg === null) return 'null';
                    if (arg === undefined) return 'undefined';
                    if (typeof arg === 'object') {
                        try { return JSON.stringify(arg); } catch(e) { return String(arg); }
                    }
                    return String(arg);
                }

                console.log = function() {
                    var msg = Array.from(arguments).map(formatArg).join(' ');
                    if (window.ArtonDevBridge) window.ArtonDevBridge.postConsoleLog('log', msg);
                    oldLog.apply(console, arguments);
                };

                console.info = function() {
                    var msg = Array.from(arguments).map(formatArg).join(' ');
                    if (window.ArtonDevBridge) window.ArtonDevBridge.postConsoleLog('info', msg);
                    oldInfo.apply(console, arguments);
                };

                console.warn = function() {
                    var msg = Array.from(arguments).map(formatArg).join(' ');
                    if (window.ArtonDevBridge) window.ArtonDevBridge.postConsoleLog('warn', msg);
                    oldWarn.apply(console, arguments);
                };

                console.error = function() {
                    var msg = Array.from(arguments).map(formatArg).join(' ');
                    if (window.ArtonDevBridge) window.ArtonDevBridge.postConsoleLog('error', msg);
                    oldError.apply(console, arguments);
                };

                window.addEventListener('error', function(event) {
                    var msg = event.message + ' at ' + event.filename + ':' + event.lineno;
                    if (window.ArtonDevBridge) window.ArtonDevBridge.postConsoleLog('error', msg);
                });
            })();
        """

        // Script to request full DOM outerHTML
        const val EXTRACT_DOM_SCRIPT = """
            (function() {
                var html = document.documentElement ? document.documentElement.outerHTML : '';
                if (window.ArtonDevBridge) {
                    window.ArtonDevBridge.postDomContent(html);
                }
            })();
        """
    }
}
