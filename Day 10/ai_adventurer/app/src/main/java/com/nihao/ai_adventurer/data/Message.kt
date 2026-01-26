package com.nihao.ai_adventurer.data

data class Message(
    val id: String = java.util.UUID.randomUUID().toString(),
    val text: String,
    val isFromUser: Boolean,
    val timestamp: Long = System.currentTimeMillis(),
    val title: String? = null, // Для структурированных ответов от AI
    val isError: Boolean = false, // Флаг ошибки
    val tags: List<String> = emptyList(), // Ключевые слова
    val urls: List<String> = emptyList(), // Полезные ссылки
    val responseTimeMs: Long? = null, // Время ответа в миллисекундах (только для AI сообщений)
    val isActive: Boolean = true, // TRUE when message has not been summarized
    val dbId: Long? = null // Database ID for tracking
) {
    /**
     * Возвращает время ответа в удобочитаемом формате
     */
    fun getResponseTimeFormatted(): String? {
        return responseTimeMs?.let { ms ->
            when {
                ms < 1000 -> "${ms}мс"
                ms < 60000 -> String.format("%.1fс", ms / 1000.0)
                else -> String.format("%.1fмин", ms / 60000.0)
            }
        }
    }
}
