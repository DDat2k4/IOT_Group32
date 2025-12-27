package com.example.iot_app.data.remote.repository

import com.example.iot_app.data.local.TokenManager
import com.example.iot_app.data.remote.api.ApiService
import com.example.iot_app.data.remote.dto.ForgotPasswordRequest
import com.example.iot_app.data.remote.dto.LoginRequest
import com.example.iot_app.data.remote.dto.ResetPasswordRequest

class AuthRepository(
    private val api: ApiService,
    private val tokenManager: TokenManager
) {

    suspend fun login(username: String, password: String): Result<Unit> {
        return try {
            val response = api.login(LoginRequest(username, password))
            if (response.isSuccessful) {
                val body = response.body()
                if (body != null) {
                    tokenManager.saveToken(body.accessToken)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Không có dữ liệu trả về"))
                }
            } else {
                Result.failure(Exception("Sai tài khoản hoặc mật khẩu"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
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