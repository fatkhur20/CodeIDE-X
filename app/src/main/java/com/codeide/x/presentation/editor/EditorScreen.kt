package com.codeide.x.presentation.editor

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.codeide.x.presentation.editor.components.CodeEditorView
import com.codeide.x.presentation.editor.components.SearchBar
import com.codeide.x.presentation.editor.components.TabBar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: EditorViewModel = hiltViewModel(),
    onNavigateToExplorer: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val activeTab = uiState.tabs.find { it.id == uiState.activeTabId }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = activeTab?.fileName ?: "CodeIDE-X",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleSearchBar() }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    if (activeTab?.isModified == true) {
                        IconButton(onClick = { viewModel.saveCurrentFile() }) {
                            Icon(Icons.Default.Save, contentDescription = "Save")
                        }
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
            // Tab Bar
            TabBar(
                tabs = uiState.tabs,
                activeTabId = uiState.activeTabId,
                onTabSelect = { viewModel.selectTab(it) },
                onTabClose = { viewModel.closeTab(it) }
            )

            // Search Bar
            if (uiState.isSearchVisible) {
                SearchBar(
                    searchQuery = uiState.searchQuery,
                    replaceQuery = uiState.replaceQuery,
                    onSearchQueryChange = { viewModel.updateSearchQuery(it) },
                    onReplaceQueryChange = { viewModel.updateReplaceQuery(it) },
                    onClose = { viewModel.toggleSearchBar() },
                    onReplace = { /* Replace implementation */ }
                )
            }

            // Code Editor
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
                            viewModel.updateTabContent(activeTab.id, newContent)
                        }
                    )
                } else {
                    // Empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
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
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(onClick = onNavigateToExplorer) {
                                Text("Open Explorer")
                            }
                        }
                    }
                }
            }
        }
    }
}