package com.codeide.x.presentation.explorer

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
import com.codeide.x.presentation.explorer.components.FileTreeItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExplorerScreen(
    viewModel: ExplorerViewModel,
    onFileSelected: (String, String) -> Unit,
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showNewFileDialog by remember { mutableStateOf(false) }
    var showNewFolderDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { viewModel.loadFiles(viewModel.getDefaultPath()) }
                    ) {
                        Icon(
                            Icons.Default.FolderOpen,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Explorer",
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { showNewFileDialog = true }) {
                        Icon(Icons.Default.NoteAdd, contentDescription = "New File")
                    }
                    IconButton(onClick = { showNewFolderDialog = true }) {
                        Icon(Icons.Default.CreateNewFolder, contentDescription = "New Folder")
                    }
                    IconButton(onClick = { onNavigateBack() }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            CurrentPathBar(
                currentPath = uiState.currentPath,
                onNavigateUp = { viewModel.navigateUp() },
                onPathClick = { path -> viewModel.navigateToFolder(path) }
            )

            HorizontalDivider()

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (uiState.files.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.FolderOff,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Empty folder",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(uiState.files) { file ->
                        FileTreeItem(
                            file = file,
                            isSelected = uiState.selectedFile?.path == file.path,
                            onClick = {
                                if (file.isDirectory) {
                                    viewModel.navigateToFolder(file.path)
                                } else {
                                    onFileSelected(file.path, file.name)
                                }
                            },
                            onLongClick = {
                                viewModel.selectFile(file)
                            }
                        )
                    }
                }
            }
        }
    }

    uiState.selectedFile?.let { selectedFile ->
        FileContextMenu(
            file = selectedFile,
            onDismiss = { viewModel.clearSelection() },
            onRename = { viewModel.showRenameDialog() },
            onDelete = { viewModel.showDeleteDialog() }
        )
    }

    if (showNewFileDialog) {
        NewItemDialog(
            title = "New File",
            onDismiss = { showNewFileDialog = false },
            onConfirm = { name ->
                viewModel.updateDialogFileName(name)
                viewModel.createFile(isDirectory = false)
                showNewFileDialog = false
            }
        )
    }

    if (showNewFolderDialog) {
        NewItemDialog(
            title = "New Folder",
            onDismiss = { showNewFolderDialog = false },
            onConfirm = { name ->
                viewModel.updateDialogFileName(name)
                viewModel.createFile(isDirectory = true)
                showNewFolderDialog = false
            }
        )
    }

    if (uiState.isRenameDialogVisible) {
        NewItemDialog(
            title = "Rename",
            initialValue = uiState.dialogFileName,
            onDismiss = { viewModel.hideRenameDialog() },
            onConfirm = { name ->
                viewModel.updateDialogFileName(name)
                viewModel.renameFile()
            }
        )
    }

    if (uiState.isDeleteDialogVisible) {
        AlertDialog(
            onDismissRequest = { viewModel.hideDeleteDialog() },
            title = { Text("Delete") },
            text = { Text("Are you sure you want to delete '${uiState.selectedFile?.name}'?") },
            confirmButton = {
                TextButton(
                    onClick = { viewModel.deleteFile() },
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { viewModel.hideDeleteDialog() }) {
                    Text("Cancel")
                }
            }
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
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onNavigateUp) {
            Icon(Icons.Default.ArrowUpward, contentDescription = "Go up")
        }

        Icon(
            Icons.Default.Folder,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.width(4.dp))

        Row(modifier = Modifier.weight(1f)) {
            pathParts.forEachIndexed { index, part ->
                if (index > 0) {
                    Text("/")
                }
                TextButton(
                    onClick = {
                        val path = "/${pathParts.take(index + 1).joinToString("/")}"
                        onPathClick(path)
                    },
                    contentPadding = PaddingValues(horizontal = 4.dp)
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
private fun FileContextMenu(
    file: FileItem,
    onDismiss: () -> Unit,
    onRename: () -> Unit,
    onDelete: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(file.name) },
        text = {
            Column {
                TextButton(
                    onClick = {
                        onDismiss()
                        onRename()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Edit, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Rename")
                }
                TextButton(
                    onClick = {
                        onDismiss()
                        onDelete()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)
                ) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Delete")
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun NewItemDialog(
    title: String,
    initialValue: String = "",
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var name by remember { mutableStateOf(initialValue) }

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
