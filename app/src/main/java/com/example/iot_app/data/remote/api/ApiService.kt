package com.example.iot_app.data.remote.api


import com.example.iot_app.data.remote.dto.ApiResponse
import com.example.iot_app.data.remote.dto.AuthData
import com.example.iot_app.data.remote.dto.ChangePasswordRequest
import com.example.iot_app.data.remote.dto.ForgotPasswordRequest
import com.example.iot_app.data.remote.dto.LoginRequest
import com.example.iot_app.data.remote.dto.LogoutRequest
import com.example.iot_app.data.remote.dto.RegisterRequest
import com.example.iot_app.data.remote.dto.ResetPasswordRequest
import com.example.iot_app.data.remote.dto.UpdateProfileRequest
import com.example.iot_app.data.remote.dto.UserProfileDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): ApiResponse<Unit>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): ApiResponse<AuthData>

    @POST("api/auth/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<Unit>

    @POST("api/auth/reset-password")
    suspend fun resetPassword(
        @Body request: ResetPasswordRequest
    ): Response<Unit>

    @POST("api/auth/logout")
    suspend fun logout(@Body body: LogoutRequest): Response<Unit>

    @GET("api/user/me")
    suspend fun getMyProfile(): UserProfileDto

    @PUT("api/user/me")
    suspend fun updateMyProfile(
        @Body body: UpdateProfileRequest
    ): UserProfileDto

    @POST("api/auth/change-password")
    suspend fun changePassword(
        @Body body: ChangePasswordRequest
    )


}
