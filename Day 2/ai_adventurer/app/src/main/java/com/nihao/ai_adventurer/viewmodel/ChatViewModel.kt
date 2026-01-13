package com.nihao.ai_adventurer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.nihao.ai_adventurer.api.RetrofitInstance
import com.nihao.ai_adventurer.data.ChatMessage
import com.nihao.ai_adventurer.data.ChatRequest
import com.nihao.ai_adventurer.data.LLMJsonResponse
import com.nihao.ai_adventurer.data.Message
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
    
    // Системный промпт для структурированных ответов
    private val systemPrompt = """
Ты — backend-сервис.

Ответь СТРОГО в JSON.
Никакого текста вне JSON.
Ответ должен соответствовать схеме:

{
  "status": "ok | error",
  "data": {
    "title": "string",
    "message": "string",
    "tags": ["ключевое_слово1", "ключевое_слово2"],
    "urls": ["https://ссылка1.com", "https://ссылка2.com"]
  },
  "error": {
    "code": "string",
    "message": "string"
  }
}

Правила:
- Ответ ТОЛЬКО валидный JSON;
- Не используй markdown, комментарии или пояснения;
- Все поля присутствуют всегда;
- Если status = "ok", поле error = null;
- Если status = "error", поле data = null;
- tags - массив ключевых слов (максимум 5), если нет - пустой массив [];
- urls - массив полезных ссылок, если нет - пустой массив [];
- Типы данных должны соблюдаться;
  
Пример корректного ответа:
{
  "status": "ok",
  "data": {
    "title": "О языке Kotlin",
    "message": "Kotlin - современный язык программирования для JVM, Android и других платформ.",
    "tags": ["kotlin", "программирование", "android", "jvm"],
    "urls": ["https://kotlinlang.org", "https://developer.android.com/kotlin"]
  },
  "error": null
}

Если запрос некорректен:
{
  "status": "error",
  "data": null,
  "error": {
    "code": "INVALID_INPUT",
    "message": "Описание ошибки"
  }
}
""".trimIndent()
    
    init {
        // Добавляем системный промпт в начале диалога
        conversationHistory.add(ChatMessage(role = "system", content = systemPrompt))
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

            try {
                val request = ChatRequest(
                    messages = conversationHistory.toList()
                )

                val response = RetrofitInstance.api.sendMessage(request)
                
                if (response.choices.isNotEmpty()) {
                    val rawResponse = response.choices[0].message.content
                    
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
                                        urls = data.urls
                                    )
                                    _messages.value = _messages.value + aiMessage
                                } else {
                                    // Fallback если data = null
                                    val aiMessage = Message(
                                        text = rawResponse,
                                        isFromUser = false
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
                                    isError = true
                                )
                                _messages.value = _messages.value + aiMessage
                            }
                            else -> {
                                // Неизвестный статус
                                val aiMessage = Message(
                                    text = rawResponse,
                                    isFromUser = false
                                )
                                _messages.value = _messages.value + aiMessage
                            }
                        }
                    } else {
                        // Не удалось распарсить JSON - показываем как есть
                        val aiMessage = Message(
                            text = rawResponse,
                            isFromUser = false
                        )
                        _messages.value = _messages.value + aiMessage
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
                
                // Показываем ошибку в чате
                val errorMsg = Message(
                    text = "Извините, произошла ошибка при отправке сообщения. Попробуйте снова.",
                    isFromUser = false,
                    isError = true
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
}
