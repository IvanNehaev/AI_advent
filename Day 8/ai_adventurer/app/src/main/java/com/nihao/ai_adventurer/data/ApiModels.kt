package com.nihao.ai_adventurer.data

import com.google.gson.annotations.SerializedName

// DeepSeek API models (OpenAI-compatible)
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 3500,
    val temperature: Float = 1.0f
)

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>,
    val usage: Usage? = null
)

data class Choice(
    val message: ChatMessage,
    @SerializedName("finish_reason")
    val finishReason: String
)

data class Usage(
    @SerializedName("prompt_tokens")
    val promptTokens: Int = 0,
    @SerializedName("completion_tokens")
    val completionTokens: Int = 0,
    @SerializedName("total_tokens")
    val totalTokens: Int = 0
)

// Models List API Response
data class ModelsResponse(
    val data: List<ModelInfo>
)

data class ModelInfo(
    val id: String,
    val created: Long? = null,
    val owned_by: String? = null
)
