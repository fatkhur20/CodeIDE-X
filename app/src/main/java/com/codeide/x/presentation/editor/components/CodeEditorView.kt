package com.codeide.x.presentation.editor.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

@Composable
fun CodeEditorView(
    content: String,
    language: com.blacksquircle.ui.language.base.Language?,
    modifier: Modifier = Modifier,
    onContentChange: (String) -> Unit = {}
) {
    var text by remember(content) { mutableStateOf(content) }
    
    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            onContentChange(it)
        },
        modifier = modifier.fillMaxSize(),
        textStyle = androidx.compose.ui.text.TextStyle(
            fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
        ),
        label = { Text("Editor") }
    )
}
