package com.codeide.x.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.blacksquircle.ui.language.c.CLanguage
import com.blacksquircle.ui.language.cpp.CppLanguage
import com.blacksquircle.ui.language.css.CssLanguage
import com.blacksquircle.ui.language.go.GoLanguage
import com.blacksquircle.ui.language.html.HtmlLanguage
import com.blacksquircle.ui.language.java.JavaLanguage
import com.blacksquircle.ui.language.javascript.JavaScriptLanguage
import com.blacksquircle.ui.language.json.JsonLanguage
import com.blacksquircle.ui.language.kotlin.KotlinLanguage
import com.blacksquircle.ui.language.plaintext.PlaintextLanguage
import com.blacksquircle.ui.language.python.PythonLanguage
import com.blacksquircle.ui.language.rust.RustLanguage
import com.blacksquircle.ui.language.typescript.TypeScriptLanguage
import com.blacksquircle.ui.language.xml.XmlLanguage
import com.codeide.x.data.repository.FileRepository
import com.codeide.x.domain.model.EditorTab
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID

data class EditorUiState(
    val tabs: List<EditorTab> = emptyList(),
    val activeTabId: String? = null,
    val searchQuery: String = "",
    val replaceQuery: String = "",
    val isSearchVisible: Boolean = false,
    val currentFilePath: String = ""
)

class EditorViewModel(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private val languageMap = mapOf(
        "kotlin" to KotlinLanguage(),
        "java" to JavaLanguage(),
        "python" to PythonLanguage(),
        "javascript" to JavaScriptLanguage(),
        "typescript" to TypeScriptLanguage(),
        "html" to HtmlLanguage(),
        "css" to CssLanguage(),
        "json" to JsonLanguage(),
        "xml" to XmlLanguage(),
        "go" to GoLanguage(),
        "rust" to RustLanguage(),
        "c" to CLanguage(),
        "cpp" to CppLanguage(),
        "plaintext" to PlaintextLanguage()
    )

    fun openFile(filePath: String, fileName: String) {
        viewModelScope.launch {
            val existingTab = _uiState.value.tabs.find { it.filePath == filePath }
            if (existingTab != null) {
                _uiState.update { it.copy(activeTabId = existingTab.id) }
                return@launch
            }

            val content = fileRepository.readFile(filePath)
            val extension = fileName.substringAfterLast(".", "")
            val languageName = fileRepository.getLanguageFromExtension(extension)
            val language = languageMap[languageName] ?: PlaintextLanguage()

            val newTab = EditorTab(
                id = UUID.randomUUID().toString(),
                filePath = filePath,
                fileName = fileName,
                content = content,
                language = language,
                isModified = false
            )

            _uiState.update { state ->
                state.copy(
                    tabs = state.tabs + newTab,
                    activeTabId = newTab.id,
                    currentFilePath = filePath
                )
            }
        }
    }

    fun closeTab(tabId: String) {
        _uiState.update { state ->
            val updatedTabs = state.tabs.filter { it.id != tabId }
            val newActiveTabId = if (state.activeTabId == tabId) {
                updatedTabs.lastOrNull()?.id
            } else {
                state.activeTabId
            }
            state.copy(tabs = updatedTabs, activeTabId = newActiveTabId)
        }
    }

    fun selectTab(tabId: String) {
        _uiState.update { it.copy(activeTabId = tabId) }
    }

    fun updateTabContent(tabId: String, newContent: String) {
        _uiState.update { state ->
            state.copy(
                tabs = state.tabs.map { tab ->
                    if (tab.id == tabId) {
                        tab.copy(content = newContent, isModified = true)
                    } else tab
                }
            )
        }
    }

    fun saveCurrentFile() {
        viewModelScope.launch {
            val activeTab = _uiState.value.tabs.find { it.id == _uiState.value.activeTabId }
            if (activeTab != null) {
                val success = fileRepository.writeFile(activeTab.filePath, activeTab.content)
                if (success) {
                    _uiState.update { state ->
                        state.copy(
                            tabs = state.tabs.map { tab ->
                                if (tab.id == activeTab.id) {
                                    tab.copy(isModified = false)
                                } else tab
                            }
                        )
                    }
                }
            }
        }
    }

    fun toggleSearchBar() {
        _uiState.update { it.copy(isSearchVisible = !it.isSearchVisible) }
    }

    fun updateSearchQuery(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun updateReplaceQuery(query: String) {
        _uiState.update { it.copy(replaceQuery = query) }
    }

    fun getActiveTab(): EditorTab? {
        return _uiState.value.tabs.find { it.id == _uiState.value.activeTabId }
    }
}