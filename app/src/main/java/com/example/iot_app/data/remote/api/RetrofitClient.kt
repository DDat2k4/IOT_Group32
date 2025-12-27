package com.example.iot_app.data.remote.api

import android.content.Context
import com.example.iot_app.data.local.TokenManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:8080/"
    private var apiService: ApiService? = null

    fun getInstance(context: Context): ApiService {
        if (apiService == null) {
            val client = OkHttpClient.Builder()
                .addInterceptor(AuthInterceptor(context)) // Dùng context để khởi tạo TokenManager bên trong
                .build()

            apiService = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
        return apiService!!
    }
}