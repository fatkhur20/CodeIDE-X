package com.codeide.x.presentation.editor.components

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.codeide.x.data.repository.FileRepository

@Composable
fun CodeEditorView(
    content: String,
    language: String?,
    modifier: Modifier = Modifier,
    onContentChange: (String) -> Unit = {}
) {
    var text by remember(content) { mutableStateOf(content) }
    
    val displayLanguage = language?.let {
        FileRepository().getLanguageFromExtension(it)
    } ?: "Plain Text"

    OutlinedTextField(
        value = text,
        onValueChange = { 
            text = it
            onContentChange(it)
        },
        modifier = modifier.fillMaxSize(),
        textStyle = TextStyle(
            fontFamily = FontFamily.Monospace
        ),
        label = { Text("$displayLanguage") }
    )
}
