package com.nihao.ai_adventurer.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.nihao.ai_adventurer.config.SystemPrompts
import com.nihao.ai_adventurer.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: ChatViewModel,
    onNavigateBack: () -> Unit
) {
    val currentPromptId by viewModel.currentPromptId.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val temperature by viewModel.temperature.collectAsState()
    var showNewDialogDialog by remember { mutableStateOf(false) }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Настройки") },
                    navigationIcon = {
                        IconButton(
                            onClick = onNavigateBack,
                            enabled = !isLoading
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Назад"
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
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Секция: Управление диалогом
                Text(
                    text = "Управление диалогом",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(enabled = !isLoading) { showNewDialogDialog = true }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "🔄 Начать новый диалог",
                            style = MaterialTheme.typography.titleSmall
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Очистить историю и начать с чистого листа",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Системный промпт
                Text(
                    text = "Системный промпт",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Выберите режим работы ассистента",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Список промптов
                SystemPrompts.allPrompts.forEach { prompt ->
                    PromptSelectionCard(
                        prompt = prompt,
                        isSelected = currentPromptId == prompt.id,
                        isEnabled = !isLoading,
                        onSelect = { viewModel.changeSystemPrompt(prompt.id) }
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Секция: Temperature
                Text(
                    text = "Temperature",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )

                Text(
                    text = "Контролирует случайность ответов. Меньше = более предсказуемо, больше = более креативно",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Значение:",
                                style = MaterialTheme.typography.titleSmall
                            )
                            Text(
                                text = String.format("%.1f", temperature),
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Slider(
                            value = temperature,
                            onValueChange = { viewModel.updateTemperature(it) },
                            valueRange = 0.0f..2.0f,
                            steps = 19, // 20 шагов по 0.1
                            enabled = !isLoading,
                            modifier = Modifier.fillMaxWidth()
                        )
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "0.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "1.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "2.0",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Overlay с индикатором загрузки
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(enabled = false) {} // Блокировка кликов
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier
                            .fillMaxSize(),
                        color = MaterialTheme.colorScheme.background.copy(alpha = 0.7f)
                    ) {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(64.dp),
                                strokeWidth = 4.dp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = "Смена системного промпта...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Сохраняем контекст диалога",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }
        }
    }
    
    // Диалог подтверждения нового диалога
    if (showNewDialogDialog) {
        AlertDialog(
            onDismissRequest = { showNewDialogDialog = false },
            title = { Text("Начать новый диалог?") },
            text = { Text("История текущего диалога будет удалена. Это действие нельзя отменить.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.startNewDialog()
                        showNewDialogDialog = false
                        onNavigateBack()
                    }
                ) {
                    Text("Начать")
                }
            },
            dismissButton = {
                TextButton(onClick = { showNewDialogDialog = false }) {
                    Text("Отмена")
                }
            }
        )
    }
}

@Composable
fun PromptSelectionCard(
    prompt: com.nihao.ai_adventurer.config.PromptItem,
    isSelected: Boolean,
    isEnabled: Boolean = true,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(enabled = isEnabled, onClick = onSelect),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                enabled = isEnabled
            )
            
            Spacer(modifier = Modifier.width(12.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = prompt.name,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = prompt.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }
        }
    }
}
