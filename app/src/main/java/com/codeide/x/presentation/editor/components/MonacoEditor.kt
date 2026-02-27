package com.codeide.x.presentation.editor.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun MonacoEditor(
    content: String,
    language: String?,
    modifier: Modifier = Modifier,
    onContentChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    var isEditorReady by remember { mutableStateOf(false) }

    val monacoHtml = """
<!DOCTYPE html>
<html>
<head>
    <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no">
    <style>
        * { margin: 0; padding: 0; box-sizing: border-box; }
        html, body { width: 100%; height: 100%; overflow: hidden; background: #1e1e1e; }
        #editor { width: 100%; height: 100%; }
    </style>
    <script src="https://cdn.jsdelivr.net/npm/monaco-editor@0.45.0/min/vs/loader.js"></script>
</head>
<body>
    <div id="editor"></div>
    <script>
        var editor = null;
        
        require.config({ paths: { vs: 'https://cdn.jsdelivr.net/npm/monaco-editor@0.45.0/min/vs' }});
        
        require(['vs/editor/editor.main'], function() {
            editor = monaco.editor.create(document.getElementById('editor'), {
                value: '',
                language: 'plaintext',
                theme: 'vs-dark',
                automaticLayout: true,
                fontSize: 14,
                fontFamily: 'Consolas, Monaco, monospace',
                minimap: { enabled: false },
                scrollBeyondLastLine: false,
                wordWrap: 'on',
                lineNumbers: 'on',
                renderWhitespace: 'selection',
                tabSize: 4,
                insertSpaces: true,
                padding: { top: 8 },
                readOnly: false
            });

            editor.onDidChangeModelContent(function() {
                try {
                    window.android.onContentChange(editor.getValue());
                } catch(e) {}
            });

            window.android.onEditorReady();
        });

        function setContent(text) {
            if (editor && editor.getValue() !== text) {
                editor.setValue(text);
            }
        }

        function setLanguage(lang) {
            if (editor) {
                monaco.editor.setModelLanguage(editor.getModel(), lang);
            }
        }
    </script>
</body>
</html>
"""

    AndroidView(
        modifier = modifier.fillMaxSize(),
        factory = { ctx ->
            WebView(ctx).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.allowFileAccess = true
                settings.allowContentAccess = true
                settings.loadWithOverviewMode = true
                settings.useWideViewPort = true
                settings.builtInZoomControls = false
                settings.displayZoomControls = false
                settings.setSupportZoom(false)
                settings.cacheMode = WebView.LOAD_NO_CACHE
                
                webChromeClient = WebChromeClient()

                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                    }
                }

                addJavascriptInterface(
                    JSInterface(
                        onReady = { isEditorReady = true },
                        onContentChanged = onContentChange
                    ),
                    "android"
                )

                loadDataWithBaseURL("https://cdn.jsdelivr.net", monacoHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            if (isEditorReady) {
                try {
                    val safeContent = content
                        .replace("\\", "\\\\")
                        .replace("'", "\\'")
                        .replace("\n", "\\n")
                        .replace("\r", "")
                        .replace("\t", "\\t")
                    webView.evaluateJavascript("setContent('$safeContent');", null)
                    
                    language?.let { lang ->
                        webView.evaluateJavascript("setLanguage('$lang');", null)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    )
}

class JSInterface(
    private val onReady: () -> Unit,
    private val onContentChanged: (String) -> Unit
) {
    @JavascriptInterface
    fun onEditorReady() {
        onReady()
    }

    @JavascriptInterface
    fun onContentChange(content: String) {
        onContentChanged(content)
    }
}
