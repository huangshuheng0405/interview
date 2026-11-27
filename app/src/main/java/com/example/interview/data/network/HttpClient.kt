package com.example.interview.data.network

import android.util.Log
import com.example.interview.data.model.ApiResponse
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

object HttpClient {
    private const val BASE_URL = "https://api-harmony-teach.itheima.net/hm/"
    private const val TAG = "InterviewTong"

    var token = ""

    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(100, TimeUnit.SECONDS)
        .readTimeout(100, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .header("User-Agent", "Mozilla/5.0")  // ← 覆盖默认 okhttp UA
            if (token.isNotEmpty()) {
                request.addHeader("Authorization", "Bearer $token")
            }
            chain.proceed(request.build())
        }
        .addInterceptor(
            HttpLoggingInterceptor { message ->
                Log.d(TAG, message)
            }.setLevel(HttpLoggingInterceptor.Level.BODY)
        )
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("applicaton/json".toMediaType()))
        .build()

    val api: ApiService = retrofit.create(ApiService::class.java)

    suspend fun <T> request(call: suspend () -> ApiResponse<T>): T {
        val response = call()
        if (response.code == 10000) {
            return response.data ?: throw Exception("数据为空")
        }
        throw Exception(response.message ?: "请求失败")
    }
}