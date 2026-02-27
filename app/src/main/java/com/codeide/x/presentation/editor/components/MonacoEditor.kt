package com.codeide.x.presentation.editor.components

import android.annotation.SuppressLint
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
    var currentContent by remember(content) { mutableStateOf(content) }
    var currentLanguage by remember(language) { mutableStateOf(language ?: "plaintext") }

    val monacoHtml = remember {
        """
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
                require.config({ paths: { vs: 'https://cdn.jsdelivr.net/npm/monaco-editor@0.45.0/min/vs' }});
                
                require(['vs/editor/editor.main'], function() {
                    var editor = monaco.editor.create(document.getElementById('editor'), {
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
                        padding: { top: 8 }
                    });

                    window.getEditorContent = function() {
                        return editor.getValue();
                    };

                    window.setEditorContent = function(content) {
                        if (editor.getValue() !== content) {
                            editor.setValue(content);
                        }
                    };

                    window.setEditorLanguage = function(lang) {
                        monaco.editor.setModelLanguage(editor.getModel(), lang);
                    };

                    editor.onDidChangeModelContent(function() {
                        window.android.onContentChange(editor.getValue());
                    });

                    window.onload = function() {
                        window.android.onEditorReady();
                    };
                });
            </script>
        </body>
        </html>
        """.trimIndent()
    }

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
                
                webViewClient = object : WebViewClient() {
                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (isEditorReady) {
                            evaluateJavascript("window.setEditorContent('$currentContent');", null)
                            evaluateJavascript("window.setEditorLanguage('$currentLanguage');", null)
                        }
                    }
                }

                addJavascriptInterface(
                    AndroidInterface(
                        onReady = { isEditorReady = true },
                        onContentChanged = { newContent ->
                            if (newContent != currentContent) {
                                currentContent = newContent
                                onContentChange(newContent)
                            }
                        }
                    ),
                    "android"
                )

                loadDataWithBaseURL(null, monacoHtml, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            if (isEditorReady) {
                val escapedContent = content.replace("\\", "\\\\").replace("'", "\\'").replace("\n", "\\n").replace("\r", "")
                webView.evaluateJavascript("window.setEditorContent('$escapedContent');", null)
                
                val lang = language ?: "plaintext"
                if (lang != currentLanguage) {
                    currentLanguage = lang
                    webView.evaluateJavascript("window.setEditorLanguage('$lang');", null)
                }
            }
        }
    )
}

class AndroidInterface(
    private val onReady: () -> Unit,
    private val onContentChanged: (String) -> Unit
) {
    @android.webkit.JavascriptInterface
    fun onEditorReady() {
        onReady()
    }

    @android.webkit.JavascriptInterface
    fun onContentChange(content: String) {
        onContentChanged(content)
    }
}
