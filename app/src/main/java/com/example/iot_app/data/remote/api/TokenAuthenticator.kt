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

    // xếp hàng luồng, k chạy song song để refresh token k lỗi
    @Synchronized
    override fun authenticate(route: Route?, response: Response): Request? {
        if (response.code != 401) return null

        //lấy token đã lưu
        val currentAccessToken = tokenManager.getToken()

        // nếu token request khác token trong máy thì k cần refresh nữa vì có reqest đã yêu cầu token mới
        val requestToken = response.request.header("Authorization")?.replace("Bearer ", "")

        if (currentAccessToken != null && currentAccessToken != requestToken) {
            return response.request.newBuilder()
                .header("Authorization", "Bearer $currentAccessToken")
                .build()
        }

        // nếu giống thì refresh
        val refreshToken = tokenManager.getRefreshToken()
        if (refreshToken.isNullOrEmpty()) {
            return null // k có refreshtoken thì logout
        }

        // Tránh lặp vô tận
        if (responseCount(response) >= 2) {
            return null
        }

        return try {
            // Gọi API Refresh
            val apiService = RetrofitClient.getInstance(context)
            val refreshCall = apiService.refreshToken(RefreshTokenRequest(refreshToken))
            val refreshResponse = refreshCall.execute()

            if (refreshResponse.isSuccessful) {
                val body = refreshResponse.body()
                if (body != null && body.success) {
                    // Lưu token mới
                    val newAccessToken = body.data.accessToken
                    val newRefreshToken = body.data.refreshToken
                    tokenManager.saveTokens(newAccessToken, newRefreshToken)

                    // Tạo lại request cũ với header mới
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    null
                }
            } else {
                // Refresh token hết hạn thì xóa data để user đăng nhập lại
                tokenManager.clear()
                null
            }
        } catch (e: Exception) {
            null
        }
    }

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