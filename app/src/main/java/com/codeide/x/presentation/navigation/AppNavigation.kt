package com.codeide.x.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codeide.x.data.repository.FileRepository
import com.codeide.x.presentation.editor.EditorScreen
import com.codeide.x.presentation.editor.EditorViewModel
import com.codeide.x.presentation.explorer.ExplorerViewModel

sealed class Screen(val route: String) {
    object Editor : Screen("editor")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val fileRepository = remember { FileRepository() }
    val editorViewModel = remember { EditorViewModel(fileRepository) }
    val explorerViewModel = remember { ExplorerViewModel(fileRepository) }

    NavHost(
        navController = navController,
        startDestination = Screen.Editor.route
    ) {
        composable(Screen.Editor.route) {
            EditorScreen(
                editorViewModel = editorViewModel,
                explorerViewModel = explorerViewModel
            )
        }
    }
}
