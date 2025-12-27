package com.example.iot_app.data.remote.api

import android.content.Context
import com.example.iot_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val tokenManager = TokenManager(context.applicationContext)

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()

        val request = chain.request().newBuilder().apply {
            if (!token.isNullOrEmpty()) {
                addHeader("Authorization", "Bearer $token")
            }
        }.build()

        return chain.proceed(request)
    }
}
