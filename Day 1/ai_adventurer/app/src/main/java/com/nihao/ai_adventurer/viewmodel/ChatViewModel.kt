package com.nihao.ai_adventurer.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nihao.ai_adventurer.api.RetrofitInstance
import com.nihao.ai_adventurer.data.ChatMessage
import com.nihao.ai_adventurer.data.ChatRequest
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
                    val assistantMessage = response.choices[0].message.content
                    
                    // Добавляем ответ в историю
                    conversationHistory.add(ChatMessage(role = "assistant", content = assistantMessage))
                    
                    // Добавляем ответ в UI
                    val aiMessage = Message(text = assistantMessage, isFromUser = false)
                    _messages.value = _messages.value + aiMessage
                }
            } catch (e: Exception) {
                _errorMessage.value = "Ошибка: ${e.message}"
                
                // Показываем ошибку в чате
                val errorMsg = Message(
                    text = "Извините, произошла ошибка при отправке сообщения. Попробуйте снова.",
                    isFromUser = false
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
}
