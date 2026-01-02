package com.example.iot_app.data.remote.api

import android.content.Context
import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.dto.RefreshTokenRequest
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route

class TokenAuthenticator(
    private val context: Context,
    private val tokenManager: TokenManager
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        // lấy Refresh Token từ bộ nhớ
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            return null // Không có token để refreshddalog out
        }

        // tránh lặp
        if (responseCount(response) >= 2) {
            return null
        }

        return try {
            val apiService = RetrofitClient.getInstance(context)
            val refreshCall = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            val refreshResponse = refreshCall.execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()
                if (body != null && body.success) {
                    // 5. Lưu token mới vào máy
                    val newAccessToken = body.data.accessToken
                    val newRefreshToken = body.data.refreshToken
                    tokenManager.saveTokens(newAccessToken, newRefreshToken)

                    // tạo lại request cũ với header mới
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    null // Server trả về lỗi logic
                }
            } else {
                // Refresh token hết hạn hoặc không hợpthì  lệ óa data để user đăng nhập lại
                tokenManager.clear()
                null
            }
        } catch (e: Exception) {
            // Lỗi mạng hoặc lỗi khác
            null
        }
    }

    // Hàm đếm số lần retry để tránh lặp vô tận
    private fun responseCount(response: Response): Int {
        var result = 1
        var prior = response.priorResponse
        while (prior != null) {
            result++
            prior = prior.priorResponse
        }
        return result
    }
}