package com.spandey.edugramproject.network

import com.spandey.edugramproject.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

// --- Data Models for Gemini API ---
data class GeminiContent(
    val role: String? = null,
    val parts: List<GeminiPart>
)

data class GeminiPart(
    val text: String
)

data class GeminiRequest(
    val contents: List<GeminiContent>
)

data class GeminiResponse(
    val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    val content: GeminiContent?
)

// --- Retrofit Interface for Gemini ---
interface AIService {
    // ✅ CORRECTED: Use 'gemini-1.5-flash' in the path for the v1beta endpoint
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun getChatCompletion(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
    companion object {
        fun create(): AIService {
            val logging = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) {
                    HttpLoggingInterceptor.Level.BODY
                } else {
                    HttpLoggingInterceptor.Level.NONE
                }
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl("https://generativelanguage.googleapis.com/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(AIService::class.java)
        }
    }
}

/*
 * OTHER WORKING MODELS:
 *
 * Gemini 1.5 Pro (slower but smarter):
 * @POST("v1beta/models/gemini-1.5-pro-latest:generateContent")
 *
 * Gemini 1.0 Pro (most stable, uses v1 not v1beta):
 * @POST("v1/models/gemini-pro:generateContent")
 */