package com.nihao.ai_adventurer.util

import com.nihao.ai_adventurer.data.ChatMessage

/**
 * Утилита для локальной оценки количества токенов
 * Использует приблизительный подсчет на основе символов
 */
object TokenEstimator {
    
    /**
     * Оценивает количество токенов в тексте
     * 
     * Приблизительные правила:
     * - Латинские символы: ~4 символа = 1 токен
     * - Кириллица: ~2 символа = 1 токен
     * - Пробелы и пунктуация учитываются
     * 
     * @param text Текст для оценки
     * @return Приблизительное количество токенов
     */
    fun estimateTokens(text: String): Int {
        if (text.isEmpty()) return 0
        
        // Считаем латинские и кириллические символы отдельно
        var latinChars = 0
        var cyrillicChars = 0
        var otherChars = 0
        
        text.forEach { char ->
            when {
                char in 'A'..'Z' || char in 'a'..'z' -> latinChars++
                char in 'А'..'Я' || char in 'а'..'я' || char == 'ё' || char == 'Ё' -> cyrillicChars++
                else -> otherChars++
            }
        }
        
        // Приблизительный подсчет токенов
        // Латиница: 4 символа = 1 токен
        // Кириллица: 2 символа = 1 токен (более плотная)
        // Остальные: 3 символа = 1 токен
        val latinTokens = (latinChars / 4.0).toInt()
        val cyrillicTokens = (cyrillicChars / 2.0).toInt()
        val otherTokens = (otherChars / 3.0).toInt()
        
        // Минимум 1 токен для непустого текста
        return maxOf(1, latinTokens + cyrillicTokens + otherTokens)
    }
    
    /**
     * Оценивает количество токенов для одного сообщения
     * Включает небольшой overhead на форматирование (роль + разделители)
     * 
     * @param message Сообщение для оценки
     * @return Приблизительное количество токенов
     */
    fun estimateMessageTokens(message: ChatMessage): Int {
        // Базовый overhead на форматирование сообщения (роль + структура)
        val formatOverhead = 4
        
        // Токены из контента
        val contentTokens = estimateTokens(message.content)
        
        return contentTokens + formatOverhead
    }
    
    /**
     * Оценивает общее количество токенов для списка сообщений
     * 
     * @param messages Список сообщений
     * @return Приблизительное количество токенов
     */
    fun estimateConversationTokens(messages: List<ChatMessage>): Int {
        if (messages.isEmpty()) return 0
        
        // Базовый overhead на весь запрос
        val requestOverhead = 3
        
        // Суммируем токены всех сообщений
        val totalMessageTokens = messages.sumOf { estimateMessageTokens(it) }
        
        return totalMessageTokens + requestOverhead
    }
    
    /**
     * Оценивает токены промпта (все сообщения кроме последнего ответа ассистента)
     * 
     * @param messages Список сообщений
     * @return Приблизительное количество промпт-токенов
     */
    fun estimatePromptTokens(messages: List<ChatMessage>): Int {
        if (messages.isEmpty()) return 0
        
        // Промпт токены - это все сообщения до последнего ответа
        // Обычно это системный промпт + история + текущий запрос пользователя
        return estimateConversationTokens(messages)
    }
    
    /**
     * Оценивает токены ответа (completion)
     * 
     * @param responseText Текст ответа от модели
     * @return Приблизительное количество токенов ответа
     */
    fun estimateCompletionTokens(responseText: String): Int {
        return estimateTokens(responseText)
    }
}
