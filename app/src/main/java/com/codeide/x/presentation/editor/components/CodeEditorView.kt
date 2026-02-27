package com.codeide.x.presentation.editor.components

import android.widget.EditText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import com.blacksquircle.ui.editorkit.plugin.TextProcessor
import com.blacksquircle.ui.editorkit.themes.TextTheme
import com.blacksquircle.ui.language.base.Language

@Composable
fun CodeEditorView(
    content: String,
    language: Language?,
    modifier: Modifier = Modifier,
    onContentChange: (String) -> Unit = {}
) {
    val context = LocalContext.current
    
    val textProcessor = remember {
        TextProcessor(context).apply {
            language?.let { this.language = it }
            setTextTheme(TextTheme.MONOKAI)
        }
    }

    DisposableEffect(content) {
        if (textProcessor.text.toString() != content) {
            textProcessor.setTextContent(content)
        }
        onDispose { }
    }

    AndroidView(
        factory = { textProcessor },
        modifier = modifier,
        update = { view ->
            language?.let { view.language = it }
        }
    )
}