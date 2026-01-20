package com.nihao.ai_adventurer.data

/**
 * Структурированный ответ от LLM в JSON формате
 */
data class LLMJsonResponse(
    val status: String, // "ok" или "error"
    val data: ResponseData?,
    val error: ResponseError?
)

data class ResponseData(
    val title: String,
    val message: String,
    val tags: List<String> = emptyList(), // Массив ключевых слов (до 5)
    val urls: List<String> = emptyList() // Массив полезных ссылок
)

data class ResponseError(
    val code: String,
    val message: String
)
