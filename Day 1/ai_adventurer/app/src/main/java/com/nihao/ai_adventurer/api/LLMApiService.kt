package com.nihao.ai_adventurer.api

import com.nihao.ai_adventurer.data.ChatRequest
import com.nihao.ai_adventurer.data.ChatResponse
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface LLMApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
}
