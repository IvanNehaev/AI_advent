package com.nihao.ai_adventurer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.nihao.ai_adventurer.ui.ChatScreen
import com.nihao.ai_adventurer.ui.DialogsScreen
import com.nihao.ai_adventurer.ui.SettingsScreen
import com.nihao.ai_adventurer.ui.theme.Ai_adventurerTheme
import com.nihao.ai_adventurer.viewmodel.ChatViewModel
import com.nihao.ai_adventurer.viewmodel.DialogsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Ai_adventurerTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val chatViewModel: ChatViewModel = viewModel()
    val dialogsViewModel: DialogsViewModel = viewModel()
    
    NavHost(
        navController = navController,
        startDestination = "dialogs"
    ) {
        composable("dialogs") {
            DialogsScreen(
                viewModel = dialogsViewModel,
                onNavigateBack = {
                    // Если это главный экран, не делаем ничего
                },
                onCreateNewDialog = {
                    // Создаем новый диалог и переходим к чату
                    chatViewModel.startNewDialog()
                    navController.navigate("chat")
                },
                onContinueDialog = { dialogId ->
                    // Загружаем выбранный диалог и переходим к чату
                    chatViewModel.loadDialog(dialogId)
                    navController.navigate("chat")
                }
            )
        }
        
        composable("chat") {
            ChatScreen(
                viewModel = chatViewModel,
                onNavigateToSettings = {
                    navController.navigate("settings")
                },
                onNavigateToDialogs = {
                    navController.navigate("dialogs")
                }
            )
        }
        
        composable("settings") {
            SettingsScreen(
                viewModel = chatViewModel,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}