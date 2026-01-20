package com.nihao.ai_adventurer.config

/**
 * Модель для системного промпта
 */
data class PromptItem(
    val id: String,
    val name: String,
    val description: String,
    val content: String
)

/**
 * Системные промпты для настройки поведения LLM
 */
object SystemPrompts {
    
    /**
     * Основной системный промпт для структурированных JSON ответов
     */
    const val JSON_RESPONSE_PROMPT = """
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
"""
    
    /**
     * Альтернативный промпт с более свободным форматом
     * (можно переключаться между промптами)
     */
    const val SIMPLE_RESPONSE_PROMPT = """
Ты — полезный AI ассистент.
Отвечай кратко и по делу.
Будь дружелюбным и помогай пользователю.
"""

    /**
     * Альтернативный промпт с более свободным форматом
     * (можно переключаться между промптами)
     */
    const val QUESTIONS_RESPONSE_PROMPT = """
Ты — экспертный ассистент.

Твоя задача — отвечать на вопросы пользователя максимально точно.
Чтобы это сделать, ты ОБЯЗАН:

1. Определить основной вопрос пользователя.
2. Определить, каких данных не хватает для точного ответа.
3. Сформулировать уточняющие вопросы ТОЛЬКО по этим данным.
4. НЕ отвечать на исходный вопрос до получения ответов.

Правила:
- Вопросы должны быть конкретными и проверяемыми.
- Не делай предположений за пользователя.
- Задавай по 1 вопрсосу за раз.
- Если информации достаточно — сразу отвечай на вопрос.
"""

    /**
     * Пустой системный промт
     */
    const val EMPTY_SYSTEM_PROMPT = """
"""

    /**
     * Список всех доступных промптов
     */
    val allPrompts = listOf(
        PromptItem(
            id = "json_response",
            name = "JSON Ответы",
            description = "Структурированные ответы в JSON формате с тегами и ссылками",
            content = JSON_RESPONSE_PROMPT
        ),
        PromptItem(
            id = "simple_response",
            name = "Простые Ответы",
            description = "Дружелюбный ассистент с краткими ответами",
            content = SIMPLE_RESPONSE_PROMPT
        ),
        PromptItem(
            id = "questions_response",
            name = "Уточняющие Вопросы",
            description = "Задает уточняющие вопросы для точного ответа",
            content = QUESTIONS_RESPONSE_PROMPT
        ),
        PromptItem(
            id = "empty",
            name = "Пустой системный промт",
            description = "пустой промт",
            content = EMPTY_SYSTEM_PROMPT
        )
    )
    
    /**
     * Получить промпт по ID
     */
    fun getPromptById(id: String): PromptItem? {
        return allPrompts.find { it.id == id }
    }
}
