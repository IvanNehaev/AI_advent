package com.nihao.ai_adventurer.data

import com.google.gson.annotations.SerializedName

// DeepSeek API models (OpenAI-compatible)
data class ChatRequest(
    val model: String = "deepseek-chat",
    val messages: List<ChatMessage>,
    @SerializedName("max_tokens")
    val maxTokens: Int = 1500,
    val temperature: Float = 1.0f
)

data class ChatMessage(
    val role: String, // "user" or "assistant"
    val content: String
)

data class ChatResponse(
    val id: String,
    val choices: List<Choice>
)

data class Choice(
    val message: ChatMessage,
    @SerializedName("finish_reason")
    val finishReason: String
)
