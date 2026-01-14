package com.example.iot_app.data.remote.dto

data class ResetPasswordRequest(
    val email: String,
    val otp: String,
    val newPassword: String
)
