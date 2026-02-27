package com.codeide.x.data.repository

import android.os.Environment
import com.codeide.x.domain.model.FileItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

class FileRepository {

    private val rootDirectory: File
        get() = Environment.getExternalStorageDirectory()

    suspend fun getFiles(path: String): List<FileItem> = withContext(Dispatchers.IO) {
        val directory = if (path.isEmpty()) rootDirectory else File(path)
        if (!directory.exists() || !directory.isDirectory) {
            return@withContext emptyList()
        }
        
        directory.listFiles()?.map { file ->
            FileItem(
                name = file.name,
                path = file.absolutePath,
                isDirectory = file.isDirectory,
                extension = if (file.isFile) file.extension else null,
                size = file.length(),
                lastModified = file.lastModified()
            )
        }?.sortedWith(compareBy<FileItem> { !it.isDirectory }.thenBy { it.name.lowercase() }) ?: emptyList()
    }

    suspend fun readFile(path: String): String = withContext(Dispatchers.IO) {
        val file = File(path)
        if (file.exists() && file.isFile) {
            file.readText()
        } else {
            ""
        }
    }

    suspend fun writeFile(path: String, content: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            file.writeText(content)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun createFile(parentPath: String, name: String, isDirectory: Boolean): Boolean = withContext(Dispatchers.IO) {
        try {
            val parentDir = if (parentPath.isEmpty()) rootDirectory else File(parentPath)
            val newFile = File(parentDir, name)
            if (isDirectory) {
                newFile.mkdir()
            } else {
                newFile.createNewFile()
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteFile(path: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val file = File(path)
            if (file.isDirectory) {
                file.deleteRecursively()
            } else {
                file.delete()
            }
        } catch (e: Exception) {
            false
        }
    }

    suspend fun renameFile(oldPath: String, newName: String): Boolean = withContext(Dispatchers.IO) {
        try {
            val oldFile = File(oldPath)
            val newFile = File(oldFile.parent, newName)
            oldFile.renameTo(newFile)
        } catch (e: Exception) {
            false
        }
    }

    fun getDefaultPath(): String = rootDirectory.absolutePath

    fun getLanguageFromExtension(extension: String): String {
        return when (extension.lowercase()) {
            "kt", "kts" -> "kotlin"
            "java" -> "java"
            "py" -> "python"
            "js" -> "javascript"
            "ts" -> "typescript"
            "html", "htm" -> "html"
            "css" -> "css"
            "json" -> "json"
            "xml" -> "xml"
            "go" -> "go"
            "rs" -> "rust"
            "c", "h" -> "c"
            "cpp", "cc", "cxx", "hpp" -> "cpp"
            "md", "markdown" -> "markdown"
            "txt" -> "plaintext"
            "sh", "bash" -> "bash"
            "sql" -> "sql"
            "yaml", "yml" -> "yaml"
            "toml" -> "toml"
            "gradle" -> "groovy"
            else -> "plaintext"
        }
    }
}