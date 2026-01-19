package com.nihao.ai_adventurer.ui

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import com.nihao.ai_adventurer.data.Message
import com.nihao.ai_adventurer.viewmodel.ChatViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    viewModel: ChatViewModel = viewModel(),
    onNavigateToSettings: () -> Unit = {}
) {
    val messages by viewModel.messages.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()
    
    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // Автопрокрутка при добавлении нового сообщения
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            coroutineScope.launch {
                listState.animateScrollToItem(messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("AI Чат") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Настройки"
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
        ) {
            // Список сообщений
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(messages) { message ->
                    MessageBubble(message = message)
                }

                // Анимация загрузки (печатает...)
                if (isLoading) {
                    item {
                        TypingIndicator()
                    }
                }
            }

            // Поле ввода
            Surface(
                shadowElevation = 8.dp,
                tonalElevation = 2.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        placeholder = { 
                            Text(
                                if (isLoading) "Ожидание ответа..." else "Введите сообщение..."
                            ) 
                        },
                        enabled = !isLoading,
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                            disabledContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            disabledIndicatorColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(24.dp),
                        maxLines = 4,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Send
                        ),
                        keyboardActions = KeyboardActions(
                            onSend = {
                                if (messageText.isNotBlank() && !isLoading) {
                                    viewModel.sendMessage(messageText)
                                    messageText = ""
                                }
                            }
                        )
                    )

                    FloatingActionButton(
                        onClick = {
                            if (messageText.isNotBlank() && !isLoading) {
                                viewModel.sendMessage(messageText)
                                messageText = ""
                            }
                        },
                        modifier = Modifier.size(56.dp),
                        containerColor = if (isLoading || messageText.isBlank()) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                        } else {
                            MaterialTheme.colorScheme.primary
                        }
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "Отправить",
                                tint = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                }
            }
        }
    }

    // Показываем Snackbar при ошибке
    errorMessage?.let { error ->
        LaunchedEffect(error) {
            // Можно добавить Snackbar здесь при необходимости
            viewModel.clearError()
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 4.dp,
                bottomEnd = 16.dp
            ),
            color = MaterialTheme.colorScheme.secondaryContainer,
            modifier = Modifier.padding(horizontal = 4.dp)
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    TypingDot(index)
                }
            }
        }
    }
}

@Composable
fun TypingDot(index: Int) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = index * 150),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    
    Box(
        modifier = Modifier
            .size(8.dp)
            .alpha(alpha)
            .background(
                color = MaterialTheme.colorScheme.onSecondaryContainer,
                shape = CircleShape
            )
    )
}

@Composable
fun MessageBubble(message: Message) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (message.isFromUser) Arrangement.End else Arrangement.Start
    ) {
        Surface(
            shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = if (message.isFromUser) 16.dp else 4.dp,
                bottomEnd = if (message.isFromUser) 4.dp else 16.dp
            ),
            color = when {
                message.isError -> MaterialTheme.colorScheme.errorContainer
                message.isFromUser -> MaterialTheme.colorScheme.primaryContainer
                else -> MaterialTheme.colorScheme.secondaryContainer
            },
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Column(
                modifier = Modifier.padding(12.dp)
            ) {
                // Отображаем заголовок если есть
                if (!message.title.isNullOrBlank()) {
                    Text(
                        text = message.title,
                        style = MaterialTheme.typography.titleSmall,
                        color = when {
                            message.isError -> MaterialTheme.colorScheme.onErrorContainer
                            message.isFromUser -> MaterialTheme.colorScheme.onPrimaryContainer
                            else -> MaterialTheme.colorScheme.onSecondaryContainer
                        },
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                
                // Основной текст сообщения
                Text(
                    text = message.text,
                    color = when {
                        message.isError -> MaterialTheme.colorScheme.onErrorContainer
                        message.isFromUser -> MaterialTheme.colorScheme.onPrimaryContainer
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    },
                    style = MaterialTheme.typography.bodyLarge
                )
                
                // Теги (для AI сообщений)
                if (!message.isFromUser && message.tags.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    TagsBlock(
                        tags = message.tags,
                        isError = message.isError
                    )
                }
                
                // Ссылки (для AI сообщений)
                if (!message.isFromUser && message.urls.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    UrlsBlock(
                        urls = message.urls,
                        isError = message.isError
                    )
                }
            }
        }
    }
}

/**
 * Блок с тегами
 */
@Composable
fun TagsBlock(tags: List<String>, isError: Boolean) {
    Column {
        Text(
            text = "🏷️ Теги:",
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            },
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            tags.take(5).forEach { tag ->
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isError) {
                        MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    }
                ) {
                    Text(
                        text = tag,
                        style = MaterialTheme.typography.labelSmall,
                        color = if (isError) {
                            MaterialTheme.colorScheme.onErrorContainer
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }
    }
}

/**
 * Блок со ссылками
 */
@Composable
fun UrlsBlock(urls: List<String>, isError: Boolean) {
    val context = LocalContext.current
    
    Column {
        Text(
            text = "🔗 Ссылки:",
            style = MaterialTheme.typography.labelSmall,
            color = if (isError) {
                MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.7f)
            } else {
                MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            },
            modifier = Modifier.padding(bottom = 4.dp)
        )
        
        urls.forEach { url ->
            Text(
                text = url,
                style = MaterialTheme.typography.labelSmall,
                color = if (isError) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                textDecoration = TextDecoration.Underline,
                modifier = Modifier
                    .padding(vertical = 2.dp)
                    .clickable {
                        try {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                            context.startActivity(intent)
                        } catch (e: Exception) {
                            // Игнорируем ошибку открытия ссылки
                        }
                    }
            )
        }
    }
}

