package com.nihao.ai_adventurer.data

/**
 * Перечисление поддерживаемых LLM провайдеров
 */
enum class LLMProvider(
    val displayName: String,
    val baseUrl: String,
    val description: String
) {
    DEEPSEEK(
        displayName = "DeepSeek",
        baseUrl = "https://api.deepseek.com/",
        description = "DeepSeek AI - мощная модель для диалога и рассуждений"
    ),
    MISTRAL(
        displayName = "Mistral AI",
        baseUrl = "https://api.mistral.ai/",
        description = "Mistral AI - европейская open-source модель"
    );
    
    companion object {
        /**
         * Получить модели по умолчанию для провайдера
         */
        fun getDefaultModels(provider: LLMProvider): List<ModelInfo> {
            return when (provider) {
                DEEPSEEK -> listOf(
                    ModelInfo(id = "deepseek-chat"),
                    ModelInfo(id = "deepseek-reasoner")
                )
                MISTRAL -> listOf(
                    ModelInfo(id = "mistral-small-latest"),
                    ModelInfo(id = "mistral-medium-latest"),
                    ModelInfo(id = "mistral-large-latest"),
                    ModelInfo(id = "open-mistral-7b"),
                    ModelInfo(id = "open-mixtral-8x7b"),
                    ModelInfo(id = "open-mixtral-8x22b")
                )
            }
        }
        
        /**
         * Получить модель по умолчанию для провайдера
         */
        fun getDefaultModelId(provider: LLMProvider): String {
            return when (provider) {
                DEEPSEEK -> "deepseek-chat"
                MISTRAL -> "mistral-small-latest"
            }
        }
    }
}
