package com.example.iot_app.data.remote.api


import com.example.iot_app.data.remote.dto.AuthResponse
import com.example.iot_app.data.remote.dto.ForgotPasswordRequest
import com.example.iot_app.data.remote.dto.LoginRequest
import com.example.iot_app.data.remote.dto.ResetPasswordRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): Response<AuthResponse>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>
}
