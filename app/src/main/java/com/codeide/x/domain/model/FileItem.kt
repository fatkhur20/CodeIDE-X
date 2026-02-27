package com.codeide.x.domain.model

data class FileItem(
    val name: String,
    val path: String,
    val isDirectory: Boolean,
    val extension: String? = null,
    val size: Long = 0,
    val lastModified: Long = 0
)