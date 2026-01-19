package com.nihao.ai_adventurer.data

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String? = null, // Для структурированных ответов от AI
    val isError: Boolean = false, // Флаг ошибки
    val tags: List<String> = emptyList(), // Ключевые слова
    val urls: List<String> = emptyList() // Полезные ссылки
)
