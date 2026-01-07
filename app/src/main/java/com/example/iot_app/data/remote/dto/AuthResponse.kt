package com.example.iot_app.data.remote.dto

data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T
)

data class AuthData(
    val accessToken: String,
    val refreshToken: String,
    val userId: Int,
    val username: String,
    val role: String
)

