package com.nihao.ai_adventurer.api

import com.nihao.ai_adventurer.BuildConfig
import com.nihao.ai_adventurer.data.LLMProvider
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    // API ключи читаются из local.properties
    private const val DEEPSEEK_API_KEY = BuildConfig.DEEPSEEK_API_KEY
    private const val MISTRAL_API_KEY = BuildConfig.MISTRAL_API_KEY

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    /**
     * Создает OkHttpClient с авторизацией для указанного провайдера
     */
    private fun createOkHttpClient(provider: LLMProvider): OkHttpClient {
        val apiKey = when (provider) {
            LLMProvider.DEEPSEEK -> DEEPSEEK_API_KEY
            LLMProvider.MISTRAL -> MISTRAL_API_KEY
        }
        
        val authInterceptor = Interceptor { chain ->
            val originalRequest = chain.request()
            val requestWithAuth = originalRequest.newBuilder()
                .header("Authorization", "Bearer $apiKey")
                .build()
            chain.proceed(requestWithAuth)
        }

        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(authInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(240, TimeUnit.SECONDS)
            .writeTimeout(120, TimeUnit.SECONDS)
            .build()
    }

    /**
     * Создает Retrofit instance для указанного провайдера
     */
    private fun createRetrofit(provider: LLMProvider): Retrofit {
        return Retrofit.Builder()
            .baseUrl(provider.baseUrl)
            .client(createOkHttpClient(provider))
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Кэш API сервисов для каждого провайдера
     */
    private val apiCache = mutableMapOf<LLMProvider, LLMApiService>()

    /**
     * Получить API сервис для указанного провайдера
     */
    fun getApi(provider: LLMProvider): LLMApiService {
        return apiCache.getOrPut(provider) {
            createRetrofit(provider).create(LLMApiService::class.java)
        }
    }

    /**
     * Обратная совместимость - API для DeepSeek по умолчанию
     */
    @Deprecated("Use getApi(provider) instead", ReplaceWith("getApi(LLMProvider.DEEPSEEK)"))
    val api: LLMApiService
        get() = getApi(LLMProvider.DEEPSEEK)
}
