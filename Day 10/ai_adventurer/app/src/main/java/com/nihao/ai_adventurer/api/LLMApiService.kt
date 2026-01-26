package com.nihao.ai_adventurer.api

import com.nihao.ai_adventurer.data.ChatRequest
import com.nihao.ai_adventurer.data.ChatResponse
import com.nihao.ai_adventurer.data.ModelsResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST

interface LLMApiService {
    @Headers("Content-Type: application/json")
    @POST("v1/chat/completions")
    suspend fun sendMessage(@Body request: ChatRequest): ChatResponse
    
    @Headers("Content-Type: application/json")
    @GET("v1/models")
    suspend fun getModels(): ModelsResponse
}
