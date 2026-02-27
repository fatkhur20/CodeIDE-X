data class EditorTab(
    val id: String,
    val filePath: String,
    val fileName: String,
    val content: String,
    val language: String? = null,
    val isModified: Boolean = false,
    val cursorPosition: Int = 0
)