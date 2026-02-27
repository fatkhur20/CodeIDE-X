package com.codeide.x.presentation.editor

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codeide.x.domain.model.FileItem
import com.codeide.x.presentation.editor.components.CodeEditorView
import com.codeide.x.presentation.editor.components.SearchBar
import com.codeide.x.presentation.editor.components.TabBar
import com.codeide.x.presentation.explorer.ExplorerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    editorViewModel: EditorViewModel,
    explorerViewModel: ExplorerViewModel
) {
    val editorUiState by editorViewModel.uiState.collectAsStateWithLifecycle()
    val explorerUiState by explorerViewModel.uiState.collectAsStateWithLifecycle()
    val activeTab = editorUiState.tabs.find { it.id == editorUiState.activeTabId }

    var isSidebarExpanded by remember { mutableStateOf(true) }
    var showNewFileDialog by remember { mutableStateOf(false) }
    var showNewFolderDialog by remember { mutableStateOf(false) }

    Row(modifier = Modifier.fillMaxSize()) {
        if (isSidebarExpanded) {
            Surface(
                modifier = Modifier
                    .width(280.dp)
                    .fillMaxHeight(),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.surface)
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "EXPLORER",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showNewFileDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.NoteAdd,
                                contentDescription = "New File",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        IconButton(
                            onClick = { showNewFolderDialog = true },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.CreateNewFolder,
                                contentDescription = "New Folder",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    CurrentPathBar(
                        currentPath = explorerUiState.currentPath,
                        onNavigateUp = { explorerViewModel.navigateUp() },
                        onPathClick = { path -> explorerViewModel.navigateToFolder(path) }
                    )

                    HorizontalDivider()

                    if (explorerUiState.isLoading) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(24.dp))
                        }
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(explorerUiState.files) { file ->
                                FileTreeItem(
                                    file = file,
                                    isSelected = explorerUiState.selectedFile?.path == file.path,
                                    onClick = {
                                        if (file.isDirectory) {
                                            explorerViewModel.navigateToFolder(file.path)
                                        } else {
                                            val ext = file.name.substringAfterLast(".", "")
                                            editorViewModel.openFile(file.path, file.name, ext)
                                        }
                                    },
                                    onLongClick = { explorerViewModel.selectFile(file) }
                                )
                            }
                        }
                    }
                }
            }

            VerticalDivider()
        }

        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { isSidebarExpanded = !isSidebarExpanded }) {
                            Icon(Icons.Default.Menu, contentDescription = "Toggle Sidebar")
                        }
                        Text(
                            text = activeTab?.fileName ?: "CodeIDE-X",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { editorViewModel.toggleSearchBar() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    if (activeTab?.isModified == true) {
                        IconButton(onClick = { editorViewModel.saveCurrentFile() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
                    }
                }
            )

            TabBar(
                tabs = editorUiState.tabs,
                activeTabId = editorUiState.activeTabId,
                onTabSelect = { editorViewModel.selectTab(it) },
                onTabClose = { editorViewModel.closeTab(it) }
            )

            if (editorUiState.isSearchVisible) {
                SearchBar(
                    searchQuery = editorUiState.searchQuery,
                    replaceQuery = editorUiState.replaceQuery,
                    onSearchQueryChange = { editorViewModel.updateSearchQuery(it) },
                    onReplaceQueryChange = { editorViewModel.updateReplaceQuery(it) },
                    onClose = { editorViewModel.toggleSearchBar() },
                    onReplace = { }
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
            ) {
                if (activeTab != null) {
                    CodeEditorView(
                        content = activeTab.content,
                        language = activeTab.language,
                        modifier = Modifier.fillMaxSize(),
                        onContentChange = { newContent ->
                            editorViewModel.updateTabContent(activeTab.id, newContent)
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.Code,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "No file open",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Select a file from the explorer",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }

    if (showNewFileDialog) {
        NewItemDialog(
            title = "New File",
            onDismiss = { showNewFileDialog = false },
            onConfirm = { name ->
                explorerViewModel.updateDialogFileName(name)
                explorerViewModel.createFile(isDirectory = false)
                showNewFileDialog = false
            }
        )
    }

    if (showNewFolderDialog) {
        NewItemDialog(
            title = "New Folder",
            onDismiss = { showNewFolderDialog = false },
            onConfirm = { name ->
                explorerViewModel.updateDialogFileName(name)
                explorerViewModel.createFile(isDirectory = true)
                showNewFolderDialog = false
            }
        )
    }
}

@Composable
private fun FileTreeItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) Icons.Default.Folder else Icons.Default.InsertDriveFile,
            contentDescription = null,
            modifier = Modifier.size(18.dp),
            tint = if (file.isDirectory) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.onSurfaceVariant
            }
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = file.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun CurrentPathBar(
    currentPath: String,
    onNavigateUp: () -> Unit,
    onPathClick: (String) -> Unit
) {
    val pathParts = currentPath.split("/").filter { it.isNotEmpty() }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 4.dp, vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateUp, modifier = Modifier.size(24.dp)) {
            Icon(Icons.Default.ArrowUpward, contentDescription = "Go up", modifier = Modifier.size(16.dp))
        }

        Row(modifier = Modifier.weight(1f)) {
            pathParts.takeLast(2).forEachIndexed { index, part ->
                if (index > 0) {
                    Text("/", style = MaterialTheme.typography.bodySmall)
                }
                TextButton(
                    onClick = {
                        val path = "/${pathParts.take(pathParts.size - 2 + index + 1).joinToString("/")}"
                        onPathClick(path)
                    },
                    contentPadding = PaddingValues(horizontal = 2.dp),
                    modifier = Modifier.height(24.dp)
                ) {
                    Text(
                        text = part,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

@Composable
private fun NewItemDialog(
    title: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(name) },
                enabled = name.isNotBlank()
            ) {
                Text("Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
