package com.codeide.x.presentation.editor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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

    fun openFile(filePath: String, fileName: String, extension: String? = null) {
        viewModelScope.launch {
            val existingTab = _uiState.value.tabs.find { it.filePath == filePath }
            if (existingTab != null) {
                _uiState.update { it.copy(activeTabId = existingTab.id) }
                return@launch
            }

            val content = fileRepository.readFile(filePath)
            val lang = extension ?: fileName.substringAfterLast(".", "")

            val newTab = EditorTab(
                id = UUID.randomUUID().toString(),
                filePath = filePath,
                fileName = fileName,
                content = content,
                language = lang,
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
