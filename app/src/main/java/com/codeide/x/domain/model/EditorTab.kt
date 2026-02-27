package com.codeide.x.domain.model

import com.blacksquircle.ui.language.base.Language

data class EditorTab(
    val id: String,
    val filePath: String,
    val fileName: String,
    val content: String,
    val language: Language?,
    val isModified: Boolean = false,
    val cursorPosition: Int = 0
)