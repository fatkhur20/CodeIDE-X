package com.codeide.x.presentation.editor.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Replace
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SearchBar(
    searchQuery: String,
    replaceQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onReplaceQueryChange: (String) -> Unit,
    onClose: () -> Unit,
    onReplace: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showReplace by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        shadowElevation = 4.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("Search") },
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodySmall
                )

                IconButton(onClick = { /* Previous match */ }) {
                    Icon(Icons.Default.KeyboardArrowUp, contentDescription = "Previous")
                }

                IconButton(onClick = { /* Next match */ }) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = "Next")
                }

                IconButton(onClick = { showReplace = !showReplace }) {
                    Icon(Icons.Default.Replace, contentDescription = "Replace")
                }

                IconButton(onClick = onClose) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            AnimatedVisibility(visible = showReplace) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = replaceQuery,
                        onValueChange = onReplaceQueryChange,
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Replace with") },
                        singleLine = true,
                        textStyle = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onReplace,
                        modifier = Modifier.height(48.dp)
                    ) {
                        Text("Replace")
                    }
                }
            }
        }
    }
}