package com.codeide.x.presentation.navigation

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.codeide.x.presentation.editor.EditorScreen
import com.codeide.x.presentation.editor.EditorViewModel
import com.codeide.x.presentation.explorer.ExplorerScreen

sealed class Screen(val route: String) {
    object Editor : Screen("editor")
    object Explorer : Screen("explorer")
}

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController()
) {
    val editorViewModel: EditorViewModel = hiltViewModel()
    val uiState by editorViewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Screen.Editor.route
    ) {
        composable(Screen.Editor.route) {
            EditorScreen(
                viewModel = editorViewModel,
                onNavigateToExplorer = {
                    navController.navigate(Screen.Explorer.route)
                }
            )
        }

        composable(Screen.Explorer.route) {
            ExplorerScreen(
                onFileSelected = { filePath, fileName ->
                    editorViewModel.openFile(filePath, fileName)
                    navController.popBackStack()
                },
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}