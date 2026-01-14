package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.AuthData
import com.example.iot_app.data.remote.dto.ForgotPasswordRequest
import com.example.iot_app.data.remote.dto.LoginRequest
import com.example.iot_app.data.remote.dto.LogoutRequest
import com.example.iot_app.data.remote.dto.RegisterRequest
import com.example.iot_app.data.remote.dto.ResetPasswordRequest

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {
    suspend fun login(username: String, password: String): Result<AuthData> {
        return runCatching {
            val response = api.login(LoginRequest(username, password))
            if (response.success) {
                // Lấy token từ response.data
                tokenManager.saveTokens(response.data.accessToken, response.data.refreshToken)
                response.data
            } else {
                throw Exception(response.message)
            }
        }
    }
    suspend fun register(request: RegisterRequest): Result<String> {
        return runCatching {
            val response = api.register(request)
            if (response.success) "Đăng ký thành công" else throw Exception(response.message)
        }
    }

    suspend fun logout() {
        runCatching {
            val refresh = tokenManager.getRefreshToken() ?: ""
            api.logout(LogoutRequest(refresh))
        }
        tokenManager.clear() // Luôn xóa sạch máy
    }



    suspend fun forgotPassword(email: String): Result<Unit> {
        return try {
            val response = api.forgotPassword(ForgotPasswordRequest(email))
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("Email không tồn tại"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun resetPassword(
        email: String,
        otp: String,
        newPassword: String
    ): Result<Unit> {
        return try {
            val response = api.resetPassword(
                ResetPasswordRequest(email, otp, newPassword)
            )
            if (response.isSuccessful) Result.success(Unit)
            else Result.failure(Exception("OTP không đúng hoặc hết hạn"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}