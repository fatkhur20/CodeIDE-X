package com.codeide.x.presentation.explorer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.codeide.x.domain.model.FileItem

@Composable
fun FileTreeItem(
    file: FileItem,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    val backgroundColor = if (isSelected) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        Color.Transparent
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = if (file.isDirectory) {
                if (isSelected) Icons.Default.FolderOpen else Icons.Default.Folder
            } else {
                getFileIcon(file.extension)
            },
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = if (file.isDirectory) {
                Color(0xFFFFB74D)
            } else {
                getFileColor(file.extension)
            }
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = file.name,
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.weight(1f))

        if (!file.isDirectory && file.size > 0) {
            Text(
                text = formatFileSize(file.size),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun getFileIcon(extension: String?) = when (extension?.lowercase()) {
    "kt", "kts" -> Icons.Default.Code
    "java" -> Icons.Default.Code
    "py" -> Icons.Default.Code
    "js", "ts", "jsx", "tsx" -> Icons.Default.Code
    "html", "htm" -> Icons.Default.Html
    "css", "scss", "sass" -> Icons.Default.Palette
    "json" -> Icons.Default.DataObject
    "xml" -> Icons.Default.Code
    "md", "markdown" -> Icons.Default.Description
    "txt" -> Icons.Default.TextSnippet
    "png", "jpg", "jpeg", "gif", "svg" -> Icons.Default.Image
    "mp3", "wav", "ogg" -> Icons.Default.AudioFile
    "mp4", "avi", "mkv" -> Icons.Default.VideoFile
    "zip", "rar", "7z", "tar", "gz" -> Icons.Default.Archive
    "pdf" -> Icons.Default.PictureAsPdf
    else -> Icons.Default.InsertDriveFile
}

private fun getFileColor(extension: String?) = when (extension?.lowercase()) {
    "kt" -> Color(0xFF7E57C2)
    "java" -> Color(0xFFE65100)
    "py" -> Color(0xFF306998)
    "js", "ts" -> Color(0xFFF7DF1E)
    "html" -> Color(0xFFE34F26)
    "css" -> Color(0xFF1572B6)
    "json" -> Color(0xFF000000)
    "md" -> Color(0xFF083FA1)
    else -> Color(0xFF757575)
}

private fun formatFileSize(size: Long): String {
    return when {
        size < 1024 -> "$size B"
        size < 1024 * 1024 -> "${size / 1024} KB"
        size < 1024 * 1024 * 1024 -> "${size / (1024 * 1024)} MB"
        else -> "${size / (1024 * 1024 * 1024)} GB"
    }
}