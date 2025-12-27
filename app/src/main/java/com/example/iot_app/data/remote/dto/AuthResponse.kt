package com.example.iot_app.data.remote.dto

data class AuthResponse(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Bearer"
)

