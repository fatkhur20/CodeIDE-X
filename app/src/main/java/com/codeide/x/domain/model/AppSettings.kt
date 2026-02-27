package com.codeide.x.domain.model

data class AppSettings(
    val theme: AppTheme = AppTheme.DARK,
    val editorTheme: String = "monokai",
    val fontSize: Int = 14,
    val wordWrap: Boolean = true,
    val showLineNumbers: Boolean = true,
    val autoSave: Boolean = false
)

enum class AppTheme { LIGHT, DARK, SYSTEM }