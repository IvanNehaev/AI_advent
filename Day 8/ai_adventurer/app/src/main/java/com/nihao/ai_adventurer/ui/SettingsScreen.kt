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
import com.nihao.ai_adventurer.data.LLMProvider
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
    val totalTokens by viewModel.totalTokens.collectAsState()
    val totalPromptTokens by viewModel.totalPromptTokens.collectAsState()
    val totalCompletionTokens by viewModel.totalCompletionTokens.collectAsState()
    val estimatedTotalTokens by viewModel.estimatedTotalTokens.collectAsState()
    val estimatedPromptTokens by viewModel.estimatedPromptTokens.collectAsState()
    val estimatedCompletionTokens by viewModel.estimatedCompletionTokens.collectAsState()
    val availableModels by viewModel.availableModels.collectAsState()
    val selectedModel by viewModel.selectedModel.collectAsState()
    val modelsLoading by viewModel.modelsLoading.collectAsState()
    val currentProvider by viewModel.currentProvider.collectAsState()
    var showNewDialogDialog by remember { mutableStateOf(false) }
    var expandedModels by remember { mutableStateOf(false) }
    
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
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Секция: Выбор провайдера
                Text(
                    text = "LLM Провайдер",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Выберите провайдера для языковой модели",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                // Список провайдеров
                LLMProvider.entries.forEach { provider ->
                    ProviderSelectionCard(
                        provider = provider,
                        isSelected = currentProvider == provider,
                        isEnabled = !isLoading,
                        onSelect = { viewModel.changeProvider(provider) }
                    )
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Секция: Выбор модели
                Text(
                    text = "Модель",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Выберите языковую модель для работы",
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
                        Text(
                            text = "Доступные модели:",
                            style = MaterialTheme.typography.titleSmall,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        
                        ExposedDropdownMenuBox(
                            expanded = expandedModels,
                            onExpandedChange = { 
                                if (!isLoading && !modelsLoading) {
                                    expandedModels = !expandedModels
                                }
                            }
                        ) {
                            OutlinedTextField(
                                value = selectedModel,
                                onValueChange = {},
                                readOnly = true,
                                enabled = !isLoading && !modelsLoading,
                                trailingIcon = {
                                    if (modelsLoading) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(20.dp),
                                            strokeWidth = 2.dp
                                        )
                                    } else {
                                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedModels)
                                    }
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                colors = OutlinedTextFieldDefaults.colors()
                            )
                            
                            ExposedDropdownMenu(
                                expanded = expandedModels,
                                onDismissRequest = { expandedModels = false }
                            ) {
                                if (availableModels.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { Text("Нет доступных моделей") },
                                        onClick = { },
                                        enabled = false
                                    )
                                } else {
                                    availableModels.forEach { model ->
                                        DropdownMenuItem(
                                            text = { 
                                                Column {
                                                    Text(
                                                        text = model.id,
                                                        style = MaterialTheme.typography.bodyMedium
                                                    )
                                                    // Добавляем описание для известных моделей
                                                    when (model.id) {
                                                        "deepseek-chat" -> Text(
                                                            text = "Универсальная модель для диалога",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "deepseek-reasoner" -> Text(
                                                            text = "Модель для сложных задач и рассуждений",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "mistral-small-latest" -> Text(
                                                            text = "Компактная и быстрая модель",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "mistral-medium-latest" -> Text(
                                                            text = "Сбалансированная модель",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "mistral-large-latest" -> Text(
                                                            text = "Самая мощная модель Mistral",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "open-mistral-7b" -> Text(
                                                            text = "Open-source модель 7B параметров",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "open-mixtral-8x7b" -> Text(
                                                            text = "Open-source Mixture of Experts модель",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                        "open-mixtral-8x22b" -> Text(
                                                            text = "Open-source продвинутая MoE модель",
                                                            style = MaterialTheme.typography.bodySmall,
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    }
                                                }
                                            },
                                            onClick = {
                                                viewModel.selectModel(model.id)
                                                expandedModels = false
                                            }
                                        )
                                    }
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // Описание выбранной модели
                        when (selectedModel) {
                            "deepseek-chat" -> {
                                Text(
                                    text = "💬 DeepSeek Chat - универсальная модель для диалога и общих задач. Быстрая и эффективная.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "deepseek-reasoner" -> {
                                Text(
                                    text = "🧠 DeepSeek Reasoner (R1) - специализированная модель для сложных логических задач, математики и программирования. Использует цепочку рассуждений.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "mistral-small-latest" -> {
                                Text(
                                    text = "⚡ Mistral Small - компактная и быстрая модель для повседневных задач. Оптимальная для простых запросов.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "mistral-medium-latest" -> {
                                Text(
                                    text = "⚖️ Mistral Medium - сбалансированная модель среднего размера. Хороша для большинства задач.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "mistral-large-latest" -> {
                                Text(
                                    text = "🚀 Mistral Large - самая мощная модель Mistral AI. Для сложных и комплексных задач.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "open-mistral-7b" -> {
                                Text(
                                    text = "🔓 Open Mistral 7B - open-source модель с 7 миллиардами параметров. Быстрая и эффективная.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "open-mixtral-8x7b" -> {
                                Text(
                                    text = "🎯 Open Mixtral 8x7B - open-source модель с архитектурой Mixture of Experts. Отличный баланс производительности.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            "open-mixtral-8x22b" -> {
                                Text(
                                    text = "💎 Open Mixtral 8x22B - продвинутая open-source MoE модель. Высокая производительность для сложных задач.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            else -> {
                                Text(
                                    text = "ℹ️ Выбрана модель: $selectedModel",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Секция: Статистика токенов
                Text(
                    text = "Статистика токенов",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Использование токенов в текущем диалоге",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Card(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // Заголовок секции фактических токенов
                        Text(
                            text = "Фактические токены (от API)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Общее количество токенов от API
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔢 Всего токенов:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalTokens.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        // Детализация фактических токенов
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📥 Промпт токены:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalPromptTokens.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📤 Токены ответов:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = totalCompletionTokens.toString(),
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                        
                        HorizontalDivider()
                        
                        // Заголовок секции оценочных токенов
                        Text(
                            text = "Оценочные токены (локальный подсчет)",
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.tertiary
                        )
                        
                        // Общее количество оценочных токенов
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "🔢 Всего токенов:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = estimatedTotalTokens.toString(),
                                style = MaterialTheme.typography.titleLarge,
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                        
                        // Детализация оценочных токенов
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📥 Промпт токены:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = estimatedPromptTokens.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "📤 Токены ответов:",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = estimatedCompletionTokens.toString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        if (totalTokens > 0 || estimatedTotalTokens > 0) {
                            HorizontalDivider()
                            
                            // Сравнение точности оценки
                            if (totalTokens > 0 && estimatedTotalTokens > 0) {
                                val difference = kotlin.math.abs(estimatedTotalTokens - totalTokens)
                                val deviation = (difference.toFloat() / totalTokens * 100).toInt()
                                val isOverestimate = estimatedTotalTokens > totalTokens
                                
                                val deviationText = if (deviation == 0) {
                                    "📊 Точность: идеальное совпадение!"
                                } else {
                                    val direction = if (isOverestimate) "переоценка" else "недооценка"
                                    "📊 Отклонение: $deviation% ($direction на ${difference} токенов)"
                                }
                                
                                Text(
                                    text = deviationText,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                            
                            // Примерная стоимость (если есть фактические данные)
                            if (totalTokens > 0) {
                                Text(
                                    text = "💡 Примерная стоимость: $${String.format("%.4f", totalTokens * 0.00027 / 1000)}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
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

@Composable
fun ProviderSelectionCard(
    provider: LLMProvider,
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
                    text = provider.displayName,
                    style = MaterialTheme.typography.titleSmall,
                    color = if (isSelected) {
                        MaterialTheme.colorScheme.onPrimaryContainer
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = provider.description,
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
