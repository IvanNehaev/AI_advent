package com.nihao.ai_adventurer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nihao.ai_adventurer.api.RetrofitInstance
import com.nihao.ai_adventurer.config.SystemPrompts
import com.nihao.ai_adventurer.data.ChatMessage
import com.nihao.ai_adventurer.data.ChatRequest
import com.nihao.ai_adventurer.data.LLMJsonResponse
import com.nihao.ai_adventurer.data.LLMProvider
import com.nihao.ai_adventurer.data.Message
import com.nihao.ai_adventurer.data.ModelInfo
import com.nihao.ai_adventurer.util.TokenEstimator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val conversationHistory = mutableListOf<ChatMessage>()
    private val gson = Gson()
    
    private val _currentPromptId = MutableStateFlow("questions_response")
    val currentPromptId: StateFlow<String> = _currentPromptId.asStateFlow()
    
    private val _temperature = MutableStateFlow(1.0f)
    val temperature: StateFlow<Float> = _temperature.asStateFlow()
    
    // Статистика токенов для текущего диалога (от API)
    private val _totalPromptTokens = MutableStateFlow(0)
    val totalPromptTokens: StateFlow<Int> = _totalPromptTokens.asStateFlow()
    
    private val _totalCompletionTokens = MutableStateFlow(0)
    val totalCompletionTokens: StateFlow<Int> = _totalCompletionTokens.asStateFlow()
    
    private val _totalTokens = MutableStateFlow(0)
    val totalTokens: StateFlow<Int> = _totalTokens.asStateFlow()
    
    // Локальная оценка токенов
    private val _estimatedPromptTokens = MutableStateFlow(0)
    val estimatedPromptTokens: StateFlow<Int> = _estimatedPromptTokens.asStateFlow()
    
    private val _estimatedCompletionTokens = MutableStateFlow(0)
    val estimatedCompletionTokens: StateFlow<Int> = _estimatedCompletionTokens.asStateFlow()
    
    private val _estimatedTotalTokens = MutableStateFlow(0)
    val estimatedTotalTokens: StateFlow<Int> = _estimatedTotalTokens.asStateFlow()
    
    // LLM Provider
    private val _currentProvider = MutableStateFlow(LLMProvider.DEEPSEEK)
    val currentProvider: StateFlow<LLMProvider> = _currentProvider.asStateFlow()
    
    // Модели
    private val _availableModels = MutableStateFlow<List<ModelInfo>>(emptyList())
    val availableModels: StateFlow<List<ModelInfo>> = _availableModels.asStateFlow()
    
    private val _selectedModel = MutableStateFlow("deepseek-chat")
    val selectedModel: StateFlow<String> = _selectedModel.asStateFlow()
    
    private val _modelsLoading = MutableStateFlow(false)
    val modelsLoading: StateFlow<Boolean> = _modelsLoading.asStateFlow()
    
    init {
        // Добавляем системный промпт в начале диалога
        initializeWithPrompt("empty")
        // Загружаем список доступных моделей
        loadAvailableModels()
    }
    
    /**
     * Инициализирует диалог с выбранным промптом
     */
    private fun initializeWithPrompt(promptId: String) {
        val prompt = SystemPrompts.getPromptById(promptId)
        if (prompt != null) {
            conversationHistory.clear()
            conversationHistory.add(
                ChatMessage(
                    role = "system",
                    content = prompt.content
                )
            )
            _currentPromptId.value = promptId
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return

        // Добавляем сообщение пользователя
        val userMessage = Message(text = text, isFromUser = true)
        _messages.value = _messages.value + userMessage

        // Добавляем в историю для контекста
        conversationHistory.add(ChatMessage(role = "user", content = text))

        // Отправляем запрос к API
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            
            // Засекаем время начала запроса
            val requestStartTime = System.currentTimeMillis()

            try {
                val request = ChatRequest(
                    model = _selectedModel.value,
                    messages = conversationHistory.toList(),
                    temperature = _temperature.value
                )
                
                // Оцениваем токены промпта локально перед отправкой
                val estimatedPrompt = TokenEstimator.estimatePromptTokens(conversationHistory.toList())
                _estimatedPromptTokens.value += estimatedPrompt

                val api = RetrofitInstance.getApi(_currentProvider.value)
                val response = api.sendMessage(request)
                
                // Обновляем статистику токенов от API
                response.usage?.let { usage ->
                    _totalPromptTokens.value += usage.promptTokens
                    _totalCompletionTokens.value += usage.completionTokens
                    _totalTokens.value += usage.totalTokens
                }
                
                if (response.choices.isNotEmpty()) {
                    val rawResponse = response.choices[0].message.content
                    
                    // Вычисляем время ответа
                    val responseTime = System.currentTimeMillis() - requestStartTime
                    
                    // Оцениваем токены ответа локально
                    val estimatedCompletion = TokenEstimator.estimateCompletionTokens(rawResponse)
                    _estimatedCompletionTokens.value += estimatedCompletion
                    _estimatedTotalTokens.value = _estimatedPromptTokens.value + _estimatedCompletionTokens.value
                    
                    // Добавляем ответ в историю
                    conversationHistory.add(ChatMessage(role = "assistant", content = rawResponse))
                    
                    // Парсим JSON ответ
                    val parsedResponse = parseJsonResponse(rawResponse)
                    
                    if (parsedResponse != null) {
                        when (parsedResponse.status) {
                            "ok" -> {
                                // Успешный ответ
                                val data = parsedResponse.data
                                if (data != null) {
                                    val aiMessage = Message(
                                        text = data.message,
                                        isFromUser = false,
                                        title = data.title,
                                        isError = false,
                                        tags = data.tags,
                                        urls = data.urls,
                                        responseTimeMs = responseTime
                                    )
                                    _messages.value = _messages.value + aiMessage
                                } else {
                                    // Fallback если data = null
                                    val aiMessage = Message(
                                        text = rawResponse,
                                        isFromUser = false,
                                        responseTimeMs = responseTime
                                    )
                                    _messages.value = _messages.value + aiMessage
                                }
                            }
                            "error" -> {
                                // Ошибка от LLM
                                val error = parsedResponse.error
                                val errorText = error?.message ?: "Неизвестная ошибка"
                                val aiMessage = Message(
                                    text = errorText,
                                    isFromUser = false,
                                    title = "Ошибка: ${error?.code ?: "UNKNOWN"}",
                                    isError = true,
                                    responseTimeMs = responseTime
                                )
                                _messages.value = _messages.value + aiMessage
                            }
                            else -> {
                                // Неизвестный статус
                                val aiMessage = Message(
                                    text = rawResponse,
                                    isFromUser = false,
                                    responseTimeMs = responseTime
                                )
                                _messages.value = _messages.value + aiMessage
                            }
                        }
                    } else {
                        // Не удалось распарсить JSON - показываем как есть
                        val aiMessage = Message(
                            text = rawResponse,
                            isFromUser = false,
                            responseTimeMs = responseTime
                        )
                        _messages.value = _messages.value + aiMessage
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
                
                // Вычисляем время до ошибки
                val responseTime = System.currentTimeMillis() - requestStartTime
                
                // Показываем ошибку в чате
                val errorMsg = Message(
                    text = "Извините, произошла ошибка при отправке сообщения. Попробуйте снова.",
                    isFromUser = false,
                    isError = true,
                    responseTimeMs = responseTime
                )
                _messages.value = _messages.value + errorMsg
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
    
    /**
     * Начать новый диалог (очистить историю)
     */
    fun startNewDialog() {
        _messages.value = emptyList()
        initializeWithPrompt(_currentPromptId.value)
        // Сброс статистики токенов от API
        _totalPromptTokens.value = 0
        _totalCompletionTokens.value = 0
        _totalTokens.value = 0
        // Сброс оценочных токенов
        _estimatedPromptTokens.value = 0
        _estimatedCompletionTokens.value = 0
        _estimatedTotalTokens.value = 0
    }
    
    /**
     * Обновить значение temperature
     */
    fun updateTemperature(newTemperature: Float) {
        _temperature.value = newTemperature.coerceIn(0.0f, 2.0f)
    }
    
    /**
     * Сменить системный промпт
     */
    fun changeSystemPrompt(newPromptId: String) {
        val newPrompt = SystemPrompts.getPromptById(newPromptId)
        if (newPrompt == null) return
        
        // Если история пустая, просто инициализируем с новым промптом
        if (conversationHistory.size <= 1) {
            initializeWithPrompt(newPromptId)
            _currentPromptId.value = newPromptId
            return
        }
        
        // Если есть история, делаем summarize и добавляем новый промпт
        viewModelScope.launch {
            _isLoading.value = true
            
            try {
                // Создаем запрос на summarize
                val summarizeMessages = conversationHistory.toList() + listOf(
                    ChatMessage(
                        role = "user",
                        content = "Суммируй наш диалог в 2-3 предложениях, сохрани ключевую информацию."
                    )
                )
                
                val request = ChatRequest(
                    model = _selectedModel.value,
                    messages = summarizeMessages,
                    temperature = _temperature.value
                )
                
                // Оцениваем токены промпта локально
                val estimatedPrompt = TokenEstimator.estimatePromptTokens(summarizeMessages)
                _estimatedPromptTokens.value += estimatedPrompt
                
                val api = RetrofitInstance.getApi(_currentProvider.value)
                val response = api.sendMessage(request)
                
                // Обновляем статистику токенов от API
                response.usage?.let { usage ->
                    _totalPromptTokens.value += usage.promptTokens
                    _totalCompletionTokens.value += usage.completionTokens
                    _totalTokens.value += usage.totalTokens
                }
                
                if (response.choices.isNotEmpty()) {
                    val summary = response.choices[0].message.content
                    
                    // Оцениваем токены ответа локально
                    val estimatedCompletion = TokenEstimator.estimateCompletionTokens(summary)
                    _estimatedCompletionTokens.value += estimatedCompletion
                    _estimatedTotalTokens.value = _estimatedPromptTokens.value + _estimatedCompletionTokens.value
                    
                    // Очищаем историю и добавляем новый промпт с контекстом
                    conversationHistory.clear()
                    conversationHistory.add(
                        ChatMessage(
                            role = "system",
                            content = newPrompt.content + "\n\nКонтекст предыдущего диалога: $summary"
                        )
                    )
                    
                    _currentPromptId.value = newPromptId
                    
                    // Добавляем сообщение в UI о смене промпта
                    val systemMessage = Message(
                        text = "Системный промпт изменен на: ${newPrompt.name}\nКонтекст сохранен.",
                        isFromUser = false,
                        title = "Настройки обновлены"
                    )
                    _messages.value = _messages.value + systemMessage
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка при смене промпта: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Парсит JSON ответ от LLM
     * Извлекает JSON из markdown блоков если необходимо
     */
    private fun parseJsonResponse(rawResponse: String): LLMJsonResponse? {
        return try {
            // Пробуем распарсить напрямую
            try {
                gson.fromJson(rawResponse, LLMJsonResponse::class.java)
            } catch (e: JsonSyntaxException) {
                // Если не получилось, пробуем извлечь JSON из markdown блока
                val jsonMatch = Regex("```(?:json)?\\s*\\n?([\\s\\S]*?)```").find(rawResponse)
                if (jsonMatch != null) {
                    val jsonContent = jsonMatch.groupValues[1].trim()
                    gson.fromJson(jsonContent, LLMJsonResponse::class.java)
                } else {
                    // Пробуем найти JSON объект в тексте
                    val jsonObjectMatch = Regex("\\{[\\s\\S]*\\}").find(rawResponse)
                    if (jsonObjectMatch != null) {
                        gson.fromJson(jsonObjectMatch.value, LLMJsonResponse::class.java)
                    } else {
                        null
                    }
                }
            }
        } catch (e: Exception) {
            // Не удалось распарсить
            null
        }
    }
    
    /**
     * Загружает список доступных моделей для текущего провайдера
     */
    fun loadAvailableModels() {
        viewModelScope.launch {
            _modelsLoading.value = true
            try {
                val api = RetrofitInstance.getApi(_currentProvider.value)
                val response = api.getModels()
                _availableModels.value = response.data
                
                // Если текущая модель не в списке доступных, выбираем первую доступную
                if (_availableModels.value.isNotEmpty() && 
                    _availableModels.value.none { it.id == _selectedModel.value }) {
                    _selectedModel.value = _availableModels.value[0].id
                }
            } catch (e: Exception) {
                // Если не удалось загрузить, используем стандартный список для провайдера
                _availableModels.value = LLMProvider.getDefaultModels(_currentProvider.value)
                // Выбираем модель по умолчанию для провайдера
                _selectedModel.value = LLMProvider.getDefaultModelId(_currentProvider.value)
            } finally {
                _modelsLoading.value = false
            }
        }
    }
    
    /**
     * Выбирает модель для использования
     */
    fun selectModel(modelId: String) {
        _selectedModel.value = modelId
    }
    
    /**
     * Меняет LLM провайдера
     */
    fun changeProvider(newProvider: LLMProvider) {
        if (_currentProvider.value == newProvider) return
        
        _currentProvider.value = newProvider
        // Выбираем модель по умолчанию для нового провайдера
        _selectedModel.value = LLMProvider.getDefaultModelId(newProvider)
        // Загружаем список моделей для нового провайдера
        loadAvailableModels()
    }
}
