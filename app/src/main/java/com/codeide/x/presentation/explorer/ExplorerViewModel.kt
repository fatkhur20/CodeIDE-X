package com.codeide.x.presentation.explorer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.codeide.x.data.repository.FileRepository
import com.codeide.x.domain.model.FileItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File

data class ExplorerUiState(
    val currentPath: String = "",
    val files: List<FileItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedFile: FileItem? = null,
    val isCreateDialogVisible: Boolean = false,
    val isRenameDialogVisible: Boolean = false,
    val isDeleteDialogVisible: Boolean = false,
    val dialogFileName: String = ""
)

class ExplorerViewModel(
    private val fileRepository: FileRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExplorerUiState())
    val uiState: StateFlow<ExplorerUiState> = _uiState.asStateFlow()

    init {
        // Don't auto-load - show "open folder" message initially
    }

    fun loadFiles(path: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            try {
                val files = fileRepository.getFiles(path)
                _uiState.update { it.copy(currentPath = path, files = files, isLoading = false) }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    fun navigateToFolder(path: String) {
        loadFiles(path)
    }

    fun navigateUp() {
        val currentPath = _uiState.value.currentPath
        if (currentPath.isNotEmpty()) {
            val parentPath = File(currentPath).parent
            if (parentPath != null) {
                loadFiles(parentPath)
            }
        }
    }

    fun selectFile(file: FileItem) {
        _uiState.update { it.copy(selectedFile = file) }
    }

    fun clearSelection() {
        _uiState.update { it.copy(selectedFile = null) }
    }

    fun showCreateDialog() {
        _uiState.update { it.copy(isCreateDialogVisible = true, dialogFileName = "") }
    }

    fun hideCreateDialog() {
        _uiState.update { it.copy(isCreateDialogVisible = false, dialogFileName = "") }
    }

    fun updateDialogFileName(name: String) {
        _uiState.update { it.copy(dialogFileName = name) }
    }

    fun createFile(isDirectory: Boolean) {
        viewModelScope.launch {
            val name = _uiState.value.dialogFileName
            val parentPath = _uiState.value.currentPath
            if (name.isNotBlank()) {
                fileRepository.createFile(parentPath, name, isDirectory)
                hideCreateDialog()
                loadFiles(parentPath)
            }
        }
    }

    fun showRenameDialog() {
        val selectedFile = _uiState.value.selectedFile
        if (selectedFile != null) {
            _uiState.update { it.copy(isRenameDialogVisible = true, dialogFileName = selectedFile.name) }
        }
    }

    fun hideRenameDialog() {
        _uiState.update { it.copy(isRenameDialogVisible = false, dialogFileName = "") }
    }

    fun renameFile() {
        viewModelScope.launch {
            val selectedFile = _uiState.value.selectedFile ?: return@launch
            val newName = _uiState.value.dialogFileName
            if (newName.isNotBlank() && newName != selectedFile.name) {
                fileRepository.renameFile(selectedFile.path, newName)
                hideRenameDialog()
                loadFiles(_uiState.value.currentPath)
            }
        }
    }

    fun showDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = true) }
    }

    fun hideDeleteDialog() {
        _uiState.update { it.copy(isDeleteDialogVisible = false) }
    }

    fun deleteFile() {
        viewModelScope.launch {
            val selectedFile = _uiState.value.selectedFile ?: return@launch
            fileRepository.deleteFile(selectedFile.path)
            hideDeleteDialog()
            clearSelection()
            loadFiles(_uiState.value.currentPath)
        }
    }

    fun getCurrentPath(): String = _uiState.value.currentPath

    fun getDefaultPath(): String = fileRepository.getDefaultPath()
}