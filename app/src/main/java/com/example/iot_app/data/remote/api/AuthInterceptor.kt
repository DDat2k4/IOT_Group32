package com.example.iot_app.data.remote.api

import android.content.Context
import com.example.iot_app.data.local.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {
    private val tokenManager = TokenManager(context)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        val requestUrl = originalRequest.url.toString()
        // những api này thì k cần acesstoken
        if (requestUrl.contains("/auth/login") ||
            requestUrl.contains("/auth/register") ||
            requestUrl.contains("/auth/refresh-token")) {
            return chain.proceed(originalRequest)
        }

        // Các API khác thì gắn token bình thường
        val token = tokenManager.getToken()
        val requestBuilder = originalRequest.newBuilder()

        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}