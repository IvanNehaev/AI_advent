package com.nihao.ai_adventurer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.nihao.ai_adventurer.database.entities.Dialog
import com.nihao.ai_adventurer.viewmodel.DialogsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DialogsScreen(
    viewModel: DialogsViewModel = viewModel(),
    onNavigateBack: () -> Unit = {},
    onCreateNewDialog: () -> Unit = {},
    onContinueDialog: (Long) -> Unit = {}
) {
    val dialogs by viewModel.dialogs.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var dialogToDelete by remember { mutableStateOf<Dialog?>(null) }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Мои диалоги") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = onCreateNewDialog
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Новый диалог"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading && dialogs.isEmpty()) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (dialogs.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Нет сохраненных диалогов",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Создайте новый диалог для начала общения",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onCreateNewDialog,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Создать новый диалог")
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(
                        items = dialogs,
                        key = { it.id }
                    ) { dialog ->
                        DialogItem(
                            dialog = dialog,
                            onDialogClick = {
                                onContinueDialog(dialog.id)
                            },
                            onDeleteClick = {
                                dialogToDelete = dialog
                            }
                        )
                    }
                }
            }
        }
        
        // Диалог подтверждения удаления
        dialogToDelete?.let { dialog ->
            AlertDialog(
                onDismissRequest = { dialogToDelete = null },
                title = { Text("Удалить диалог?") },
                text = { Text("Вы уверены, что хотите удалить диалог \"${dialog.title}\"?") },
                confirmButton = {
                    TextButton(
                        onClick = {
                            viewModel.deleteDialog(dialog)
                            dialogToDelete = null
                        }
                    ) {
                        Text("Удалить")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { dialogToDelete = null }) {
                        Text("Отмена")
                    }
                }
            )
        }
        
        // Показываем Snackbar при ошибке
        errorMessage?.let { error ->
            LaunchedEffect(error) {
                viewModel.clearError()
            }
        }
    }
}

@Composable
fun DialogItem(
    dialog: Dialog,
    onDialogClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())
    val updatedDate = dateFormat.format(Date(dialog.updatedAt))
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onDialogClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = dialog.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Обновлен: $updatedDate",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (dialog.totalTokens > 0) {
                    Text(
                        text = "Токены: ${dialog.totalTokens}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
            
            IconButton(onClick = onDeleteClick) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Удалить диалог",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}
